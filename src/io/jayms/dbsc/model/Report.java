package io.jayms.dbsc.model;

import java.util.List;

import com.google.common.collect.Lists;

import io.jayms.xlsx.model.DoubleBandFormat;
import lombok.Getter;

public class Report {

	@Getter private final int id;
	@Getter private final DB db;
	@Getter private String workbookName;
	@Getter private DoubleBandFormat doubleBandFormat;
	@Getter private List<Query> queries;
	
	public Report(DB db, String workbookName, DoubleBandFormat doubleBandFormat, Query... queries) {
		this(-1, db, workbookName, doubleBandFormat, queries);
	}
	
	public Report(int id, DB db, String workbookName, DoubleBandFormat doubleBandFormat, Query... queries) {
		this.id = id;
		this.db = db;
		this.workbookName = workbookName;
		this.doubleBandFormat = doubleBandFormat;
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
