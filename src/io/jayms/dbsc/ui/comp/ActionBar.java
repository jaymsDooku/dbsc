package io.jayms.dbsc.ui.comp;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.task.QueryTask;
import io.jayms.dbsc.task.QueryTaskMaster;
import io.jayms.dbsc.ui.AbstractUIModule;
import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;

public class ActionBar extends AbstractUIModule {

	@Getter private HBox actionBar;
	private Button runQueryBtn;
	private Button stopQueryBtn;
	private Button openQueryBuilderBtn;
	
	private void openQueryBuilderBtn() {
		openQueryBuilderBtn = new Button("Open Query Builder");
		openQueryBuilderBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			openQueryBuilder();
		});
	}
	
	@Getter private ProgressBar queryProgressBar = new ProgressBar();
	@Getter private ProgressIndicator queryProgressIndicator = new ProgressIndicator();
	
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
		if (!(tabUserData instanceof Query)) {
			Alert alert = new Alert(AlertType.ERROR, "You need to have a query open!", ButtonType.OK);
			alert.showAndWait();
			return;
		}
		
		Query query = (Query) tabUserData;
		Report report = query.getReport();
		DB selectedDB = report.getDb();
		QueryBuilderUI queryBuilderUI = new QueryBuilderUI(masterUI, selectedDB);
		queryBuilderUI.show();
	}
	
	@Override
	public void init() {
		super.init();
		
		actionBar = new HBox();
		
		runQueryBtn = ComponentFactory.createButton("Run Query", (e) -> {
			File chosenFile = masterUI.getRightPane().getChosenFile();
			TabPane queriesTab = masterUI.getRightPane().getQueriesTab();
			
			if (chosenFile == null) {
				Alert alert = new Alert(AlertType.ERROR, "You need to select a file destination first!", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			Tab curTab = queriesTab.getSelectionModel().getSelectedItem();
			if (curTab == null) {
				Alert alert = new Alert(AlertType.ERROR, "You need to open a query tab first!", ButtonType.OK);
				alert.showAndWait();
				return;
			}
			Object tabDataObj = curTab.getUserData();
			if (tabDataObj == null || !(tabDataObj instanceof Query)) {
				System.out.println("No tab editor data held");
				return;
			}
			Query query = (Query) tabDataObj;
			
			TextField tabTextField = (TextField) curTab.getContent();
			String tabQueryText = tabTextField.getText();
			query.setQuery(tabQueryText);
			
			QueryTaskMaster taskMaster = masterUI.getQueryTaskMaster();
			int queryTaskId = taskMaster.startQuery(query, chosenFile);
			QueryTask queryTask = taskMaster.getQueryTask(queryTaskId);
			
			queryProgressBar.progressProperty().bind(queryTask.progressProperty());
			queryProgressIndicator.progressProperty().bind(queryTask.progressProperty());
			actionBar.getChildren().addAll(queryProgressBar, queryProgressIndicator);
		});
		
		stopQueryBtn = new Button("Stop Query");
		openQueryBuilderBtn();
		actionBar.getChildren().addAll(runQueryBtn, stopQueryBtn, openQueryBuilderBtn);
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void close() {
		
	}
}
