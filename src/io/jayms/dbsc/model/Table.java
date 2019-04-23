package io.jayms.dbsc.model;

import java.util.List;
import java.util.Set;

import lombok.Getter;

public class Table {

	private String name;
	@Getter private int rowCount;
	@Getter private List<Column> columns;
	@Getter private DB db;
	
	public Table(String name, int rowCount, List<Column> columns, DB db) {
		this.name = name;
		this.rowCount = rowCount;
		this.columns = columns;
		this.db = db;
	}
	
	public String getName() {
		if (db.getType() == DBType.ORACLE) {
			return db.getDatabaseName() + "." + name;
		}
		return name;
	}
	
	@Override
	public String toString() {
		return "name=" + name + "|rowCount=" + rowCount + "|columns=" + columns;
	}
}
