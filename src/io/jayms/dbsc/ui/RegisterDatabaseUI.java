package io.jayms.dbsc.ui;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.ui.comp.ConnectionTreeView;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.NumberField;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.DBTreeItem;
import io.jayms.dbsc.util.Validation;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
	
	private void newDBScene() {
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newDBRoot = new VBox();
		newDBRoot.setSpacing(10);
		rootCtr.getChildren().add(newDBRoot);
		root.getChildren().add(rootCtr);
		newDBScene = new Scene(root, 400, 300);
	}
	
	private HBox newDBTitleCtr;
	private Label newDBTitle;
	
	private void newDBTitle() {
		newDBTitleCtr = new HBox();
		newDBTitleCtr.setAlignment(Pos.CENTER);
		newDBTitle = new Label("New Database");
		newDBTitle.setFont(Font.font("Arial", 20));
		newDBTitle.setAlignment(Pos.CENTER);
		newDBTitleCtr.getChildren().add(newDBTitle);
	}
	
	private HBox dbNameCtr;
	private Label dbNameLbl;
	private TextField dbNameTxt;
	
	private void newDBName() {
		dbNameCtr = new HBox();
		dbNameCtr.setAlignment(Pos.CENTER_LEFT);
		dbNameLbl = new Label("DB Name: ");
		dbNameTxt = new TextField();
		dbNameTxt.setPromptText("Enter DB name");
		dbNameCtr.getChildren().addAll(dbNameLbl, dbNameTxt);
	}
	
	private HBox dbTypeCtr;
	private Label dbTypeLbl;
	private ComboBox<String> dbTypeCmb;
	
	private void newDBType() {
		dbTypeCtr = new HBox();
		dbTypeCtr.setAlignment(Pos.CENTER_LEFT);
		
		dbTypeLbl = new Label("DB Type: ");
		
		dbTypeCmb = new ComboBox<>();
		dbTypeCmb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			DBType dbType = DBType.valueOf(newValue.toUpperCase());
			
			if (selectedDBType == dbType) return;
			
			ObservableList<Node> children = newDBRoot.getChildren();
			if (dbType == DBType.SQLITE) {
				if (children.contains(dbServerNameCtr)) {
					children.remove(dbServerNameCtr);
				}
				if (children.contains(portCtr) ) {
					children.remove(portCtr);
				}
				if (children.contains(userCtr) ) {
					children.remove(userCtr);
				}
				if (children.contains(passCtr) ) {
					children.remove(passCtr);
				}
				
				children.add(3, dbFileChooserCtr);
				dbNameLbl.setText("Database Name: ");
			} else {
				if (children.contains(dbFileChooserCtr)) {
					newDBRoot.getChildren().remove(dbFileChooserCtr);
				}
				
				if (dbType == DBType.ORACLE) {
					if (children.contains(dbServerNameCtr)) {
						children.remove(dbServerNameCtr);
					}
					dbNameLbl.setText("Schema Name: ");
				}
			
				int portIndex = 3;
				int userIndex = 4;
				int passIndex = 5;
				if (dbType == DBType.SQL_SERVER) {
					children.add(3, dbServerNameCtr);
					portIndex++;
					userIndex++;
					passIndex++;
				}
				if (!children.contains(portCtr)) {
					children.add(portIndex, portCtr);
				}
				if (!children.contains(userCtr)) {
					children.add(userIndex, userCtr);
				}
				if (!children.contains(passCtr)) {
					children.add(passIndex, passCtr);
				}
			}
			
			selectedDBType = dbType;
		});
		
		dbTypeCtr.getChildren().addAll(dbTypeLbl, dbTypeCmb);
	}
	
	private HBox dbFileChooserCtr;
	private Label dbFileChooserLbl;
	private TextField dbFileChooserPath;
	private Button dbFileChooserBtn;
	private FileChooser dbFileChooser;
	private File dbFileChosen;
	
	private void newDBFileChooser() {
		dbFileChooserCtr = new HBox();
		dbFileChooserCtr.setAlignment(Pos.CENTER_LEFT);
		
		dbFileChooserLbl = new Label("DB File: ");
		
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
		
		dbFileChooserCtr.getChildren().addAll(dbFileChooserLbl, dbFileChooserPath, dbFileChooserBtn);
	}
	
	private HBox portCtr;
	private Label portLbl;
	private NumberField portTxt;
	
	private void newDBPort() {
		portCtr = new HBox();
		portCtr.setAlignment(Pos.CENTER_LEFT);
		portLbl = new Label("Port: ");
		portTxt = new NumberField();
		portTxt.setPromptText("Enter port");
		portCtr.getChildren().addAll(portLbl, portTxt);
	}
	
	private HBox userCtr;
	private Label userLbl;
	private TextField userTxt;
	
	private void newDBUser() {
		userCtr = new HBox();
		userCtr.setAlignment(Pos.CENTER_LEFT);
		userLbl = new Label("User: ");
		userTxt = new TextField();
		userTxt.setPromptText("Enter user");
		userCtr.getChildren().addAll(userLbl, userTxt);
	}
	
	private HBox passCtr;
	private Label passLbl;
	private PasswordField passTxt;
	
	private void newDBPass() {
		passCtr = new HBox();
		passCtr.setAlignment(Pos.CENTER_LEFT);
		passLbl = new Label("Pass: ");
		passTxt = new PasswordField();
		passTxt.setPromptText("Enter pass");
		passCtr.getChildren().addAll(passLbl, passTxt);
	}
	
	private HBox dbServerNameCtr;
	private Label dbServerNameLbl;
	private TextField dbServerNameTxt;
	
	private void newDBServerName() {
		dbServerNameCtr = new HBox();
		dbServerNameCtr.setAlignment(Pos.CENTER_LEFT);
		
		dbServerNameLbl = new Label("Server Name: ");
		
		dbServerNameTxt = new TextField();
		dbServerNameTxt.setPromptText("Enter server name");
		
		dbServerNameCtr.getChildren().addAll(dbServerNameLbl, dbServerNameTxt);
	}
	
	private HBox registerDBBtnCtr;
	private Button registerDBBtn;
	
	private void registerDBBtn() {
		registerDBBtnCtr = new HBox();
		registerDBBtnCtr.setAlignment(Pos.CENTER);
		registerDBBtn = new Button("Register Database");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onRegisterDB();
		};
		registerDBBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		registerDBBtnCtr.getChildren().add(registerDBBtn);
	}
	
	public RegisterDatabaseUI(DBSCGraphicalUserInterface masterUI, ConnectionConfig connConfig) {
		super(masterUI);
		this.selectedConnConfig = connConfig;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Database");
		
		newDBScene();
		
		newDBTitle();
		
		newDBName();
		newDBType();
		newDBFileChooser();
		newDBServerName();
		newDBPort();
		newDBUser();
		newDBPass();
		
		registerDBBtn();
		
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
	
	private DBType selectedDBType;
	
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
			selectedDBType = DBType.valueOf(dbTypeCmb.getSelectionModel().getSelectedItem().toUpperCase());
		}
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	/**
	 * Called upon clicking 'Register Database' or pressing enter.
	 * 
	 * Retrieves user input, validates it where needed, and then creates new database item.
	 */
	private void onRegisterDB() {
		String dbName = dbNameTxt.getText();
		
		if (Validation.sanityString(dbName)) {
			Validation.alert("You need to choose a database name!");
			return;
		}
		
		if (selectedConnConfig.hasDB(dbName)) {
			Validation.alert("This connection config already has a database with that name!");
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
		} else {
			String serverName = null;
			if (dbType == DBType.SQL_SERVER) {
				serverName = dbServerNameTxt.getText(); // if you're not connecting to a sql server then don't need to specify one
			}
			
			int port = 0;
			boolean invalidPort = false;
			try {
				port = portTxt.getValue();
				
				if (port < 0 || port > 65535) {
					invalidPort = true;
				}
			} catch (NumberFormatException e) {
				invalidPort = true;
			}
			if (invalidPort) {
				Validation.alert("Port needs to be an integer between 0 - 65535.");
				return;
			}
			String user = userTxt.getText();
			if (Validation.sanityString(user)) {
				Validation.alert("Username cannot be left empty.");
				return;
			}
			
			String pass = passTxt.getText();
			if (Validation.sanityString(pass)) {
				Validation.alert("Password cannot be left empty.");
				return;
			}
			
			db = (dbType == DBType.SQL_SERVER) ? new DB(selectedConnConfig, dbName, port, user, pass, serverName) :
				new DB(selectedConnConfig, dbName, port, user, pass);
		}
		
		selectedConnConfig.getDbs().add(db);
		
		TreeItem<DBSCTreeItem> ccTreeItem = connTreeView.getConnectionTreeItem(selectedConnConfig);
		TreeItem<DBSCTreeItem> dbTreeItem = new TreeItem<>(new DBTreeItem(masterUI, db));
		ccTreeItem.getChildren().add(dbTreeItem);
		
		System.out.println("Creating new database: " + db);
		close();
	}

}
