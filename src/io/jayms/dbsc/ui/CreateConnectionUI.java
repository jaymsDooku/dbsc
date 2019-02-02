package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.DatabaseManager;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.ui.comp.LeftPane;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class CreateConnectionUI extends StandaloneUIModule {

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
	private PasswordField passTxt;
	
	private HBox createBtnCtr;
	private Button createBtn;
	
	public CreateConnectionUI(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Create New Connection");
		
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
		passTxt = new PasswordField();
		passTxt.setPromptText("Enter pass");
		
		createBtn = new Button("Create");
		EventHandler<MouseEvent> createBtnPress = (MouseEvent e) -> {
			onCreateConnection();
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
		ConnectionConfig cc = new ConnectionConfig(hostnameTxt.getText(),
				Integer.parseInt(portTxt.getText()),
				userTxt.getText(),
				passTxt.getText());
		dbMan.store(cc);
		
		System.out.println("Creating new connection config: " + cc);
		
		LeftPane leftPane = masterUI.getLeftPane();
		leftPane.getConnections().newConnectionTreeItem(cc);
		close();
	}

}
