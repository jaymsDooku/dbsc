package io.jayms.dbsc.model;

import java.util.Set;

import lombok.Getter;

public class Table {

	@Getter private String name;
	@Getter private Set<Column> columns;
	
	public Table(String name, Set<Column> columns) {
		this.name = name;
		this.columns = columns;
	}
}
