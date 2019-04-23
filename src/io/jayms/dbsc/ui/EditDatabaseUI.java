package io.jayms.dbsc.ui;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class EditDatabaseUI extends StandaloneUIModule {
	
	private final DB selectedDB;
	
	private Scene editDBScene;
	private VBox editDBRoot;
	
	private HBox editDBTitleCtr;
	private Label editDBTitle;
	
	private HBox dbFileChooserCtr;
	private Label dbFileChooserLbl;
	private TextField dbFileChooserPath;
	private Button dbFileChooserBtn;
	private FileChooser dbFileChooser;
	private File dbFileChosen;
	
	private HBox editDBBtnCtr;
	private Button editDBBtn;
	
	public EditDatabaseUI(DBSCGraphicalUserInterface masterUI, DB selectedDB) {
		super(masterUI);
		this.selectedDB = selectedDB;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Database");
		
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		editDBRoot = new VBox();
		editDBRoot.setSpacing(10);
		rootCtr.getChildren().add(editDBRoot);
		root.getChildren().add(rootCtr);
		editDBScene = new Scene(root, 400, 300);
		
		editDBTitleCtr = new HBox();
		editDBTitleCtr.setAlignment(Pos.CENTER);
		editDBTitle = new Label("Edit Database");
		editDBTitle.setFont(Font.font("Arial", 20));
		editDBTitle.setAlignment(Pos.CENTER);
		editDBTitleCtr.getChildren().add(editDBTitle);
		
		dbFileChooserCtr = new HBox();
		dbFileChooserCtr.setAlignment(Pos.CENTER_LEFT);
		
		dbFileChooserLbl = new Label("DB File: ");
		
		dbFileChooser = new FileChooser();
		setFilePathDisplay(selectedDB.getSqliteDBFile());
		
		ExtensionFilter extFilter = new ExtensionFilter("SQLite Database (*.sqlite)", "*.sqlite");
		dbFileChooser.getExtensionFilters().add(extFilter);
		
		dbFileChooser.setSelectedExtensionFilter(extFilter);
		dbFileChooserPath = new TextField();
		dbFileChooserPath.setText(dbFileChooser.getInitialDirectory().getAbsolutePath() + "\\" + dbFileChooser.getInitialFileName());
		dbFileChooserPath.setEditable(false);
		dbFileChooserBtn = new Button("Browse");
		dbFileChooserBtn.setOnMouseClicked((e) -> {
			dbFileChosen = dbFileChooser.showOpenDialog(masterUI.getStage());
			setFilePathDisplay(dbFileChosen);
		});
		
		editDBBtnCtr = new HBox();
		editDBBtnCtr.setAlignment(Pos.CENTER);
		editDBBtn = new Button("Edit Database");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onEditDB();
		};
		editDBBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		editDBBtnCtr.getChildren().add(editDBBtn);
		
		dbFileChooserCtr.getChildren().addAll(dbFileChooserLbl, dbFileChooserPath, dbFileChooserBtn);
		
		editDBRoot.getChildren().addAll(editDBTitleCtr,
				dbFileChooserCtr,
				editDBBtnCtr);
	
		editDBScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onEditDB();
				}
			}
		});
		uiStage.setScene(editDBScene);
	}
	
	private void setFilePathDisplay(File file) {
		if (dbFileChooser == null) return;
		
		File initialDir = file != null ? file.getParentFile() : new File(System.getProperty("user.dir"));
		String fileName = file != null ? file.getName() : null;
		dbFileChooser.setInitialDirectory(initialDir);
		if (fileName != null) {
			dbFileChooser.setInitialFileName(fileName);
		}
	}
	
	private void onEditDB() {
		selectedDB.setSqliteDBFile(dbFileChosen);
		System.out.println("Set DB File of " + selectedDB + " to " + dbFileChosen);
	}
}
