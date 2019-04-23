package io.jayms.dbsc.qb;

import java.sql.Time;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class OracleOperation {

	private SimpleStringProperty operationName;
	private SimpleStringProperty object;
	private SimpleIntegerProperty rows;
	private SimpleLongProperty bytes;
	private SimpleLongProperty cost;
	private SimpleObjectProperty<Time> time;
	
	public OracleOperation(String operationName, String object, int rows, long bytes, long cost, Time time) {
		this.operationName = new SimpleStringProperty(operationName);
		this.object = new SimpleStringProperty(object);
		this.rows = new SimpleIntegerProperty(rows);
		this.bytes = new SimpleLongProperty(bytes);
		this.cost = new SimpleLongProperty(cost);
		this.time = new SimpleObjectProperty<>(time);
	}
	
	public String getOperationName() {
		return operationName.get();
	}
	
	public String getObject() {
		return object.get();
	}
	
	public int getRows() {
		return rows.get();
	}
	
	public long getBytes() {
		return bytes.get();
	}
	
	public long getCost() {
		return cost.get();
	}
	
	public Time getTime() {
		return time.get();
	}
	
}
