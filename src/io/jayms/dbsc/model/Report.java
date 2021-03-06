package io.jayms.dbsc.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import io.jayms.xlsx.model.WorksheetDescriptor;
import lombok.Getter;
import lombok.Setter;

public class Report {

	@Getter private final int id;
	@Getter private final DB db;
	@Getter @Setter private String workbookName;
	@Getter @Setter private DoubleBandFormatHolder doubleBandFormat;
	@Getter @Setter private StyleHolder titleStyle;
	@Getter @Setter private StyleHolder subTotalStyle;
	@Getter @Setter private List<Query> queries;
	
	public Report(DB db, String workbookName, DoubleBandFormatHolder doubleBandFormat, StyleHolder titleStyle, StyleHolder subTotalStyle, Query... queries) {
		this(-1, db, workbookName, doubleBandFormat, titleStyle, subTotalStyle, queries);
	}
	
	public Report(int id, DB db, String workbookName, DoubleBandFormatHolder doubleBandFormat, StyleHolder titleStyle, StyleHolder subTotalStyle, Query... queries) {
		this.id = id;
		this.db = db;
		this.workbookName = workbookName;
		this.doubleBandFormat = doubleBandFormat;
		this.titleStyle = titleStyle;
		this.subTotalStyle = subTotalStyle;
		this.queries = Lists.newArrayList(queries);
	}
	
	public Set<WorksheetDescriptor> getWorksheetDescriptors() {
		return queries.stream().map(q -> new WorksheetDescriptor(q.getWorksheetName(), q.getFieldConfigs())).collect(Collectors.toSet());
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
