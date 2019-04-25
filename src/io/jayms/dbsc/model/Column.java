package io.jayms.dbsc.model;

import lombok.Getter;

/**
 * Represents a column/field of a SQLite table.
 */
public class Column {

	/**
	 * Name of column/field.
	 */
	@Getter private String name;
	/**
	 * Data Type of column/field.
	 */
	@Getter private DataType type;
	
	/**
	 * Instantiates Column.
	 * @param name - name of column/field.
	 * @param type - data type of column/field.
	 */
	public Column(String name, DataType type) {
		this.name = name;
		this.type = type;
	}
}
