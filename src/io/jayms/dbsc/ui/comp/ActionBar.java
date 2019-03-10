package io.jayms.dbsc.ui.comp;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.ui.AbstractUIModule;
import io.jayms.dbsc.ui.QueryBuilderUI;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;

public class ActionBar extends AbstractUIModule {

	@Getter private HBox actionBar;
	private Button runQueryBtn;
	private Button stopQueryBtn;
	private Button openQueryBuilderBtn;
	
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
		DB selectedDB = query.getReport().getDb();
		System.out.println("selected db: " + selectedDB);
		QueryBuilderUI queryBuilderUI = new QueryBuilderUI(masterUI, selectedDB);
		queryBuilderUI.show();
	}
	
	@Override
	public void init() {
		super.init();
		
		actionBar = new HBox();
		
		runQueryBtn = new Button("Run Query");
		runQueryBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			masterUI.getRightPane().runQuery(e);
		});
		
		stopQueryBtn = new Button("Stop Query");
		openQueryBuilderBtn = new Button("Open Query Builder");
		openQueryBuilderBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			openQueryBuilder();
		});
		actionBar.getChildren().addAll(runQueryBtn, stopQueryBtn, openQueryBuilderBtn);
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void close() {
		
	}
}
