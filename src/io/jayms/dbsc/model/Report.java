package io.jayms.dbsc.model;

import lombok.Getter;
import lombok.Setter;

public class Report {

	@Getter @Setter private DB db;
	@Getter private String workbookName;
	@Getter private Query[] queries;
	
	public Report(String workbookName, Query... queries) {
		this.workbookName = workbookName;
		this.queries = queries;
	}
	
	@Override
	public String toString() {
		String s = "{" + 
				"WorkbookName = " + workbookName + "|" +
				"Queries = [";
		for (int i = 0; i < queries.length; i++) {
			Query query = queries[i];
			s += query.toString();
			if (i < queries.length - 1) {
				s += ", ";
			}
		}
		s += "]}";
		return s;
	}
}
