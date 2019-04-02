package io.jayms.dbsc.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.qb.ColumnLabel;
import io.jayms.dbsc.qb.QueryBuilderContext;
import io.jayms.dbsc.util.ComponentFactory;
import io.jayms.dbsc.util.DraggableNode;
import io.jayms.dbsc.util.DraggablePane;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.Getter;

public class QueryBuilderUI extends StandaloneUIModule {
	
	private Scene queryBuilderScene;
	
	private VBox queryBuilderRootPane;
	
	private HBox queryBuilderActionBar;
	
	private HBox qbAddTableCtr;
	private ComboBox<String> qbAddTableCmb;
	private Button qbAddTableBtn;
	
	private DraggablePane queryBuilderPane;
	@Getter private QueryBuilderContext queryBuilderContext;
	
	private final DB db;
	
	private Set<Table> addedTables = new HashSet<>();
	@Getter private Timer timer;
	
	public QueryBuilderUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.db = db;
		this.queryBuilderContext = new QueryBuilderContext();
		this.timer = new Timer();
	}
	
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
				//colCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
				colCtr.setAlignment(Pos.CENTER);
				ColumnLabel colLbl = new ColumnLabel(this, table, c);
				StackPane colJoinIndicator = new StackPane();
				colJoinIndicator.setBackground(ColumnLabel.SELECTED_BG);
				Circle colJoinCircle = new Circle(5);
				colJoinCircle.setFill(Color.RED);
				colJoinIndicator.getChildren().add(colJoinCircle);
				colCtr.getChildren().addAll(colLbl, colJoinIndicator);
				tableColCtr.getChildren().addAll(colCtr);
			});
			
			tableCtr.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			tableCtr.getChildren().addAll(tableHeaderCtr, tableColCtr);
			queryBuilderPane.addDraggable(draggable);
		});
		
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
