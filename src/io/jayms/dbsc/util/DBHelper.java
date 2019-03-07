package io.jayms.dbsc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import io.jayms.dbsc.DatabaseManager;
import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DataType;
import io.jayms.dbsc.model.Table;
import io.jayms.xlsx.db.Database;

public class DBHelper {

	private final DatabaseManager dbMan;
	
	public DBHelper(DatabaseManager dbMan) {
		this.dbMan = dbMan;
	}
	
	public Set<Table> fetchTables(DB db) {
		ConnectionConfig cc = db.getConnConfig();
		Database dbConn = dbMan.getDatabaseConnection(cc, db);
		if (dbConn == null) {
			System.out.println("Couldn't grab a connection for this database, so failed to fetch tables!");
			return new HashSet<>();
		}
		Connection conn = dbConn.getConnection();
		
		switch (db.getType()) {
		case SQLITE:
			return fetchSQLiteTables(conn, db);
		case SQL_SERVER:
			break;
		case ORACLE:
			break;
		default:
			break;
		}
		
		return new HashSet<>();
	}

	private Set<Table> fetchSQLiteTables(Connection conn, DB db) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet tables = stmt.executeQuery("SELECT * FROM sqlite_master WHERE type=\"table\"");
			
			ResultSetMetaData meta = tables.getMetaData();
			int colCount = meta.getColumnCount();
			System.out.println("col count: " + colCount);
			for (int i = 1; i <= colCount; i++) {
				System.out.println("col name: " + meta.getColumnName(i));
				System.out.println("col type: " + meta.getColumnType(i));
			}
			Set<Table> result = new HashSet<>();

			while (tables.next()) {
				String tblName= tables.getString("tbl_name");
				String name = tables.getString("name");
				String sql = tables.getString("sql");
				
				System.out.println("tblName: " + tblName);
				System.out.println("name: " + name);
				System.out.println("sql: " + sql);
				
				Statement colStmt = conn.createStatement();
				ResultSet columns = colStmt.executeQuery("SELECT * FROM " + tblName + " WHERE 1 < 0");
				
				Set<Column> columnSet = new HashSet<>();
				
				meta = columns.getMetaData();
				colCount = meta.getColumnCount();
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
				columns.close();
				
				Table table = new Table(tblName, columnSet);
				result.add(table);
			}
			
			tables.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return new HashSet<>();
		}
	}
	
}
