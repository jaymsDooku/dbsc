package io.jayms.dbsc.model;

import lombok.Getter;
import lombok.Setter;

public class TabEditorData {

	@Getter @Setter private ConnectionConfig cc;
	@Getter @Setter private DB db;
	@Getter @Setter private Report report;
	
	public TabEditorData(ConnectionConfig cc, DB db, Report report) {
		this.cc = cc;
		this.db = db;
		this.report = report;
	}
}
