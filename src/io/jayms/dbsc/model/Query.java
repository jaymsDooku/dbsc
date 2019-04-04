package io.jayms.dbsc.model;

import java.util.Map;

import io.jayms.xlsx.model.FieldConfiguration;
import lombok.Getter;
import lombok.Setter;

public class Query {

	@Getter @Setter private Report report;
	@Getter @Setter private int id;
	@Getter @Setter private String worksheetName;
	@Getter @Setter private String query;
	
	@Getter @Setter private Map<String, FieldConfiguration> fieldConfigs;
	
	public Query(Report report, String worksheetName, String query) {
		this(-1, report, worksheetName, query);
	}
	
	public Query(int id, Report report, String worksheetName, String query) {
		this.id = id;
		this.report = report;
		this.worksheetName = worksheetName;
		this.query = query;
	}
	
	public boolean isEmpty() {
		return query == null || query.isEmpty();
	}
	
	@Override
	public String toString() {
		String s = "{" + 
				"WorksheetName = " + worksheetName + "|" +
				"Queries = " + query + "}";
		return s;
	}
}
