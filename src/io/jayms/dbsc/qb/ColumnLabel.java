package io.jayms.dbsc.qb;

import io.jayms.dbsc.model.Column;
import javafx.scene.control.Label;
import lombok.Getter;

public class ColumnLabel extends Label {

	@Getter private Column column;
	
	public ColumnLabel(Column column) {
		this.column = column;
	}
	
	
	
}
