package io.jayms.dbsc.ui.comp;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.db.DatabaseManager;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.task.QueryTask;
import io.jayms.dbsc.task.QueryTaskMaster;
import io.jayms.dbsc.ui.AbstractUIModule;
import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.ui.QueryOptionsUI;
import io.jayms.dbsc.ui.comp.treeitem.QueryTabData;
import io.jayms.dbsc.util.ComponentFactory;
import io.jayms.dbsc.util.Validation;
import io.jayms.xlsx.db.DatabaseColumn;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;

public class ActionBar extends AbstractUIModule {

	@Getter private HBox actionBar;
	private Button runQueryBtn;
	private Button queryOptionsBtn;
	private Button stopQueryBtn;
	private Button openQueryBuilderBtn;
	
	private void openQueryBuilderBtn() {
		openQueryBuilderBtn = new Button("Open Query Builder");
		openQueryBuilderBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			openQueryBuilder();
		});
	}
	
	@Getter private Label statusLbl;
	
	public void updateStatus(String status) {
		Platform.runLater(() -> {
			statusLbl.setText("Status: " + status);
		});
	}
	
	private void statusLbl() {
		statusLbl = new Label("Status: No Tasks Running");
		statusLbl.setPadding(new Insets(0, 0, 0, 10));
	}
	
	public ActionBar(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	private void openQueryBuilder() {
		RightPane rightPane = masterUI.getRightPane();
		
		Tab selectedTab = rightPane.getQueriesTab().getSelectionModel().getSelectedItem();
		if (selectedTab == null) {
			Alert alert = new Alert(AlertType.ERROR, "You need to have a editor tab open!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		
		Object tabUserData = selectedTab.getUserData();
		if (!(tabUserData instanceof QueryTabData)) {
			Alert alert = new Alert(AlertType.ERROR, "You need to have a query open!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		
		QueryTabData queryTabData = (QueryTabData) tabUserData;
		Query query = queryTabData.getQuery();
		Report report = query.getReport();
		DB selectedDB = report.getDb();
		QueryBuilderUI queryBuilderUI = new QueryBuilderUI(masterUI, selectedDB);
		queryBuilderUI.show();
	}
	
	@Override
	public void init() {
		super.init();
		
		actionBar = new HBox();
		actionBar.setAlignment(Pos.CENTER_LEFT);
		actionBar.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		runQueryBtn = ComponentFactory.createButton("Run Query", (e) -> {
			File chosenFile = masterUI.getRightPane().getChosenFile();
			TabPane queriesTab = masterUI.getRightPane().getQueriesTab();
			
			if (chosenFile == null) {
				Validation.alert("You need to select a file destination first!");
				return;
			}
			Tab curTab = queriesTab.getSelectionModel().getSelectedItem();
			if (curTab == null) {
				Validation.alert("You need to open a query tab first!");
				return;
			}
			Object tabDataObj = curTab.getUserData();
			if (tabDataObj == null || !(tabDataObj instanceof QueryTabData)) {
				System.out.println("No tab editor data held");
				return;
			}
			QueryTabData queryTabData = (QueryTabData) tabDataObj;
			Query query = queryTabData.getQuery();
			
			QueryTextEditor tabTextField = (QueryTextEditor) curTab.getContent();
			String tabQueryText = tabTextField.getText();
			query.setQuery(tabQueryText);
			
			QueryTaskMaster taskMaster = masterUI.getQueryTaskMaster();
			int queryTaskId = taskMaster.startQuery(query, chosenFile);
			QueryTask queryTask = taskMaster.getQueryTask(queryTaskId);
			queryTabData.setRunningTaskId(queryTaskId);
		});
		
		queryOptionsBtn = ComponentFactory.createButton("Query Options", (e) -> {
			TabPane queriesTab = masterUI.getRightPane().getQueriesTab();
			Tab curTab = queriesTab.getSelectionModel().getSelectedItem();
			if (curTab == null) {
				Validation.alert("You need to open a query tab first!");
				return;
			}
			
			Object tabDataObj = curTab.getUserData();
			if (tabDataObj == null || !(tabDataObj instanceof QueryTabData)) {
				System.out.println("No tab editor data held");
				return;
			}
			QueryTabData queryTabData = (QueryTabData) tabDataObj;
			Query query = queryTabData.getQuery();
			
			if (query.isEmpty()) {
				Validation.alert("You need to write a query first.");
				return;
			}
			
			DatabaseColumn[] dbCols = DatabaseManager.getTableFields(masterUI, query);
			new QueryOptionsUI(masterUI, query, dbCols).show();
		});
		
		statusLbl();
		
		stopQueryBtn = ComponentFactory.createButton("Stop Query", (e) -> {
			TabPane queriesTab = masterUI.getRightPane().getQueriesTab();
			Tab curTab = queriesTab.getSelectionModel().getSelectedItem();
			if (curTab == null) {
				Validation.alert("You need to open a query tab first!");
				return;
			}
			Object tabDataObj = curTab.getUserData();
			if (tabDataObj == null || !(tabDataObj instanceof QueryTabData)) {
				System.out.println("No tab editor data held");
				return;
			}
			QueryTabData queryTabData = (QueryTabData) tabDataObj;
			if (queryTabData.getRunningTaskId() == -1) {
				Validation.alert("No query task running.");
				return;
			}
			
			QueryTaskMaster taskMaster = masterUI.getQueryTaskMaster();
			taskMaster.stopQuery(queryTabData.getRunningTaskId());
		});
		
		openQueryBuilderBtn();
		actionBar.getChildren().addAll(runQueryBtn, queryOptionsBtn, stopQueryBtn, openQueryBuilderBtn, statusLbl);
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void close() {
		
	}
}
