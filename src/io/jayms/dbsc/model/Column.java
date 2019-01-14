package io.jayms.dbsc.model;

import lombok.Getter;

public class Column {

	@Getter private String name;
	@Getter private DataType type;
	
	public Column(String name, DataType type) {
		this.name = name;
		this.type = type;
	}
}
