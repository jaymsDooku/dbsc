package io.jayms.dbsc.ui;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.ui.comp.ConnectionTreeView;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.DBTreeItem;
import io.jayms.dbsc.util.Validation;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class RegisterDatabaseUI extends StandaloneUIModule {

	private final ConnectionConfig selectedConnConfig;
	
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
	
	private HBox dbFileChooserCtr;
	private Label dbFileChooserLbl;
	private TextField dbFileChooserPath;
	private Button dbFileChooserBtn;
	private FileChooser dbFileChooser;
	private File dbFileChosen;
	
	private HBox dbServerNameCtr;
	private Label dbServerNameLbl;
	private TextField dbServerNameTxt;
	
	private HBox registerDBBtnCtr;
	private Button registerDBBtn;
	
	public RegisterDatabaseUI(DBSCGraphicalUserInterface masterUI, ConnectionConfig connConfig) {
		super(masterUI);
		this.selectedConnConfig = connConfig;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Database");
		
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
		dbNameCtr.setAlignment(Pos.CENTER_LEFT);
		dbTypeCtr = new HBox();
		dbTypeCtr.setAlignment(Pos.CENTER_LEFT);
		dbFileChooserCtr = new HBox();
		dbFileChooserCtr.setAlignment(Pos.CENTER_LEFT);
		dbServerNameCtr = new HBox();
		dbServerNameCtr.setAlignment(Pos.CENTER_LEFT);
		
		dbNameLbl = new Label("DB Name: ");
		dbTypeLbl = new Label("DB Type: ");
		dbFileChooserLbl = new Label("DB File: ");
		dbServerNameLbl = new Label("Server Name: ");
	
		dbNameTxt = new TextField();
		dbNameTxt.setPromptText("Enter DB name");
		
		dbServerNameTxt = new TextField();
		dbServerNameTxt.setPromptText("Enter server name");
		
		dbFileChooser = new FileChooser();
		dbFileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
		ExtensionFilter extFilter = new ExtensionFilter("SQLite Database (*.sqlite)", "*.sqlite");
		dbFileChooser.getExtensionFilters().add(extFilter);
		
		dbFileChooser.setSelectedExtensionFilter(extFilter);
		dbFileChooserPath = new TextField();
		dbFileChooserPath.setText(dbFileChooser.getInitialDirectory().getAbsolutePath());
		dbFileChooserPath.setEditable(false);
		dbFileChooserBtn = new Button("Browse");
		dbFileChooserBtn.setOnMouseClicked((e) -> {
			dbFileChosen = dbFileChooser.showOpenDialog(masterUI.getStage());
		});
		
		dbTypeCmb = new ComboBox<>();
		dbTypeCmb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			DBType dbType = DBType.valueOf(newValue.toUpperCase());
			
			ObservableList<Node> children = newDBRoot.getChildren();
			if (dbType == DBType.SQLITE) {
				if (children.contains(dbServerNameCtr)) {
					children.remove(dbServerNameCtr);
				}
				children.add(3, dbFileChooserCtr);
			} else {
				if (children.contains(dbFileChooserCtr)) {
					newDBRoot.getChildren().remove(dbFileChooserCtr);
				}
				children.add(3, dbServerNameCtr);
			}
		});
		
		registerDBBtnCtr = new HBox();
		registerDBBtnCtr.setAlignment(Pos.CENTER);
		registerDBBtn = new Button("Register Database");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onRegisterDB();
		};
		registerDBBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		registerDBBtnCtr.getChildren().add(registerDBBtn);
		
		dbNameCtr.getChildren().addAll(dbNameLbl, dbNameTxt);
		dbTypeCtr.getChildren().addAll(dbTypeLbl, dbTypeCmb);
		dbFileChooserCtr.getChildren().addAll(dbFileChooserLbl, dbFileChooserPath, dbFileChooserBtn);
		dbServerNameCtr.getChildren().addAll(dbServerNameLbl, dbServerNameTxt);
		
		newDBRoot.getChildren().addAll(newDBTitleCtr,
				dbNameCtr,
				dbTypeCtr,
				registerDBBtnCtr);
	
		newDBScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onRegisterDB();
				}
			}
		});
		uiStage.setScene(newDBScene);
	}
	
	@Override
	public void show() {
		super.show();
		for (DBType dbType : DBType.values()) {
			if (dbType == DBType.SQLITE && !selectedConnConfig.isLocalHost()) {
				continue;
			}
			dbTypeCmb.getItems().add(dbType.toString().toLowerCase());
		}
		if (!dbTypeCmb.getItems().isEmpty()) {
			dbTypeCmb.getSelectionModel().select(0);
		}
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	private void onRegisterDB() {
		String dbName = dbNameTxt.getText();
		
		if (Validation.sanityString(dbName)) {
			Validation.alert("You need to choose a database name!");
			return;
		}
		
		DBType dbType = DBType.valueOf(dbTypeCmb.getSelectionModel().getSelectedItem().toUpperCase());
		
		if (dbType == null) {
			Validation.alert("You need to choose a database type!");
			return;
		}
		
		LeftPane leftPane = masterUI.getLeftPane();
		ConnectionTreeView connTreeView = leftPane.getConnections();
		DB db;
		if (dbType == DBType.SQLITE) {
			File dbFile = this.dbFileChosen;
			if (dbFile == null) {
				Validation.alert("You need to select a database file!");
				return;
			}
			if (!dbFile.getPath().endsWith(".sqlite")) {
				Validation.alert("DB File needs to have a .sqlite extension.");
				return;
			}
			db = new DB(selectedConnConfig, dbName, dbFile);
		} else if (dbType == DBType.SQL_SERVER || dbType == DBType.ORACLE) {
			String serverName = dbServerNameTxt.getText();
			if (Validation.sanityString(serverName)) {
				Validation.alert("You need to choose a server name!");
				return;
			}
			db = new DB(selectedConnConfig, dbName, serverName, dbType);
		} else {
			db = new DB(selectedConnConfig, dbName, dbType);
		}
		selectedConnConfig.getDbs().add(db);
		
		TreeItem<DBSCTreeItem> ccTreeItem = connTreeView.getConnectionTreeItem(selectedConnConfig);
		TreeItem<DBSCTreeItem> dbTreeItem = new TreeItem<>(new DBTreeItem(masterUI, db));
		ccTreeItem.getChildren().add(dbTreeItem);
		
		System.out.println("Creating new database: " + db);
		close();
	}

}
