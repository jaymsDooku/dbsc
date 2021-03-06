package io.jayms.dbsc.qb;

import com.google.common.collect.Multimap;

import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.ui.QueryBuilderUI;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import lombok.Getter;

public class ColumnLabel extends Label {

	private static final long JOIN_HOLD_DURATION = 1050;
	
	public static final Background UNSELECTED_BG = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	public static final Background SELECTED_BG = new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY));
	public static final Background JOINED_BG = new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY));
	
	@Getter private QueryBuilderUI qbUI;
	@Getter private Table table;
	@Getter private Column column;
	
	public ColumnLabel(QueryBuilderUI qbUI, Table table, Column column) {
		super(column.getName());
		this.qbUI = qbUI;
		this.table = table;
		this.column = column;
		
		this.setMinWidth(Region.USE_PREF_SIZE);
		this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		QueryBuilderContext queryBuilderContext = qbUI.getQueryBuilderContext();
		this.onMousePressedProperty().set((colEv) -> {
			Multimap<Table, Column> fieldsToSelect = queryBuilderContext.getFieldsToSelect();
			
			if (fieldsToSelect.containsEntry(table, column)) {
				this.setBackground(UNSELECTED_BG);
				fieldsToSelect.remove(table, column);
			} else {
				this.setBackground(SELECTED_BG);
				fieldsToSelect.put(table, column);
			}
		});
	}
	
}
