package io.jayms.dbsc.model;

import java.util.Arrays;

import lombok.Getter;

public enum DataType {

	TEXT(12),
	INT(4);
	
	@Getter private int type;
	
	private DataType(int type) {
		this.type = type;
	}
	
	public static DataType valueOf(int type) {
		DataType[] dataTypes = DataType.values();
		return Arrays.stream(dataTypes).filter(d -> d.getType() == type).findFirst().orElse(null);
	}
}
