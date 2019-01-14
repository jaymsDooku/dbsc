package io.jayms.dbsc.model;

import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class DB {
	
	private String databaseName;
	private List<Report> reports;
	@Getter @Setter private Set<Table> tables;
	
	public DB(String databaseName, List<Report> reports, Set<Table> tables) {
		this.databaseName = databaseName;
		this.reports = reports;
		this.tables = tables;
	}
	
	public String databaseName() {
		return databaseName;
	}
	
	public List<Report> reports() {
		return reports;
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
