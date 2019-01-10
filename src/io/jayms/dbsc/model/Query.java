package io.jayms.dbsc.model;

import lombok.Getter;
import lombok.Setter;

public class Query {

	@Getter @Setter private int id;
	@Getter @Setter private String worksheetName;
	@Getter @Setter private String query;
	
	public Query(String worksheetName, String query) {
		this(-1, worksheetName, query);
	}
	
	public Query(int id, String worksheetName, String query) {
		this.id = id;
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
