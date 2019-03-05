package io.jayms.dbsc.model;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Getter;

public class Report {

	@Getter private final DB db;
	@Getter private String workbookName;
	@Getter private List<Query> queries;
	
	public Report(DB db, String workbookName, Query... queries) {
		this.db = db;
		this.workbookName = workbookName;
		this.queries = Lists.newArrayList(queries);
	}
	
	@Override
	public String toString() {
		String s = "{" + 
				"WorkbookName = " + workbookName + "|" +
				"Queries = [";
		Query[] queries = this.queries.toArray(new Query[0]);
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
