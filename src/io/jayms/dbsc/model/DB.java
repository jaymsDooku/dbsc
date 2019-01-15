package io.jayms.dbsc.model;

import java.io.File;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class DB {
	
	@Getter private String databaseName;
	@Getter private List<Report> reports;
	@Getter @Setter private DBType type;
	@Getter @Setter private File sqliteDBFile;
	
	public DB(String databaseName, DBType type, List<Report> reports) {
		this(databaseName, type, null, reports);
	}
	
	public DB(String databaseName, DBType type, File sqliteDBFile, List<Report> reports) {
		this.databaseName = databaseName;
		this.type = type;
		if (type == DBType.SQLITE && sqliteDBFile == null) {
			throw new IllegalArgumentException("SQLite DBs must have their database file!");
		}
		this.sqliteDBFile = sqliteDBFile;
		this.reports = reports;
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
