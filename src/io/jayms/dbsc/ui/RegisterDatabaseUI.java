package io.jayms.dbsc.ui;

import java.util.ArrayList;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
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
import javafx.stage.Stage;

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
	
	private HBox registerDBBtnCtr;
	private Button registerDBBtn;
	
	public RegisterDatabaseUI(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = new Stage();
		uiStage.setTitle("Register New Database");
		
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
		
		registerDBBtnCtr = new HBox();
		registerDBBtnCtr.setAlignment(Pos.CENTER_RIGHT);
		registerDBBtn = new Button("Register Database");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			String dbName = dbNameTxt.getText();
			DBType dbType = DBType.valueOf(dbTypeCmb.getSelectionModel().getSelectedItem());
			DB db = new DB(dbName, dbType, new ArrayList<>());
			System.out.println("Creating new database: " + db);
			
			
		};
		registerDBBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		registerDBBtnCtr.getChildren().add(registerDBBtn);
		
		dbNameCtr.getChildren().addAll(dbNameLbl, dbNameTxt);
		dbTypeCtr.getChildren().addAll(dbTypeLbl, dbTypeCmb);
		
		newDBRoot.getChildren().addAll(dbNameCtr,
				dbTypeCtr,
				registerDBBtnCtr);
		
		uiStage.setScene(newDBScene);
	}

}
