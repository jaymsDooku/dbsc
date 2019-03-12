package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.ui.comp.ConnectionTreeView;
import io.jayms.dbsc.ui.comp.LeftPane;
import io.jayms.dbsc.ui.comp.treeitem.DBSCTreeItem;
import io.jayms.dbsc.ui.comp.treeitem.QueryTreeItem;
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

public class NewQueryUI extends StandaloneUIModule {

	private final Report selectedReport;
	
	private Scene newQueryScene;
	private VBox newQueryRoot;
	
	private HBox newQueryTitleCtr;
	private Label newQueryTitle;
	
	private HBox queryNameCtr;
	private Label queryNameLbl;
	private TextField queryNameTxt;
	
	private HBox newQueryBtnCtr;
	private Button newQueryBtn;
	
	public NewQueryUI(DBSCGraphicalUserInterface masterUI, Report report) {
		super(masterUI);
		this.selectedReport = report;
	}
	
	@Override
	public void init() {
		super.init();
		
		uiStage = initStage("Register New Query");
		
		VBox root = new VBox();
		HBox rootCtr = new HBox();
		root.setAlignment(Pos.CENTER);
		rootCtr.setAlignment(Pos.CENTER);
		newQueryRoot = new VBox();
		newQueryRoot.setSpacing(10);
		rootCtr.getChildren().add(newQueryRoot);
		root.getChildren().add(rootCtr);
		newQueryScene = new Scene(root, 300, 200);
		
		newQueryTitleCtr = new HBox();
		newQueryTitleCtr.setAlignment(Pos.CENTER);
		newQueryTitle = new Label("New Query");
		newQueryTitle.setFont(Font.font("Arial", 20));
		newQueryTitle.setAlignment(Pos.CENTER);
		newQueryTitleCtr.getChildren().add(newQueryTitle);
		
		queryNameCtr = new HBox();
		queryNameCtr.setAlignment(Pos.CENTER_LEFT);
		
		queryNameLbl = new Label("Query Name: ");
	
		queryNameTxt = new TextField();
		queryNameTxt.setPromptText("Enter query name");
		
		queryNameCtr.getChildren().addAll(queryNameLbl, queryNameTxt);
		
		newQueryBtnCtr = new HBox();
		newQueryBtnCtr.setAlignment(Pos.CENTER);
		newQueryBtn = new Button("New Query");
		EventHandler<MouseEvent> registerBtnPress = (MouseEvent e) -> {
			onNewQuery();
		};
		newQueryBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, registerBtnPress);
		newQueryBtnCtr.getChildren().add(newQueryBtn);
		
		newQueryRoot.getChildren().addAll(newQueryTitleCtr,
				queryNameCtr, newQueryBtnCtr);
	
		newQueryScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					onNewQuery();
				}
			}
		});
		uiStage.setScene(newQueryScene);
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void close() {
		super.close();
	}
	
	private void onNewQuery() {
		String queryName = queryNameTxt.getText();
		LeftPane leftPane = masterUI.getLeftPane();
		ConnectionTreeView connTreeView = leftPane.getConnections();
		
		Query query = new Query(selectedReport, queryName, "");
		
		TreeItem<DBSCTreeItem> reportTreeItem = connTreeView.getReportTreeItem(selectedReport);
		TreeItem<DBSCTreeItem> queryTreeItem = new TreeItem<>(new QueryTreeItem(masterUI, query));
		reportTreeItem.getChildren().add(queryTreeItem);
		
		connTreeView.getQueries().put(queryName, query);
		
		selectedReport.getQueries().add(query);
		
		System.out.println("Creating new query: " + queryName);
		close();
	}

}
