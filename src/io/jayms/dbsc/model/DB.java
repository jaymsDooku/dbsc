package io.jayms.dbsc.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.jayms.dbsc.util.DBHelper;
import lombok.Getter;
import lombok.Setter;

public class DB {
	
	@Getter private final ConnectionConfig connConfig;
	@Getter private String serverName;
	@Getter private String databaseName;
	@Getter @Setter private List<Report> reports;
	@Getter @Setter private DBType type;
	@Getter @Setter private File sqliteDBFile;
	@Setter private Set<Table> tables = null;
	
	public DB(ConnectionConfig connConfig, String databaseName, DBType type) {
		this(connConfig, databaseName, type, null, null);
	}
	
	public DB(ConnectionConfig connConfig, String databaseName, String serverName, DBType type) {
		this(connConfig, databaseName, type, serverName, null);
	}
	
	public DB(ConnectionConfig connConfig, String databaseName, File sqliteDBFile) {
		this(connConfig, databaseName, DBType.SQLITE, null, sqliteDBFile);
	}
	
	public DB(ConnectionConfig connConfig, String databaseName, DBType type, String serverName, File sqliteDBFile) {
		this.connConfig = connConfig;
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
	
	public Set<Table> getTables() {
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
