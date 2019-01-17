package io.jayms.dbsc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class DBSCGraphicalUserInterface extends Application {

	public static final boolean DEBUG = false;
	
	public static void main(String[] args) {
//		File dbFile = new File("localDBs.sqlite");
//		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
//		DatabaseManager dm = new DatabaseManager(sqliteDb);
//		List<DB> dbs = new ArrayList<>();
//		dbs.add(new DB("db1", DBType.SQL_SERVER, Arrays.asList(new Report("bubbly", new Query("smth", "SELECT * FROM TABLE WHERE ID = 1"), new Query("toptext", "SELECT yeet FROM DAB WHERE CLOUT > 666")), new Report("jubbly", new Query("bottomtext", "SELET name FROM USERS WHERE surname = \"smith\"")))));
//		dbs.add(new DB("db2", DBType.ORACLE, Arrays.asList(new Report("fubbly", new Query("smh", "SELECT emoji from emoticons WHERE ROWNUM <= 100")))));
//		ConnectionConfig cc = new ConnectionConfig("192.168.1.1", 3000, "root", "password", dbs);
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
	
	private static final double rightTopPaneHeight = 0.05;
	private static final double leftPaneWidth = 0.3;
	private static final String EDITOR_FONT = "file:resources/fonts/Courier Prime.ttf";
	
	private Stage stage;
	
	private SplitPane masterPane;
	
	private VBox leftPane;
	private Button newConnectionsBtn;
	private TreeView<String> connections;
	private TreeItem<String> connectionsRoot;
	
	private SplitPane rightPane;
	private HBox topPane;
	private TextField pathDisplay;
	private Button ssFileChooseBtn;
	private FileChooser ssFileChooser;
	
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
	
	private HBox userCtr;
	private Label userLbl;
	private TextField userTxt;
	
	private HBox passCtr;
	private Label passLbl;
	private TextField passTxt;
	
	private HBox createBtnCtr;
	private Button createBtn;

	private DatabaseManager dbMan;
	
	private Stage newDBStage;
	private Scene newDBScene;
	private VBox newDBRoot;
	
	private HBox newDBTitleCtr;
	private Label newDBTitle;
	
	private HBox dbNameCtr;
	private Label dbNameLbl;
	private TextField dbNameTxt;
	
	private HBox dbTypeCtr;
	private Label dbTypeLbl;
	private ComboBox<String> dbTypeCmb;
	
	private void newDBStage() {
		newDBStage = new Stage();
		newDBStage.setTitle("Create New Database");
		
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newDBRoot = new VBox();
		newDBRoot.setSpacing(10);
		rootCtr.getChildren().add(newDBRoot);
		root.getChildren().add(rootCtr);
		newDBScene = new Scene(root, 400, 300);
		
		newDBTitleCtr = new HBox();
		newDBTitleCtr.setAlignment(Pos.CENTER);
		newDBTitle = new Label("New Database");
		newDBTitle.setFont(Font.font("Arial", 20));
		newDBTitle.setAlignment(Pos.CENTER);
		newDBTitleCtr.getChildren().add(newDBTitle);
		
		dbNameCtr = new HBox();
		dbNameCtr.setAlignment(Pos.CENTER_RIGHT);
		dbTypeCtr = new HBox();
		dbTypeCtr.setAlignment(Pos.CENTER_RIGHT);
		
		dbNameLbl = new Label("DB Name: ");
		dbTypeLbl = new Label("DB Type: ");
	
		dbNameTxt = new TextField();
		dbNameTxt.setPromptText("Enter DB name");
		dbTypeCmb = new ComboBox<>();
		for (DBType dbType : DBType.values()) {
			dbTypeCmb.getItems().add(dbType.toString().toLowerCase());
		}
		
		dbNameCtr.getChildren().addAll(dbNameLbl, dbNameTxt);
		dbTypeCtr.getChildren().addAll(dbTypeLbl, dbTypeCmb);
		
		createBtn = new Button("Create");
		EventHandler<MouseEvent> createBtnPress = (MouseEvent e) -> {
			String databaseName = dbNameTxt.getText();
			DBType dbType = DBType.valueOf(dbTypeCmb.getSelectionModel().getSelectedItem().toUpperCase());
			DB db = new DB(databaseName, dbType, new ArrayList<>());
			System.out.println("Creating new database: " + db);
			
			newConnectionTreeItem(cc);
		};
		createBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, createBtnPress);
		createBtnCtr.getChildren().add(createBtn);
		
		newDBRoot.getChildren().addAll(newDBTitleCtr,
				dbNameCtr,
				dbTypeCtr,
				createBtnCtr,
				createBtnCtr);
		
		newDBStage.setScene(newDBScene);
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
		userCtr = new HBox();
		userCtr.setAlignment(Pos.CENTER_RIGHT);
		passCtr = new HBox();
		passCtr.setAlignment(Pos.CENTER_RIGHT);
		createBtnCtr = new HBox();
		createBtnCtr.setAlignment(Pos.CENTER);
		
		hostnameLbl = new Label("Hostname: ");
		portLbl = new Label("Port: ");
		userLbl = new Label("User: ");
		passLbl = new Label("Pass: ");
	
		hostnameTxt = new TextField();
		hostnameTxt.setPromptText("Enter hostname");
		portTxt = new TextField();
		portTxt.setPromptText("Enter port");
		EventHandler<KeyEvent> portTxtType = (KeyEvent e) -> {
			try {
				Integer.parseInt(e.getCharacter());
			} catch (NumberFormatException ex) {
				e.consume();
			}
		};
		portTxt.addEventHandler(KeyEvent.KEY_TYPED, portTxtType);
		
		userTxt = new TextField();
		userTxt.setPromptText("Enter user");
		passTxt = new TextField();
		passTxt.setPromptText("Enter pass");
		
		createBtn = new Button("Create");
		EventHandler<MouseEvent> createBtnPress = (MouseEvent e) -> {
			ConnectionConfig cc = new ConnectionConfig(hostnameTxt.getText(),
					Integer.parseInt(portTxt.getText()),
					userTxt.getText(),
					passTxt.getText(),
					new ArrayList<>());
			dbMan.store(cc);
			System.out.println("Creating new connection config: " + cc);
			
			newConnectionTreeItem(cc);
		};
		createBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, createBtnPress);
		createBtnCtr.getChildren().add(createBtn);
		
		hostnameCtr.getChildren().addAll(hostnameLbl, hostnameTxt);
		portCtr.getChildren().addAll(portLbl, portTxt);
		userCtr.getChildren().addAll(userLbl, userTxt);
		passCtr.getChildren().addAll(passLbl, passTxt);
		
		newConnectionRoot.getChildren().addAll(newConnTitleCtr,
				hostnameCtr,
				portCtr,
				userCtr,
				passCtr,
				createBtnCtr);
		
		newConnectionStage.setScene(newConnectionScene);
	}
	
	private Map<String, Query> queries = new HashMap<>();
	private Map<String, Long> doubleClick = new HashMap<>();
	private ContextMenu connectionCM;
	
	private void newConnectionCM() {
		connectionCM = new ContextMenu();
		MenuItem newDB = new MenuItem("New DB");
		newDB.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			
		});
		connectionCM.getItems().addAll(newDB);
	}
	
	private void clickedTreeItem(MouseEvent e) {
		Node node = e.getPickResult().getIntersectedNode();
	    // Accept clicks only on node cells, and not on empty spaces of the TreeView
	    if (node instanceof Text || (node instanceof TreeCell && ((TreeCell) node).getText() != null)) {
	        String name = (String) ((TreeItem)connections.getSelectionModel().getSelectedItem()).getValue();
	        System.out.println("Node click: " + name);
	        if (e.getButton() == MouseButton.SECONDARY) {
	        	System.out.println("Right click");
	        	ConnectionConfig cc = dbMan.getConnectionConfig(name);
	        	if (cc != null) {
	        		System.out.println("CC exists");
	        		if (connectionCM == null) {
	        			newConnectionCM();
	        		}
	        		connectionCM.show(node, Side.RIGHT, 0, 0);
	        	}
	        	return;
	        } 
	        if (queries.containsKey(name))  {
		        if (doubleClick.containsKey(name)) {
		        	System.out.println("Double clicking...");
		        	long lastClick = doubleClick.get(name);
		        	long timePassed = System.currentTimeMillis() - lastClick;
		        	System.out.println("Time passed: " + timePassed);
		        	if (timePassed < 500) {
		        		Query query = queries.get(name);
		    	        if (query != null) {
		    	        	if (!isQueryTabOpen(name)) {
		    	        		queriesTab.getTabs().add(queryTab(name, query.getQuery()));
		    	        	}
		    	        }
		    	        doubleClick.remove(name);
		        	} else {
		        		doubleClick.remove(name);
		        		doubleClick.put(name, System.currentTimeMillis());
		        	}
		        } else {
		        	System.out.println("Single click");
		        	doubleClick.put(name, System.currentTimeMillis());
		        	return;
		        }
	        }
	    }
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
		
		connectionsRoot = new TreeItem<>("Connections");
		connections = new TreeView<>(connectionsRoot);
		connections.setMaxHeight(Double.MAX_VALUE);
		EventHandler<MouseEvent> clickedQueryItem = (MouseEvent e) -> {
			clickedTreeItem(e);
		};
		connections.addEventHandler(MouseEvent.MOUSE_CLICKED, clickedQueryItem);
		
		Collection<ConnectionConfig> conConfigs = dbMan.getConnectionConfigs();
		for (ConnectionConfig cc : conConfigs) {
			newConnectionTreeItem(cc);
		}
		
		leftPane.getChildren().addAll(newConnectionsBtn, connections);
	}
	
	private void newConnectionTreeItem(ConnectionConfig cc) {
		TreeItem<String> connItem = new TreeItem<>(cc.getHost());
		for (DB db : cc.getDbs()) {
			TreeItem<String> dbItem = new TreeItem<>(db.getDatabaseName());
			for (Report report : db.getReports()) {
				TreeItem<String> reportItem = new TreeItem<>(report.getWorkbookName());
				for (Query query : report.getQueries()) {
					String wsName = query.getWorksheetName();
					TreeItem<String> queryItem = new TreeItem<>(query.getWorksheetName());
					queries.put(wsName, query);
					reportItem.getChildren().add(queryItem);
				}
				dbItem.getChildren().add(reportItem);
			}
			connItem.getChildren().add(dbItem);
		}
		connectionsRoot.getChildren().add(connItem);
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
		
		rightPane.setDividerPosition(0, rightTopPaneHeight);
		rightPane.setOrientation(Orientation.VERTICAL);
		rightPane.getItems().addAll(topPane, queriesTab);
	}
	
	private Tab queryTab(String wsName, String query) {
		Tab queryTab = new Tab();
		queryTab.setText(wsName);
		
		TextField queryTextBox = new TextField();
		queryTextBox.setMaxWidth(Double.MAX_VALUE);
		queryTextBox.setMaxHeight(Double.MAX_VALUE);
		queryTextBox.setAlignment(Pos.TOP_LEFT);
		System.out.println(Font.getFontNames());
		queryTextBox.setText(query);
		queryTextBox.setFont(Font.loadFont(EDITOR_FONT, 14));
		queryTextBox.selectPositionCaret(0);
		queryTab.setContent(queryTextBox);
		return queryTab;
	}
	
	private boolean isQueryTabOpen(String wsName) {
		for (Tab tab : queriesTab.getTabs()) {
			if (tab.getText().equalsIgnoreCase(wsName)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		File dbFile = new File("localDBs.sqlite");
		SQLiteDatabase sqliteDb = new SQLiteDatabase(dbFile);
		dbMan = new DatabaseManager(sqliteDb);
		dbMan.loadConnectionConfigs();
		
		this.stage = stage;
		stage.setTitle("DBSC");
		
		masterPane = new SplitPane();
		masterPane.setBackground(new Background(new BackgroundFill(Color.CORNSILK, CornerRadii.EMPTY, Insets.EMPTY)));
		
		leftPane();
		rightPane();
		
		masterPane.setDividerPosition(0, leftPaneWidth);
		masterPane.getItems().addAll(leftPane, rightPane);
		
		stage.widthProperty().addListener((obs, oldVal, newVal) -> {
			onWidthResize(oldVal, newVal);
		});
		
		Scene scene = new Scene(masterPane, 800, 600);
		stage.setScene(scene);
		
		stage.show();
	}
	
	@Override
	public void stop() throws Exception {
		if (newConnectionStage != null) {
			newConnectionStage.close();
		}
		dbMan.close();
	}
	
	private void onWidthResize(Number oldVal, Number newVal) {
		masterPane.setDividerPosition(0, leftPaneWidth);
		rightPane.setDividerPosition(0, rightTopPaneHeight);
	}

}