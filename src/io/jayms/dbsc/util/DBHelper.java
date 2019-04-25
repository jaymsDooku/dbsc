package io.jayms.dbsc.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.jayms.dbsc.db.DatabaseManager;
import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DataType;
import io.jayms.dbsc.model.Table;
import io.jayms.xlsx.db.Database;

public class DBHelper {

	private final DatabaseManager dbMan;
	
	public DBHelper(DatabaseManager dbMan) {
		this.dbMan = dbMan;
	}
	
	/**
	 * Fetch all tables from a database.
	 * @param db - database to fetch tables from.
	 * @return - Returns a list of found tables.
	 */
	public List<Table> fetchTables(DB db) {
		Database dbConn = dbMan.getDatabaseConnection(db);
		if (dbConn == null) { // Ensure connection.
			System.out.println("Couldn't grab a connection for this database, so failed to fetch tables!");
			return new ArrayList<>();
		}
		Connection conn = dbConn.getConnection();
		
		switch (db.getType()) { // Handle fetching according to the type of the database.
			case SQLITE:
				return fetchSQLiteTables(conn, db);
			case SQL_SERVER:
				return fetchSQLServerTables(conn, db);
			case ORACLE:
				return fetchOracleTables(conn, db);
			default:
				break;
		}
		return new ArrayList<>();
	}
	
	/**
	 * Fetch tables asynchronously.
	 * @param db - database to fetch from.
	 * @param cb - callback
	 */
	public void fetchTablesAsync(DB db, Consumer<List<Table>> cb) {
		CompletableFuture.supplyAsync(() -> fetchTables(db)).thenAccept(cb);
	}
	
	/**
	 * Get a list of fields/columns from the metadata of a ResultSet.
	 * @param meta - Metadata to retrieve fields/columns from.
	 * @return - A list of fields/columns found in a ResultSet.
	 * @throws SQLException
	 */
	private List<Column> getColumns(ResultSetMetaData meta) throws SQLException {
		List<Column> columnSet = new ArrayList<>();
		
		int colCount = meta.getColumnCount();
		for (int i = 1; i <= colCount; i++) { // Iterate over data in meta data,
			String colName = meta.getColumnName(i);
			int colType = meta.getColumnType(i);
			DataType colDataType = DataType.valueOf(colType);
			Column col = new Column(colName, colDataType); // instantiate Column using data,
			columnSet.add(col); // append to result.
		}
		
		return columnSet;
	}

	/**
	 * Handle fetching of tables for a SQLite database.
	 * @param conn - database connection to use.
	 * @param db - database item.
	 * @return - Returns a list of tables found in the database.
	 */
	private List<Table> fetchSQLiteTables(Connection conn, DB db) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet tables = stmt.executeQuery("SELECT tbl_name FROM sqlite_master WHERE type=\"table\""); // Query for all of the names of the tables in the SQLite DB. 
			
			List<Table> result = new ArrayList<>();

			while (tables.next()) { // For all of the table names found in the result set,
				String tblName = tables.getString("tbl_name");
				
				stmt = conn.createStatement();
				ResultSet columns = stmt.executeQuery("SELECT * FROM " + tblName + " WHERE 1 < 0"); // Query them with a 'scout' query to retrieve the column/field data of the table.
				ResultSetMetaData meta = columns.getMetaData();
				
				List<Column> columnSet = getColumns(meta); // Retrieve the column/field data.
				columns.close();
				
				stmt = conn.createStatement();
				ResultSet rowCountRS = stmt.executeQuery("SELECT COUNT(*) FROM " + tblName); // Find out how many rows are in the table.
				
				int count = 0;
				if (rowCountRS.next()) {
					count = rowCountRS.getInt(1); // Fetch row count.
				}
				
				Table table = new Table(tblName, count, columnSet, db); // Instantiate Table object using this data.
				result.add(table); // Append to result.
			}
			
			tables.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	private static final String SELECT_TBL_NAMES_SS = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE='BASE TABLE' AND TABLE_CATALOG=?";
	
	private List<Table> fetchSQLServerTables(Connection conn, DB db) {
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_TBL_NAMES_SS);
			ps.setString(1, db.getDatabaseName());
			
			ResultSet tables = ps.executeQuery();
			
			List<Table> result = new ArrayList<>();
			
			while (tables.next()) {
				String schema = tables.getString("TABLE_SCHEMA");
				String tblName = tables.getString("TABLE_NAME");
				String tbl = schema + "." + tblName;
				
				ResultSet columns = conn.createStatement().executeQuery("SELECT * FROM " + tbl + " WHERE 1 < 0");
				ResultSetMetaData meta = columns.getMetaData();
				
				List<Column> columnSet = getColumns(meta);
				columns.close();
				
				ResultSet countRS = conn.createStatement().executeQuery("SELECT COUNT(*) FROM " + tbl);
				int count = 0;
				if (countRS.next()) {
					count = countRS.getInt(1);
				}
				
				Table table = new Table(tbl, count, columnSet, db);
				result.add(table);
			}
			
			tables.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	/**
	 * Query to select all of the names of the tables in the oracle schema.
	 */
	private static final String SELECT_TBL_NAMES_ORA = "SELECT table_name FROM dba_tables WHERE owner LIKE ?";
	
	/**
	 * Handle fetching of tables for a Oracle database.
	 * @param conn - database connection to use.
	 * @param db - database item.
	 * @return - Returns a list of tables found in the database.
	 */
	private List<Table> fetchOracleTables(Connection conn, DB db) {
		try {
			String schemaName = db.getDatabaseName().toUpperCase(); // Get name of oracle schema.
			PreparedStatement ps = conn.prepareStatement(SELECT_TBL_NAMES_ORA); // Select all of the names of the tables in the oracle schema.
			ps.setString(1, schemaName);
			ResultSet rs = ps.executeQuery();
			List<String> tableNames = new ArrayList<>();
			while (rs.next()) {
				String tblName = rs.getString("table_name");
				if (tblName.startsWith("EXT_")) { // Do not include external tables or views (conventionally prefixed with 'EXT_').
					continue;
				}
				tableNames.add(tblName);
			}
			rs.close();
			ps.close();
			
			List<Table> tables = new ArrayList<>();
			for (String tableName : tableNames) {
				String tblName = schemaName + "." + tableName;
				Statement stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM " + tblName + " WHERE 1 < 0"); // Query to retrieve column/field data from table.
				
				List<Column> columns = getColumns(rs.getMetaData()); // Retrieve column/field data from result set meta data.
				
				rs.close();
				stmt.close();
				
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tblName.toLowerCase()); // Calculate row count.
				
				int rowCount = 0;
				if (rs.next()) {
					rowCount = rs.getInt(1); // Fetch row count.
				}
				
				rs.close();
				stmt.close();
				
				Table table = new Table(tableName, rowCount, columns, db); // Instantiate Table object using this data. 
				tables.add(table);
			}
			return tables;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
