package io.jayms.dbsc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.jayms.dbsc.model.Column;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DataType;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.model.TabEditorData;
import io.jayms.dbsc.model.Table;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class DBSCGraphicalUserInterface extends Application {

	public static final boolean DEBUG = false;
	
	public static void main(String[] args) {
//		File dbFile = new File("localDBs2.sqlite");
//		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
//		DatabaseManager dm = new DatabaseManager(sqliteDb);
//		List<DB> dbs = new ArrayList<>();
//		dbs.add(new DB("db1", Arrays.asList(new Report("bubbly", "SELECT * FROM TABLE WHERE ID = 1", "SELECT yeet FROM DAB WHERE CLOUT > 666"), new Report("jubbly", "SELET name FROM USERS WHERE surname = \"smith\""))));
//		dbs.add(new DB("db2", Arrays.asList(new Report("fubbly", "SELECT emoji from emoticons WHERE ROWNUM <= 100"))));
//		ConnectionConfig cc = new ConnectionConfig("192.168.1.1", 3000, "root", "password", dbs);
//		dm.store(cc);
//		System.out.println("dd");
//		dm.close();
		launch(args);
		
//		File dbFile = new File("localDBs2.sqlite");
//		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
//		DatabaseManager dm = new DatabaseManager(sqliteDb);
//		dm.loadConnectionConfigs();
//		Collection<ConnectionConfig> ccs = dm.connectionConfigs();
//		System.out.println(ccs.size());
//		for (ConnectionConfig cc : ccs) {
//			System.out.println(cc.toString());
//		}
//		dm.close();
	}
	
	private static final double rightTopPaneHeight = 0.05;
	private static final double leftPaneWidth = 0.3;
	private static final String EDITOR_FONT = "file:resources/fonts/Courier Prime.ttf";
	
	private Stage stage;
	
	private VBox rootPane;
	
	private HBox actionBar;
	private Button runQueryBtn;
	private Button stopQueryBtn;
	private Button openQueryBuilderBtn;
	
	private SplitPane masterPane;
	
	private VBox leftPane;
	private Button newConnectionsBtn;
	private TreeView<String> connections;
	
	private SplitPane rightPane;
	private HBox topPane;
	private TextField pathDisplay;
	private Button ssFileChooseBtn;
	private FileChooser ssFileChooser;
	private File chosenFile;
	
	private TabPane queriesTab;
	
	private Stage newConnectionStage;
	private Scene newConnectionScene;
	private VBox newConnectionRoot;
	
	private HBox newConnTitleCtr;
	private Label newConnTitle;
	
	private HBox hostnameCtr;
	private Label hostnameLbl;
	private TextField hostnameTxt;
	
	private HBox portCtr;
	private Label portLbl;
	private TextField portTxt;
	
	private HBox dbnameCtr;
	private Label dbnameLbl;
	private TextField dbnameTxt;
	
	private HBox userCtr;
	private Label userLbl;
	private TextField userTxt;
	
	private HBox passCtr;
	private Label passLbl;
	private TextField passTxt;
	
	private HBox createBtnCtr;
	private Button createBtn;
	
	private DatabaseManager dbMan;
	
	private Stage queryBuilderStage;
	private Scene queryBuilderScene;
	
	private VBox queryBuilderRootPane;
	
	private HBox queryBuilderActionBar;
	
	private HBox qbAddTableCtr;
	private ComboBox<String> qbAddTableCmb;
	private Button qbAddTableBtn;
	
	private Pane queryBuilderPane;
	
	private static DB qbTestDB;
	
	static {
		List<Report> testReports = new ArrayList<>();
		testReports.add(new Report("TestReport", "SELECT DAB FROM THEYEET"));
		
		Set<Table> testTables = new HashSet<>();
		Set<Column> table1Columns = new HashSet<>();
		table1Columns.add(new Column("name", DataType.TEXT));
		testTables.add(new Table("table1", table1Columns));
		
		qbTestDB = new DB("TestDB", testReports, testTables);
	}
	
	private void newQueryBuilder(DB db) {
		queryBuilderStage = new Stage();
		queryBuilderStage.setTitle("Query Builder");
		
		queryBuilderRootPane = new VBox();
		
		queryBuilderActionBar = new HBox();
		
		qbAddTableCtr = new HBox();
		qbAddTableCmb = new ComboBox<>();
		qbAddTableBtn = new Button("Add Table");
		qbAddTableCtr.getChildren().addAll(qbAddTableCmb, qbAddTableBtn);
	
		Set<Table> tables = db.getTables();
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
		queryBuilderStage.setScene(queryBuilderScene);
	}
	
	private void openQueryBuilder() {
		if (queryBuilderStage == null) {
			newQueryBuilder(qbTestDB);
		}
		queryBuilderStage.show();
	}
	
	private void newConnectionStage() {
		newConnectionStage = new Stage();
		newConnectionStage.setTitle("Create New Connection");
		
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newConnectionRoot = new VBox();
		newConnectionRoot.setSpacing(10);
		rootCtr.getChildren().add(newConnectionRoot);
		root.getChildren().add(rootCtr);
		newConnectionScene = new Scene(root, 400, 300);
		
		newConnTitleCtr = new HBox();
		newConnTitleCtr.setAlignment(Pos.CENTER);
		newConnTitle = new Label("New Connection");
		newConnTitle.setFont(Font.font("Arial", 20));
		newConnTitle.setAlignment(Pos.CENTER);
		newConnTitleCtr.getChildren().add(newConnTitle);
		
		hostnameCtr = new HBox();
		hostnameCtr.setAlignment(Pos.CENTER_RIGHT);
		portCtr = new HBox();
		portCtr.setAlignment(Pos.CENTER_RIGHT);
		dbnameCtr = new HBox();
		dbnameCtr.setAlignment(Pos.CENTER_RIGHT);
		userCtr = new HBox();
		userCtr.setAlignment(Pos.CENTER_RIGHT);
		passCtr = new HBox();
		passCtr.setAlignment(Pos.CENTER_RIGHT);
		createBtnCtr = new HBox();
		createBtnCtr.setAlignment(Pos.CENTER);
		
		hostnameLbl = new Label("Hostname: ");
		portLbl = new Label("Port: ");
		dbnameLbl = new Label("DB Name: ");
		userLbl = new Label("User: ");
		passLbl = new Label("Pass: ");
	
		hostnameTxt = new TextField();
		hostnameTxt.setPromptText("Enter hostname");
		portTxt = new TextField();
		portTxt.setPromptText("Enter port");
		dbnameTxt = new TextField();
		dbnameTxt.setPromptText("Enter DB name");
		userTxt = new TextField();
		userTxt.setPromptText("Enter user");
		passTxt = new TextField();
		passTxt.setPromptText("Enter pass");
		
		createBtn = new Button("Create");
		createBtnCtr.getChildren().add(createBtn);
		
		hostnameCtr.getChildren().addAll(hostnameLbl, hostnameTxt);
		portCtr.getChildren().addAll(portLbl, portTxt);
		dbnameCtr.getChildren().addAll(dbnameLbl, dbnameTxt);
		userCtr.getChildren().addAll(userLbl, userTxt);
		passCtr.getChildren().addAll(passLbl, passTxt);
		
		newConnectionRoot.getChildren().addAll(newConnTitleCtr,
				hostnameCtr,
				portCtr,
				dbnameCtr,
				userCtr,
				passCtr,
				createBtnCtr);
		
		newConnectionStage.setScene(newConnectionScene);
	}
	
	private void leftPane() {
		leftPane = new VBox();
		
		newConnectionsBtn = new Button("New Connection");
		newConnectionsBtn.setMaxWidth(Double.MAX_VALUE);
		
		newConnectionsBtn.setOnMouseClicked((e) -> {
			if (newConnectionStage == null) {
				newConnectionStage();
			}
			newConnectionStage.show();
		});
		
		TreeItem<String> rootItem = new TreeItem<>("Connections");
		TreeItem<String> connItem = new TreeItem<>("127.0.0.1");
		TreeItem<String> reportItem = new TreeItem<>("Finance Report");
		connItem.getChildren().add(reportItem);
		rootItem.getChildren().add(connItem);
		connections = new TreeView<>(rootItem);
		connections.setMaxHeight(Double.MAX_VALUE);
		
		leftPane.getChildren().addAll(newConnectionsBtn, connections);
	}
	
	private TreeItem<String> connectionTreeItem(ConnectionConfig cc) {
		TreeItem<String> treeItem = new TreeItem<>(cc.getHost());
		for (DB db : cc.getDbs()) {
			
		}
		return treeItem;
	}
	
	private void rightPane() {
		rightPane = new SplitPane();
		
		topPane = new HBox();
		topPane.setMaxWidth(Double.MAX_VALUE);
		
		ssFileChooser = new FileChooser();
		ssFileChooser.setTitle("Choose Spreadsheet Destination");
		ssFileChooser.getExtensionFilters().add(new ExtensionFilter("Spreadsheet Files", ".xlsx"));
		
		pathDisplay = new TextField();
		HBox.setHgrow(pathDisplay, Priority.ALWAYS);
		ssFileChooseBtn = new Button("Browse...");
		
		ssFileChooseBtn.setOnMouseClicked((e) -> {
			ssFileChooser.showOpenDialog(stage);
		});
		topPane.getChildren().addAll(pathDisplay, ssFileChooseBtn);
		
		queriesTab = new TabPane();
		Tab queryTab = queryTab("Finance Query");
		queriesTab.getTabs().add(queryTab);
		
		rightPane.setDividerPosition(0, rightTopPaneHeight);
		rightPane.setOrientation(Orientation.VERTICAL);
		rightPane.getItems().addAll(topPane, queriesTab);
	}
	
	private Tab queryTab(String text) {
		Tab queryTab = new Tab();
		queryTab.setText("Finance Query");
		
		TextField queryTextBox = new TextField();
		queryTextBox.setMaxWidth(Double.MAX_VALUE);
		queryTextBox.setMaxHeight(Double.MAX_VALUE);
		queryTextBox.setAlignment(Pos.TOP_LEFT);
		System.out.println(Font.getFontNames());
		queryTextBox.setFont(Font.loadFont(EDITOR_FONT, 14));
		queryTextBox.selectPositionCaret(0);
		queryTab.setContent(queryTextBox);
		return queryTab;
	}
	
	private void runQuery(MouseEvent e) {
		if (chosenFile == null) {
			Alert alert = new Alert(AlertType.ERROR, "You need to select a file destination first!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		Tab curTab = queriesTab.getSelectionModel().getSelectedItem();
		if (curTab == null) {
			System.out.println("No tab open.");
			return;
		}
		String title = curTab.getText();
		Object tabDataObj = curTab.getUserData();
		if (tabDataObj == null || !(tabDataObj instanceof TabEditorData)) {
			System.out.println("No tab editor data held");
			return;
		}
		TabEditorData tabData = (TabEditorData) tabDataObj;
		
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		File dbFile = new File("localDBs2.sqlite");
		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
		dbMan = new DatabaseManager(sqliteDb);
		if (dbMan.loadConnectionConfigs()) {
			System.out.println("Loaded connection configs.");
		} else {
			System.out.println("Did not load any connection configs.");
		}
		
		this.stage = stage;
		stage.setTitle("DBSC");
		
		rootPane = new VBox();
		rootPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		
		actionBar = new HBox();
		
		runQueryBtn = new Button("Run Query");
		runQueryBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			runQuery(e);
		});
		
		stopQueryBtn = new Button("Stop Query");
		openQueryBuilderBtn = new Button("Open Query Builder");
		openQueryBuilderBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			openQueryBuilder();
		});
		actionBar.getChildren().addAll(runQueryBtn, stopQueryBtn, openQueryBuilderBtn);
		
		masterPane = new SplitPane();
		masterPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		masterPane.setOrientation(Orientation.HORIZONTAL);
		
		leftPane();
		rightPane();
		
		masterPane.setDividerPosition(0, leftPaneWidth);
		masterPane.getItems().addAll(leftPane, rightPane);
		
		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			onWidthResize(oldVal, newVal);
		});
		
		rootPane.getChildren().addAll(actionBar, masterPane);
		Scene scene = new Scene(rootPane, 800, 600);
		stage.setScene(scene);
		
		stage.show();
	}
	
	private void onWidthResize(Number oldVal, Number newVal) {
		masterPane.setDividerPosition(0, leftPaneWidth);
		rightPane.setDividerPosition(0, rightTopPaneHeight);
	}

}