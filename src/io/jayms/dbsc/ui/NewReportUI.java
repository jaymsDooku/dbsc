package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.comp.ConnectionTreeView;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.ReportTreeItem;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class NewReportUI extends StandaloneUIModule {

	private final DB selectedDB;
	
	private Scene newReportScene;
	private VBox newReportRoot;
	
	private HBox newReportTitleCtr;
	private Label newReportTitle;
	
	private HBox reportNameCtr;
	private Label reportNameLbl;
	private TextField reportNameTxt;
	
	private HBox newReportBtnCtr;
	private Button newReportBtn;
	
	public NewReportUI(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI);
		this.selectedDB = db;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Report");
		
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newReportRoot = new VBox();
		newReportRoot.setSpacing(10);
		rootCtr.getChildren().add(newReportRoot);
		root.getChildren().add(rootCtr);
		newReportScene = new Scene(root, 300, 200);
		
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
		
		newReportBtnCtr = new HBox();
		newReportBtnCtr.setAlignment(Pos.CENTER);
		newReportBtn = new Button("New Report");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onNewReport();
		};
		newReportBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		newReportBtnCtr.getChildren().add(newReportBtn);
		
		newReportRoot.getChildren().addAll(newReportTitleCtr,
				reportNameCtr, newReportBtnCtr);
	
		newReportScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onNewReport();
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
	
	private void onNewReport() {
		String reportName = reportNameTxt.getText();
		LeftPane leftPane = masterUI.getLeftPane();
		ConnectionTreeView connTreeView = leftPane.getConnections();
		
		Report report = new Report(selectedDB, reportName);
		
		TreeItem<DBSCTreeItem> dbTreeItem = connTreeView.getDatabaseTreeItem(selectedDB);
		TreeItem<DBSCTreeItem> reportTreeItem = new TreeItem<>(new ReportTreeItem(masterUI, report));
		dbTreeItem.getChildren().add(reportTreeItem);
		
		selectedDB.getReports().add(report);
		
		System.out.println("Creating new report: " + reportName);
		close();
	}

}
