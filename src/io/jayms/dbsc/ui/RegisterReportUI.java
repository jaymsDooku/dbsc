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
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class RegisterReportUI extends StandaloneUIModule {

	private final DB selectedDB;
	
	private Scene newReportScene;
	private VBox newReportRoot;
	
	private HBox newReportTitleCtr;
	private Label newReportTitle;
	
	private HBox reportNameCtr;
	private Label reportNameLbl;
	private TextField reportNameTxt;
	
	public RegisterReportUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.selectedDB = db;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Database");
		
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newReportRoot = new VBox();
		newReportRoot.setSpacing(10);
		rootCtr.getChildren().add(newReportRoot);
		root.getChildren().add(rootCtr);
		newReportScene = new Scene(root, 400, 300);
		
		newReportTitleCtr = new HBox();
		newReportTitleCtr.setAlignment(Pos.CENTER);
		newReportTitle = new Label("New Report");
		newReportTitle.setFont(Font.font("Arial", 20));
		newReportTitle.setAlignment(Pos.CENTER);
		newReportTitleCtr.getChildren().add(newReportTitle);
		
		reportNameCtr = new HBox();
		reportNameCtr.setAlignment(Pos.CENTER_LEFT);
		
		reportNameLbl = new Label("Report Name: ");
	
		reportNameTxt = new TextField();
		reportNameTxt.setPromptText("Enter report name");
		
		reportNameCtr.getChildren().addAll(reportNameLbl, reportNameTxt);
		
		newReportRoot.getChildren().addAll(newReportTitleCtr,
				reportNameCtr);
	
		newReportScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onRegisterDB();
				}
			}
		});
		uiStage.setScene(newReportScene);
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	private void onRegisterDB() {
		String reportName = reportNameTxt.getText();
		LeftPane leftPane = masterUI.getLeftPane();
		ConnectionTreeView connTreeView = leftPane.getConnections();
		
		TreeItem<DBSCTreeItem> ccTreeItem = connTreeView.getConnectionTreeItem(selectedDB.getConnConfig());
		
		
		System.out.println("Creating new report: " + reportName);
		close();
	}

}
