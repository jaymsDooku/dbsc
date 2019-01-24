package io.jayms.dbsc.ui;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class RegisterDatabaseUI extends StandaloneUIModule {

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
	
	private HBox registerDBBtnCtr;
	private Button registerDBBtn;
	
	public RegisterDatabaseUI(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
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
		
		dbNameLbl = new Label("DB Name: ");
		dbTypeLbl = new Label("DB Type: ");
		dbFileChooserLbl = new Label("DB File: ");
	
		dbNameTxt = new TextField();
		dbNameTxt.setPromptText("Enter DB name");
		
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
			
			if (dbType == DBType.SQLITE) {
				newDBRoot.getChildren().add(3, dbFileChooserCtr);
			}
		});
		
		registerDBBtnCtr = new HBox();
		registerDBBtnCtr.setAlignment(Pos.CENTER);
		registerDBBtn = new Button("Register Database");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			String dbName = dbNameTxt.getText();
			DBType dbType = DBType.valueOf(dbTypeCmb.getSelectionModel().getSelectedItem().toUpperCase());
			ConnectionConfig selectedConnectionConfig = masterUI.getLeftPane().getSelectedConnectionConfig();
			if (selectedConnectionConfig == null) {
				System.out.println("Tried to register database but no selected connection config!");
				return;
			}
			DB db;
			if (dbType == DBType.SQLITE) {
				File dbFile = this.dbFileChosen;
				if (!dbFile.getPath().endsWith(".sqlite")) {
					System.out.println("DB File needs to have a .sqlite extension.");
					return;
				}
				db = new DB(selectedConnectionConfig, dbName, dbFile);
			} else {
				db = new DB(selectedConnectionConfig, dbName, dbType);
			}
			System.out.println("Creating new database: " + db);
			close();
		};
		registerDBBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		registerDBBtnCtr.getChildren().add(registerDBBtn);
		
		dbNameCtr.getChildren().addAll(dbNameLbl, dbNameTxt);
		dbTypeCtr.getChildren().addAll(dbTypeLbl, dbTypeCmb);
		dbFileChooserCtr.getChildren().addAll(dbFileChooserLbl, dbFileChooserPath, dbFileChooserBtn);
		
		newDBRoot.getChildren().addAll(newDBTitleCtr,
				dbNameCtr,
				dbTypeCtr,
				registerDBBtnCtr);
		
		uiStage.setScene(newDBScene);
	}
	
	@Override
	public void show() {
		super.show();
		ConnectionConfig selectedCC = masterUI.getLeftPane().getSelectedConnectionConfig();
		for (DBType dbType : DBType.values()) {
			if (dbType == DBType.SQLITE && !selectedCC.isLocalHost()) {
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

}
