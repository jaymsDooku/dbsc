package io.jayms.dbsc.ui.comp;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

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
import io.jayms.dbsc.util.ComponentFactory;
import io.jayms.dbsc.util.Validation;
import io.jayms.xlsx.db.DBTools;
import io.jayms.xlsx.db.Database;
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
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

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
			if (tabDataObj == null || !(tabDataObj instanceof Query)) {
				System.out.println("No tab editor data held");
				return;
			}
			Query query = (Query) tabDataObj;
			
			QueryTextEditor tabTextField = (QueryTextEditor) curTab.getContent();
			String tabQueryText = tabTextField.getText();
			query.setQuery(tabQueryText);
			
			QueryTaskMaster taskMaster = masterUI.getQueryTaskMaster();
			int queryTaskId = taskMaster.startQuery(query, chosenFile);
			QueryTask queryTask = taskMaster.getQueryTask(queryTaskId);
		});
		
		queryOptionsBtn = ComponentFactory.createButton("Query Options", (e) -> {
			TabPane queriesTab = masterUI.getRightPane().getQueriesTab();
			Tab curTab = queriesTab.getSelectionModel().getSelectedItem();
			if (curTab == null) {
				Validation.alert("You need to open a query tab first!");
				return;
			}
			
			Object tabDataObj = curTab.getUserData();
			if (tabDataObj == null || !(tabDataObj instanceof Query)) {
				System.out.println("No tab editor data held");
				return;
			}
			Query query = (Query) tabDataObj;
			
			if (query.isEmpty()) {
				Validation.alert("You need to write a query first.");
				return;
			}
			
			String queryContent = query.getQuery();
			
			try {
				Statement stmt = CCJSqlParserUtil.parse(queryContent);
				if (!(stmt instanceof Select)) {
					Validation.alert("Must be a select statement.");
					return;
				}
				
				StringBuilder selectFields = new StringBuilder();
				
				Select selectStmt = (Select) stmt;
				PlainSelect selectBody = (PlainSelect) selectStmt.getSelectBody();
				List<SelectItem> selectItems = selectBody.getSelectItems();
				for (int i = 0; i < selectItems.size(); i++) {
					SelectItem selectItem = selectItems.get(i);
					selectFields.append(selectItem.toString());
					if (i < selectItems.size() - 1) {
						selectFields.append(", ");
					}
				}
				
				StringBuilder tableParts = new StringBuilder();
				FromItem fromItem = selectBody.getFromItem();
				tableParts.append(fromItem);
				List<Join> joins = selectBody.getJoins();
				if (joins != null && !joins.isEmpty()) joins.stream().forEach(j -> tableParts.append(" " + j));
				
				String scoutQuery = "SELECT " + selectFields.toString() + " FROM "  + tableParts + " WHERE 1<0";
				
				DatabaseManager dbManager = masterUI.getDatabaseManager();
				Database connDB = dbManager.getDatabaseConnection(query.getReport().getDb());
				Connection conn = connDB.getConnection();
				try {
					ResultSet rs = conn.createStatement().executeQuery(scoutQuery);
					ResultSetMetaData meta = rs.getMetaData();
					DatabaseColumn[] dbCols = DBTools.getDatabaseColumns(meta);
					new QueryOptionsUI(masterUI, query, dbCols).show();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} catch (JSQLParserException e1) {
				Validation.alert("Failed to parse query.");
			}
		});
		
		statusLbl();
		
		stopQueryBtn = new Button("Stop Query");
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
