package io.jayms.dbsc.ui;

import java.util.Set;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Table;
import io.jayms.dbsc.util.DBHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class QueryBuilderUI extends StandaloneUIModule {

	private Scene queryBuilderScene;
	
	private VBox queryBuilderRootPane;
	
	private HBox queryBuilderActionBar;
	
	private HBox qbAddTableCtr;
	private ComboBox<String> qbAddTableCmb;
	private Button qbAddTableBtn;
	
	private Pane queryBuilderPane;
	
	private final DB db;
	
	public QueryBuilderUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.db = db;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = new Stage();
		uiStage.setTitle("Query Builder");
		
		queryBuilderRootPane = new VBox();
		
		queryBuilderActionBar = new HBox();
		
		qbAddTableCtr = new HBox();
		qbAddTableCmb = new ComboBox<>();
		qbAddTableBtn = new Button("Add Table");
		qbAddTableCtr.getChildren().addAll(qbAddTableCmb, qbAddTableBtn);
	
		Set<Table> tables = db.getTables();
		if (tables == null) {
			DBHelper dbHelper = masterUI.getDbHelper();
			tables = dbHelper.fetchTables(db);
			db.setTables(tables);
			System.out.println("fetched tables");
		}
		for (Table table : tables) {
			qbAddTableCmb.getItems().add(table.getName());
		}
		if (!qbAddTableCmb.getItems().isEmpty()) {
			qbAddTableCmb.getSelectionModel().select(0);
		}
		
		queryBuilderActionBar.getChildren().add(qbAddTableCtr);
		queryBuilderActionBar.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		queryBuilderPane = new Pane();
		queryBuilderPane.setPrefSize(800, 600);
		
		queryBuilderRootPane.getChildren().addAll(queryBuilderPane, queryBuilderActionBar);
		
		queryBuilderScene = new Scene(queryBuilderRootPane, 800, 800);
		uiStage.setScene(queryBuilderScene);
	}

}
