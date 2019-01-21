package io.jayms.dbsc.model;

import lombok.Getter;

public enum DataType {

	TEXT(0),
	INT(1);
	
	@Getter private int type;
	
	private DataType(int type) {
		this.type = type;
	}
}
