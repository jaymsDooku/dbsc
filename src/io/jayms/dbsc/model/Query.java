package io.jayms.dbsc.model;

import lombok.Getter;
import lombok.Setter;

public class Query {

	@Getter @Setter private Report report;
	@Getter @Setter private int id;
	@Getter @Setter private String worksheetName;
	@Getter @Setter private String query;
	
	public Query(Report report, String worksheetName, String query) {
		this(-1, report, worksheetName, query);
	}
	
	public Query(int id, Report report, String worksheetName, String query) {
		this.id = id;
		this.report = report;
		this.worksheetName = worksheetName;
		this.query = query;
	}
	
	@Override
	public String toString() {
		String s = "{" + 
				"WorksheetName = " + worksheetName + "|" +
				"Queries = " + query + "}";
		return s;
	}
}
