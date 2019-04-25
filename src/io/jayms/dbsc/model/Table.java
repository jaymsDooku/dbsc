package io.jayms.dbsc.model;

import java.util.List;

import lombok.Getter;

/**
 * Represents a Table in a database.
 */
public class Table {

	/**
	 * Name of table.
	 */
	private String name;
	
	/**
	 * Number of rows in table.
	 */
	@Getter private int rowCount;
	/**
	 * List of columns/fields in the table.
	 */
	@Getter private List<Column> columns;
	/**
	 * Database this table is located in.
	 */
	@Getter private DB db;
	
	/**
	 * Instantiate Table.
	 * @param name - name of table
	 * @param rowCount - row count
	 * @param columns - columns/fields
	 * @param db - database of table
	 */
	public Table(String name, int rowCount, List<Column> columns, DB db) {
		this.name = name;
		this.rowCount = rowCount;
		this.columns = columns;
		this.db = db;
	}
	
	/**
	 * Returns name of table.
	 * 
	 * If the database of the table is of Oracle type, prefix the name with the schema.
	 * 
	 * @return Name of table.
	 */
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
