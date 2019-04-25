package io.jayms.dbsc.model;

import java.util.Arrays;

import lombok.Getter;

/**
 * Represents DataType of a field/column in a SQLite table.
 */
public enum DataType {

	TEXT(12),
	INT(4);
	
	@Getter private int type;
	
	private DataType(int type) {
		this.type = type;
	}
	
	/**
	 * Retrieve DataType corresponding to its numerical id.
	 * @param type - numerical id
	 * @return - Returns DataType of which has an identical numerical id.
	 */
	public static DataType valueOf(int type) {
		DataType[] dataTypes = DataType.values();
		return Arrays.stream(dataTypes).filter(d -> d.getType() == type).findFirst().orElse(null);
	}
}
