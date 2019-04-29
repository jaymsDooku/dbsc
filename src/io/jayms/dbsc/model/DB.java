package io.jayms.dbsc.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.jayms.dbsc.util.DBHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents database item.
 */
public class DB {
	
	@Getter private final int id;
	@Getter private final ConnectionConfig connConfig;
	@Getter @Setter private String serverName;
	@Getter @Setter private String databaseName;
	
	@Getter @Setter private int port;
	@Getter @Setter private String user;
	@Getter @Setter private String pass;
	
	@Getter @Setter private List<Report> reports;
	@Getter @Setter private DBType type;
	@Getter @Setter private File sqliteDBFile;
	@Setter private List<Table> tables = null;
	
	public DB(ConnectionConfig connConfig, String databaseName, int port, String user, String pass) {
		this(-1, connConfig, databaseName, DBType.ORACLE, port, user, pass, null, null);
	}
	
	public DB(ConnectionConfig connConfig, String databaseName, int port, String user, String pass, String serverName) {
		this(-1, connConfig, databaseName, DBType.SQL_SERVER, port, user, pass, serverName, null);
	}
	
	public DB(ConnectionConfig connConfig, String databaseName, File sqliteDBFile) {
		this(-1, connConfig, databaseName, DBType.SQLITE, -1, null, null, null, sqliteDBFile);
	}

	public DB(int id, ConnectionConfig connConfig, String databaseName, int port, String user, String pass) {
		this(id, connConfig, databaseName, DBType.ORACLE, port, user, pass, null, null);
	}
	
	public DB(int id, ConnectionConfig connConfig, String databaseName, int port, String user, String pass, String serverName) {
		this(id, connConfig, databaseName, DBType.SQL_SERVER, port, user, pass, serverName, null);
	}
	
	public DB(int id, ConnectionConfig connConfig, String databaseName, File sqliteDBFile) {
		this(id, connConfig, databaseName, DBType.SQLITE, -1, null, null, null, sqliteDBFile);
	}
	
	public DB(int id, ConnectionConfig connConfig, String databaseName, DBType type, int port, String user, String pass, String serverName, File sqliteDBFile) {
		this.id = id;
		this.connConfig = connConfig;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.databaseName = databaseName;
		this.type = type;
		if (type == DBType.SQLITE && sqliteDBFile == null) {
			throw new IllegalArgumentException("SQLite DBs must have their database file!");
		} else if (type == DBType.SQL_SERVER && serverName == null) {
			throw new IllegalArgumentException("SQLServer DBs must have a server name");
		}
		this.serverName = serverName;
		this.sqliteDBFile = sqliteDBFile;
		this.reports = new ArrayList<>();
	}
	
	public List<Table> getTables() {
		if (tables == null) {
			DBHelper dbHelper = connConfig.getMasterUI().getDbHelper();
			tables = dbHelper.fetchTables(this);
			System.out.println("fetched tables");
			System.out.println("tables");
			tables.stream().forEach(t -> {
				System.out.println(t.toString());
			});
		}
		return tables;
	}
	
	@Override
	public String toString() {
		String s = "{" + 
				"DBName = " + databaseName + "|" +
				"Reports = [";
		for (int i = 0; i < reports.size(); i++) {
			Report report = reports.get(i);
			s += report.toString();
			if (i < reports.size() - 1) {
				s += ", ";
			}
		}
		s += "]}";
		return s;
	}
}
