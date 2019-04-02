package io.jayms.dbsc.qb;

import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.ui.QueryBuilderUI;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;

public class JoinCircle extends Circle {
	
	private QueryBuilderUI ui;
	@Getter private Table table;
	@Getter private Column column;
	
	@Getter @Setter private Join join;
	
	public JoinCircle(QueryBuilderUI ui, Table table, Column column) {
		super(5, Color.RED);
		this.table = table;
		this.column = column;
		
		onMouseClickedProperty().set((e) -> {
			ui.startJoinLine(this);
		});
	}
	
	public boolean isJoined() {
		return join != null;
	}
	
}
