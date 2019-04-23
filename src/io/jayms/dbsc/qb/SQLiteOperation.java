package io.jayms.dbsc.qb;

import javafx.beans.property.SimpleStringProperty;

public class SQLiteOperation {

	private SimpleStringProperty detail;
	
	public SQLiteOperation(String detail) {
		this.detail = new SimpleStringProperty(detail);
	}
	
	public String getDetail() {
		return detail.get();
	}
}
