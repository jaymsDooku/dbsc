package io.jayms.dbsc.model;

import java.util.List;
import java.util.Set;

import lombok.Getter;

public class Table {

	@Getter private String name;
	@Getter private List<Column> columns;
	
	public Table(String name, List<Column> columns) {
		this.name = name;
		this.columns = columns;
	}
}