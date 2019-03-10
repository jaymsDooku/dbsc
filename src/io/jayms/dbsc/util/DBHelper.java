package io.jayms.dbsc.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.jayms.dbsc.DatabaseManager;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
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
	
	public void fetchTablesAsync(DB db, Consumer<Set<Table>> cb) {
		CompletableFuture.supplyAsync(() -> fetchTables(db)).thenAccept(cb);
	}

	private Set<Table> fetchSQLiteTables(Connection conn, DB db) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet tables = stmt.executeQuery("SELECT * FROM " + db.getDatabaseName() + ".sqlite_master");
			
			ResultSetMetaData meta = tables.getMetaData();
			int colCount = meta.getColumnCount();
			System.out.println("col count: " + colCount);
			for (int i = 0; i < colCount; i++) {
				System.out.println("col name: " + meta.getColumnName(i));
			}
			Set<Table> result = new HashSet<>();

			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return new HashSet<>();
		}
	}
	
	private Set<Table> fetchSQLServerTables(Connection conn, DB db) {
		return null;
	}
	
	private Set<Table> fetchOracleTables(Connection conn, DB db) {
		return null;
	}
}
