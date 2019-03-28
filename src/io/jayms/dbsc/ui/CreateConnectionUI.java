package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.db.DatabaseManager;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.ConnectionConfig.CreationResult.Result;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.util.Validation;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class CreateConnectionUI extends StandaloneUIModule {

	private Scene newConnectionScene;
	private VBox newConnectionRoot;
	
	private void newConnScene() {
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newConnectionRoot = new VBox();
		newConnectionRoot.setSpacing(10);
		rootCtr.getChildren().add(newConnectionRoot);
		root.getChildren().add(rootCtr);
		newConnectionScene = new Scene(root, 400, 300);
	}
	
	private HBox newConnTitleCtr;
	private Label newConnTitle;
	
	private void newConnTitle() {
		newConnTitleCtr = new HBox();
		newConnTitleCtr.setAlignment(Pos.CENTER);
		newConnTitle = new Label("New Connection");
		newConnTitle.setFont(Font.font("Arial", 20));
		newConnTitle.setAlignment(Pos.CENTER);
		newConnTitleCtr.getChildren().add(newConnTitle);
	}
	
	private HBox hostnameCtr;
	private Label hostnameLbl;
	private TextField hostnameTxt;
	private Label errorLbl;
	
	private void newConnHostname() {
		hostnameCtr = new HBox();
		hostnameCtr.setAlignment(Pos.CENTER_LEFT);
		hostnameLbl = new Label("Hostname: ");
		hostnameTxt = new TextField();
		hostnameTxt.setPromptText("Enter hostname");
		hostnameCtr.getChildren().addAll(hostnameLbl, hostnameTxt);
	}
	
	private HBox createBtnCtr;
	private Button createBtn;
	
	private void createConnBtn() {
		createBtnCtr = new HBox();
		createBtnCtr.setAlignment(Pos.CENTER);
		
		createBtn = new Button("Create");
		EventHandler<MouseEvent> createBtnPress = (MouseEvent e) -> {
			onCreateConnection();
		};
		createBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, createBtnPress);
		createBtnCtr.getChildren().add(createBtn);
	}
	
	public CreateConnectionUI(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Create New Connection");
		
		newConnScene();
		
		newConnTitle();
		
		newConnHostname();
		
		createConnBtn();
		
		newConnectionRoot.getChildren().addAll(newConnTitleCtr,
				hostnameCtr,
				createBtnCtr);
		
		newConnectionScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onCreateConnection();
				}
			}
		});
		uiStage.setScene(newConnectionScene);
	}
	
	private void onCreateConnection() {
		DatabaseManager dbMan = masterUI.getDatabaseManager();
		
		String hostname = hostnameTxt.getText();
		
		if (!hostname.equalsIgnoreCase("localhost") && !hostname.matches("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")) {
			Validation.alert("Hostname needs to be localhost or an ip address.");
			return;
		}
		
		ConnectionConfig.CreationResult cr = dbMan.createConnectionConfig(hostname);
		if (cr.getResult() != Result.SUCCESS) {
			if (errorLbl == null) {
				errorLbl = new Label();
				errorLbl.setTextFill(Color.RED);
			}
			hostnameTxt.setBorder(new Border(new BorderStroke(Color.RED, 
		            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			if (cr.getResult() == Result.ALREADY_EXIST) {
				errorLbl.setText(" * Already exists.");
			} else if (cr.getResult() == Result.CANT_CONTACT) {
				errorLbl.setText(" * Can't contact.");
			}
			if (!hostnameCtr.getChildren().contains(errorLbl)) {
				hostnameCtr.getChildren().add(errorLbl);
			}
			return;
		}
		
		ConnectionConfig cc = cr.getConnectionConfig();
		
		System.out.println("Creating new connection config: " + cc);
		
		LeftPane leftPane = masterUI.getLeftPane();
		leftPane.getConnections().newConnectionTreeItem(cc);
		
		hostnameTxt.clear();
		close();
	}

}
