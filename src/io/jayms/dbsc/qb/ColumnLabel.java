package io.jayms.dbsc.qb;

import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.Multimap;

import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.ui.QueryBuilderUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
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
	
	private TimerTask holdTask;
	
	public ColumnLabel(QueryBuilderUI qbUI, Table table, Column column) {
		super(column.getName());
		this.qbUI = qbUI;
		this.table = table;
		this.column = column;
		
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
			
			long timeOfPress = System.currentTimeMillis();
			long activateJoinTime = timeOfPress + JOIN_HOLD_DURATION;
			holdTask = new TimerTask() {

				@Override
				public void run() {
					if (System.currentTimeMillis() > activateJoinTime) {
						Platform.runLater(() -> {
							ColumnLabel.this.setBackground(JOINED_BG);
						});
					}
				}
				
			};
			qbUI.getTimer().schedule(holdTask, 0L, 1L);
		});
		this.onMouseReleasedProperty().set((colEv) -> {
			holdTask.cancel();
		});
	}
	
}
