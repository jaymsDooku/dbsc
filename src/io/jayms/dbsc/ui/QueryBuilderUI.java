package io.jayms.dbsc.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.collect.Multimap;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.qb.QueryBuilderContext;
import io.jayms.dbsc.util.ComponentFactory;
import io.jayms.dbsc.util.DraggableNode;
import io.jayms.dbsc.util.DraggablePane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class QueryBuilderUI extends StandaloneUIModule {

	private static final long JOIN_HOLD_DURATION = 1050;
	
	private static final Background UNSELECTED_BG = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background SELECTED_BG = new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background JOINED_BG = new Background(new BackgroundFill(Color.LIME, CornerRadii.EMPTY, Insets.EMPTY));
	
	private Scene queryBuilderScene;
	
	private VBox queryBuilderRootPane;
	
	private HBox queryBuilderActionBar;
	
	private HBox qbAddTableCtr;
	private ComboBox<String> qbAddTableCmb;
	private Button qbAddTableBtn;
	
	private DraggablePane queryBuilderPane;
	private QueryBuilderContext queryBuilderContext;
	
	private final DB db;
	
	private Set<Table> addedTables = new HashSet<>();
	
	public QueryBuilderUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.db = db;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init() {
		super.init();
		
		uiStage = new Stage();
		uiStage.setTitle("Query Builder");
		System.out.println("set title");
		
		queryBuilderRootPane = new VBox();
		
		queryBuilderActionBar = new HBox();
		
		qbAddTableCtr = new HBox();
		qbAddTableCmb = new ComboBox<>();
		
		qbAddTableBtn = ComponentFactory.createButton("Add Table", (e) -> {
			String tableName = qbAddTableCmb.getSelectionModel().getSelectedItem();
			List<Table> tables = db.getTables();
			Table table = tables.stream().filter(t -> t.getName().equals(tableName)).findFirst().orElse(null);
			
			if (addedTables.contains(table)) {
				return;
			}
			
			addedTables.add(table);
			
			DraggableNode draggable;
			VBox tableCtr = new VBox();
			tableCtr.setAlignment(Pos.CENTER);
			tableCtr.setUserData(table);
			draggable = new DraggableNode(tableCtr);
			
			HBox tableHeaderCtr = new HBox();
			tableHeaderCtr.setAlignment(Pos.CENTER);
			tableHeaderCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			Label tableLbl = new Label(tableName);
			tableHeaderCtr.getChildren().addAll(tableLbl);
			
			HBox tableColCtr = new HBox();
			tableColCtr.setAlignment(Pos.CENTER);
			tableColCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			List<Column> columns = table.getColumns();
			columns.stream().forEach(c -> {
				HBox colCtr = new HBox();
				colCtr.setAlignment(Pos.CENTER);
				Label colLbl = new Label(c.getName());
				colLbl.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
				colLbl.onMousePressedProperty().set((colEv) -> {
					long timeOfPress = System.currentTimeMillis();
					long activateJoinTime = timeOfPress + JOIN_HOLD_DURATION;
					Timer pressedTimer = new Timer();
					pressedTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							if (System.currentTimeMillis() > activateJoinTime) {
								Platform.runLater(() -> {
									colLbl.setBackground(JOINED_BG);
								});
							}
						}
						
					}, 0L, 1L);
				});
				colLbl.onMouseReleasedProperty().set((colEv) -> {
					
				});
				colLbl.onMouseClickedProperty().set((colEv) -> {
					Multimap<Table, Column> fieldsToSelect = queryBuilderContext.getFieldsToSelect();
					
					if (fieldsToSelect.containsEntry(table, c)) {
						colLbl.setBackground(UNSELECTED_BG);
						fieldsToSelect.remove(table, c);
					} else {
						colLbl.setBackground(SELECTED_BG);
						fieldsToSelect.put(table, c);
					}
				});
				Circle colJoinIndicator = new Circle(4, Color.RED);
				colCtr.getChildren().addAll(colLbl, colJoinIndicator);
				tableColCtr.getChildren().add(colCtr);
			});
			
			tableCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			tableCtr.getChildren().addAll(tableHeaderCtr, tableColCtr);
			queryBuilderPane.addDraggable(draggable);
		});
		queryBuilderContext = new QueryBuilderContext();
		
		qbAddTableCtr.getChildren().addAll(qbAddTableCmb, qbAddTableBtn);
		System.out.println("set controls");
	
		List<Table> tables = db.getTables();
		for (Table table : tables) {
			qbAddTableCmb.getItems().add(table.getName());
		}
		if (!qbAddTableCmb.getItems().isEmpty()) {
			qbAddTableCmb.getSelectionModel().select(0);
		}
		System.out.println("populated combo with tables");
		
		queryBuilderActionBar.getChildren().add(qbAddTableCtr);
		queryBuilderActionBar.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		queryBuilderPane = new DraggablePane();
		queryBuilderPane.setPrefSize(800, 600);
		
		queryBuilderRootPane.getChildren().addAll(queryBuilderPane, queryBuilderActionBar);
		
		queryBuilderScene = new Scene(queryBuilderRootPane, 800, 800);
		uiStage.setScene(queryBuilderScene);
	}
}
