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

	/**
	 * Scene and most-parent pane of the UI.
	 */
	private Scene newConnectionScene;
	private VBox newConnectionRoot;
	
	// initialize above.
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
	
	/**
	 * Components representing 'New Connection' title of UI.
	 */
	private HBox newConnTitleCtr;
	private Label newConnTitle;
	
	// initialize above.
	private void newConnTitle() {
		newConnTitleCtr = new HBox();
		newConnTitleCtr.setAlignment(Pos.CENTER);
		newConnTitle = new Label("New Connection");
		newConnTitle.setFont(Font.font("Arial", 20));
		newConnTitle.setAlignment(Pos.CENTER);
		newConnTitleCtr.getChildren().add(newConnTitle);
	}
	
	/**
	 * Components representing 'Hostname' input entry.
	 */
	private HBox hostnameCtr;
	private Label hostnameLbl;
	private TextField hostnameTxt;
	/**
	 * Error label; appended to the hostname container when there's a problem with the inputted hostname.
	 */
	private Label errorLbl;
	
	//initialize above.
	private void newConnHostname() {
		hostnameCtr = new HBox();
		hostnameCtr.setAlignment(Pos.CENTER_LEFT);
		hostnameLbl = new Label("Hostname: ");
		hostnameTxt = new TextField();
		hostnameTxt.setPromptText("Enter hostname");
		hostnameCtr.getChildren().addAll(hostnameLbl, hostnameTxt);
	}
	
	/**
	 * Components representing 'Create' submit button.
	 */
	private HBox createBtnCtr;
	private Button createBtn;
	
	//initialize above.
	private void createConnBtn() {
		createBtnCtr = new HBox();
		createBtnCtr.setAlignment(Pos.CENTER);
		
		createBtn = new Button("Create");
		EventHandler<MouseEvent> createBtnPress = (MouseEvent e) -> { 
			onCreateConnection(); // handle click event
		};
		createBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, createBtnPress);
		createBtnCtr.getChildren().add(createBtn);
	}
	
	/**
	 * Initialize CreateConnectionUI
	 * @param masterUI - reference to main application, passed to parent.
	 */
	public CreateConnectionUI(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		super.init();
		
		//initialize
		uiStage = initStage("Create New Connection");
		
		newConnScene();
		
		newConnTitle();
		
		newConnHostname();
		
		createConnBtn();
		
		newConnectionRoot.getChildren().addAll(newConnTitleCtr,
				hostnameCtr,
				createBtnCtr);
		
		// If enter is pressed while focused on this UI, it will invoke the functionality of the 'Create' button.
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
	
	/**
	 * IP Address Regular Expression.
	 */
	private static final String IP_REGEX = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	/**
	 * Called when activating the 'Create' button or pressing enter.
	 * 
	 * Validates inputs, tries to create connection config, reacts accordingly to such attempt.
	 */
	private void onCreateConnection() {
		DatabaseManager dbMan = masterUI.getDatabaseManager();
		
		String hostname = hostnameTxt.getText();
		
		if (Validation.sanityString(hostname)) {
			Validation.alert("Hostname can't be left empty."); // Notify user of mistake.
			return;
		}
		
		// Hostname can't be null.
		// Ensure that the hostname is either 'localhost' or a valid ip address.
		// Uses regex to check whether it is a valid ip address or not.
		if (!hostname.equalsIgnoreCase("localhost") && !hostname.matches(IP_REGEX)) {
			Validation.alert("Hostname needs to be localhost or an ip address."); // Notify user of mistake.
			return;
		}
		
		ConnectionConfig.CreationResult cr = dbMan.createConnectionConfig(hostname); // Create connection config.
		if (cr.getResult() != Result.SUCCESS) { // Something went wrong.
			if (errorLbl == null) { // Initialize error label.
				errorLbl = new Label();
				errorLbl.setTextFill(Color.RED);
			}
			hostnameTxt.setBorder(new Border(new BorderStroke(Color.RED, 
		            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			
			// Set error label text accordingly.
			if (cr.getResult() == Result.ALREADY_EXIST) { 
				errorLbl.setText(" * Already exists.");
			} else if (cr.getResult() == Result.CANT_CONTACT) {
				errorLbl.setText(" * Can't contact.");
			}
			if (!hostnameCtr.getChildren().contains(errorLbl)) { // Add error label to UI.
				hostnameCtr.getChildren().add(errorLbl);
			}
			return;
		}
		
		ConnectionConfig cc = cr.getConnectionConfig();
		
		System.out.println("Creating new connection config: " + cc); //debug
		
		LeftPane leftPane = masterUI.getLeftPane();
		leftPane.getConnections().newConnectionTreeItem(cc); // Add this new connection config item to the connection tree view.
		
		hostnameTxt.clear(); // Clean up
		close();
	}

}
