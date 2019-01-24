package io.jayms.dbsc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.CreateConnectionUI;
import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.ui.RegisterDatabaseUI;
import io.jayms.dbsc.ui.UIModule;
import io.jayms.dbsc.ui.comp.ActionBar;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.RightPane;
import io.jayms.dbsc.util.DBHelper;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;

public class DBSCGraphicalUserInterface extends Application {

	public static final boolean DEBUG = false;
	
	public static void main(String[] args) {
//		File dbFile = new File("localDBs.sqlite");
//		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
//		DatabaseManager dm = new DatabaseManager(sqliteDb);
//		ConnectionConfig cc = new ConnectionConfig("192.168.1.1", 3000, "root", "password");
//		List<DB> dbs = new ArrayList<>();
//		
//		List<Report> reports1 = new ArrayList<>();
//		Report r1 = new Report("bubbly", new Query("smth", "SELECT * FROM TABLE WHERE ID = 1"), new Query("toptext", "SELECT yeet FROM DAB WHERE CLOUT > 666"));
//		Report r2 = new Report("jubbly", new Query("bottomtext", "SELET name FROM USERS WHERE surname = \"smith\""));
//		reports1.add(r1);
//		reports1.add(r2);
//		DB db1 = new DB(cc, "db1", "svr1", DBType.SQL_SERVER);
//		db1.setReports(reports1);
//		dbs.add(db1);
//		
//		List<Report> reports2 = new ArrayList<>();
//		Report r3 = new Report("fubbly", new Query("smh", "SELECT emoji from emoticons WHERE ROWNUM <= 100"));
//		reports2.add(r3);
//		DB db2 = new DB(cc, "db2", DBType.ORACLE);
//		db2.setReports(reports2);
//		dbs.add(db2);
//		
//		cc.setDbs(dbs);
//		dm.store(cc);
//		System.out.println("dd");
//		dm.close();
		launch(args);

		/*File dbFile = new File("localDBs2.sqlite");
		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
		DatabaseManager dm = new DatabaseManager(sqliteDb);
		dm.loadConnectionConfigs();
		Collection<ConnectionConfig> ccs = dm.connectionConfigs();
		System.out.println(ccs.size());
		for (ConnectionConfig cc : ccs) {
			System.out.println(cc.toString());
		}*/
	}
	
	public static final double rightTopPaneHeight = 0.05;
	public static final double leftPaneWidth = 0.3;
	public static final String EDITOR_FONT = "file:resources/fonts/Courier Prime.ttf";
	
	@Getter private Stage stage;
	
	@Getter private BorderPane genesisPane;
	@Getter private VBox rootPane;
	
	@Getter private SplitPane masterPane;
	
	@Getter private ActionBar actionBar;
	@Getter private LeftPane leftPane;
	@Getter private RightPane rightPane;
	
	@Getter private DatabaseManager databaseManager;
	@Getter private DBHelper dbHelper;
	
	@Getter private final Set<UIModule> uiModules = new HashSet<>();
	@Getter private	CreateConnectionUI createConnectionUI;
	@Getter private RegisterDatabaseUI registerDatabaseUI;
	@Getter private QueryBuilderUI queryBuilderUI;
	
	/*private static DB qbTestDB;
	
	static {
		List<Report> testReports = new ArrayList<>();
		testReports.add(new Report("TestReport", new Query("YeetSheet1", "SELECT DAB FROM THEYEET")));
		
		Set<Table> testTables = new HashSet<>();
		Set<Column> table1Columns = new HashSet<>();
		table1Columns.add(new Column("name", DataType.TEXT));
		testTables.add(new Table("table1", table1Columns));
		
		qbTestDB = new DB("TestDB", DBType.SQLITE, testReports, testTables);
	}*/
	
	@Override
	public void start(Stage stage) throws Exception {
		File dbFile = new File("localDBs.sqlite");
		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
		databaseManager = new DatabaseManager(sqliteDb);

		if (databaseManager.loadConnectionConfigs()) {
			System.out.println("Loaded connection configs.");
		} else {
			System.out.println("Did not load any connection configs.");
		}
		
		Collection<ConnectionConfig> connConfigs = databaseManager.getConnectionConfigs();
		int i = 0;
		for (ConnectionConfig cc : connConfigs) {
			System.out.println("Connection Config " + i++ + ": " + cc);
		}
		
		dbHelper = new DBHelper(databaseManager);
		
		this.stage = stage;
		stage.setTitle("DBSC");
		
		genesisPane = new BorderPane();
		
		rootPane = new VBox();
		rootPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		rootPane.setPrefHeight(Double.MAX_VALUE);
		
		masterPane = new SplitPane();
		masterPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		masterPane.setOrientation(Orientation.HORIZONTAL);
		masterPane.prefHeightProperty().bind(rootPane.heightProperty());
		
		initUIModules();
		
		masterPane.setDividerPosition(0, leftPaneWidth);
		masterPane.getItems().addAll(leftPane.getLeftPane(), rightPane.getRightPane());
		
		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			onWidthResize(oldVal, newVal);
		});
		
		rootPane.getChildren().addAll(actionBar.getActionBar(), masterPane);
		genesisPane.setCenter(rootPane);
		Scene scene = new Scene(genesisPane, 800, 600);
		stage.setScene(scene);
		
		stage.show();
	}
	
	@Override
	public void stop() throws Exception {
		for (UIModule uiModule : uiModules) {
			uiModule.close();
		}
		databaseManager.close();
	}
	
	private void initUIModules() {
		createConnectionUI = new CreateConnectionUI(this);
		registerDatabaseUI = new RegisterDatabaseUI(this);
		
		leftPane = new LeftPane(this);
		rightPane = new RightPane(this);
		
		uiModules.add(leftPane);
		uiModules.add(rightPane);
		
		actionBar = new ActionBar(this);
		
		uiModules.add(actionBar);
		for (UIModule uiModule : uiModules) {
			uiModule.init();
		}
	}
	
	private void onWidthResize(Number oldVal, Number newVal) {
		masterPane.setDividerPosition(0, leftPaneWidth);
		rightPane.getRightPane().setDividerPosition(0, rightTopPaneHeight);
	}

}