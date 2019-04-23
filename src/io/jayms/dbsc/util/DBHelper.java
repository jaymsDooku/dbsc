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
	
	public List<Table> fetchTables(DB db) {
		Database dbConn = dbMan.getDatabaseConnection(db);
		if (dbConn == null) {
			System.out.println("Couldn't grab a connection for this database, so failed to fetch tables!");
			return new ArrayList<>();
		}
		Connection conn = dbConn.getConnection();
		
		switch (db.getType()) {
		case SQLITE:
			return fetchSQLiteTables(conn, db);
		case SQL_SERVER:
			break;
		case ORACLE:
			return fetchOracleTables(conn, db);
		default:
			break;
		}
		
		return new ArrayList<>();
	}
	
	public void fetchTablesAsync(DB db, Consumer<List<Table>> cb) {
		CompletableFuture.supplyAsync(() -> fetchTables(db)).thenAccept(cb);
	}
	
	private List<Column> getColumns(ResultSetMetaData meta) throws SQLException {
		List<Column> columnSet = new ArrayList<>();
		
		int colCount = meta.getColumnCount();
		System.out.println("col count: " + colCount);
		for (int i = 1; i <= colCount; i++) {
			String colName = meta.getColumnName(i);
			int colType = meta.getColumnType(i);
			DataType colDataType = DataType.valueOf(colType);
			System.out.println("col name: " + colName);
			System.out.println("col type: " + colType);
			Column col = new Column(colName, colDataType);
			columnSet.add(col);
		}
		
		return columnSet;
	}

	private List<Table> fetchSQLiteTables(Connection conn, DB db) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet tables = stmt.executeQuery("SELECT * FROM sqlite_master WHERE type=\"table\"");
			
			List<Table> result = new ArrayList<>();

			while (tables.next()) {
				String tblName = tables.getString("tbl_name");
				String name = tables.getString("name");
				String sql = tables.getString("sql");
				
				System.out.println("tblName: " + tblName);
				System.out.println("name: " + name);
				System.out.println("sql: " + sql);
				
				stmt = conn.createStatement();
				ResultSet columns = stmt.executeQuery("SELECT * FROM " + tblName + " WHERE 1 < 0");
				ResultSetMetaData meta = columns.getMetaData();
				
				List<Column> columnSet = getColumns(meta);
				columns.close();
				
				stmt = conn.createStatement();
				ResultSet rowCountRS = stmt.executeQuery("SELECT COUNT(*) FROM " + tblName);
				
				int count = 0;
				if (rowCountRS.next()) {
					count = rowCountRS.getInt(1);
				}
				
				Table table = new Table(tblName, count, columnSet, db);
				result.add(table);
			}
			
			tables.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	private List<Table> fetchSQLServerTables(Connection conn, DB db) {
		return null;
	}
	
	private static final String SELECT_TBL_NAMES = "SELECT table_name FROM dba_tables WHERE owner LIKE ?";
	
	private List<Table> fetchOracleTables(Connection conn, DB db) {
		try {
			String schemaName = db.getDatabaseName().toUpperCase();
			PreparedStatement ps = conn.prepareStatement(SELECT_TBL_NAMES);
			ps.setString(1, schemaName);
			ResultSet rs = ps.executeQuery();
			List<String> tableNames = new ArrayList<>();
			while (rs.next()) {
				String tblName = rs.getString("table_name");
				if (tblName.startsWith("EXT_")) {
					continue;
				}
				tableNames.add(tblName);
			}
			rs.close();
			ps.close();
			
			System.out.print("tableNames: " + tableNames);
			List<Table> tables = new ArrayList<>();
			for (String tableName : tableNames) {
				String tblName = schemaName + "." + tableName;
				Statement stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM " + tblName + " WHERE 1 < 0");
				
				List<Column> columns = getColumns(rs.getMetaData());
				
				rs.close();
				stmt.close();
				
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tblName.toLowerCase());
				
				int rowCount = 0;
				if (rs.next()) {
					rowCount = rs.getInt(1);
				}
				
				rs.close();
				stmt.close();
				
				Table table = new Table(tableName, rowCount, columns, db);
				System.out.println("table: " + table);
				tables.add(table);
			}
			return tables;
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
