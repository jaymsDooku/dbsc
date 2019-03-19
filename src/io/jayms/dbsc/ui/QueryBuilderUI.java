package io.jayms.dbsc.ui;

import java.util.List;

import com.google.common.collect.Multimap;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.qb.QueryBuilderContext;
import io.jayms.dbsc.util.ComponentFactory;
import io.jayms.dbsc.util.DraggableNode;
import io.jayms.dbsc.util.DraggablePane;
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
import javafx.stage.Stage;

public class QueryBuilderUI extends StandaloneUIModule {

	private static final Background UNSELECTED_BG = new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
	private static final Background SELECTED_BG = new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY, Insets.EMPTY));
	
	private Scene queryBuilderScene;
	
	private VBox queryBuilderRootPane;
	
	private HBox queryBuilderActionBar;
	
	private HBox qbAddTableCtr;
	private ComboBox<String> qbAddTableCmb;
	private Button qbAddTableBtn;
	
	private DraggablePane queryBuilderPane;
	private QueryBuilderContext queryBuilderContext;
	
	private final DB db;
	
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
			
			DraggableNode draggable;
			VBox tableCtr = new VBox();
			tableCtr.setAlignment(Pos.CENTER);
			tableCtr.setUserData(table);
			draggable = new DraggableNode(tableCtr);
			
			HBox tableHeaderCtr = new HBox();
			tableHeaderCtr.setAlignment(Pos.CENTER);
			tableHeaderCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			Label tableLbl = new Label(tableName);
			//tableLbl.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			tableHeaderCtr.getChildren().addAll(tableLbl);
			
			HBox tableColCtr = new HBox();
			tableColCtr.setAlignment(Pos.CENTER);
			tableColCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			List<Column> columns = table.getColumns();
			columns.stream().forEach(c -> {
				Label colLbl = new Label(c.getName());
				colLbl.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
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
				tableColCtr.getChildren().add(colLbl);
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
