package io.jayms.dbsc.model;

public class Report {

	private String worksheetName;
	private String[] queries;
	
	public Report(String worksheetName, String... queries) {
		this.worksheetName = worksheetName;
		this.queries = queries;
	}
	
	public String worksheetName() {
		return worksheetName;
	}
	
	public String[] queries() {
		return queries;
	}
	
	@Override
	public String toString() {
		String s = "{" + 
				"WorksheetName = " + worksheetName + "|" +
				"Reports = [";
		for (int i = 0; i < queries.length; i++) {
			String query = queries[i];
			s += query.toString();
			if (i < queries.length - 1) {
				s += ", ";
			}
		}
		s += "]}";
		return s;
	}
}
