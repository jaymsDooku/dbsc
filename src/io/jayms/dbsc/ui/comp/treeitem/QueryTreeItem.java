package io.jayms.dbsc.ui.comp.treeitem;

import java.sql.SQLException;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.ui.comp.QueryTextEditor;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import lombok.Getter;

public class QueryTreeItem extends DBSCTreeItem {

	private static final long DOUBLE_CLICK_INTERVAL = 500;
	
	@Getter private final Query query;
	@Getter private long lastClicked = -1;
	
	public QueryTreeItem(DBSCGraphicalUserInterface masterUI, Query query) {
		super(masterUI, new Label(query.getWorksheetName()), new Button("x"));
		this.query = query;
	}
	
	@Override
	public boolean hasSubItems() {
		return false;
	}
	
	@Override
	public ContextMenu getContextMenu() {
		return newQueryCM();
	}
	
	private ContextMenu newQueryCM() {
		ContextMenu queryCM = new ContextMenu();
		MenuItem openInEditor = new MenuItem("Open In Editor");
		openInEditor.setOnAction(e -> {
			openInTab();
		});
		MenuItem deleteQuery = new MenuItem("Delete Query");
		deleteQuery.setOnAction(e -> {
			masterUI.getDatabaseManager().deleteQuery(query);
		});
		queryCM.getItems().addAll(openInEditor, deleteQuery);
		return queryCM;
	}
	
	@Override
	public void click() {
		long now = System.currentTimeMillis();
		
		if (lastClicked == -1) {
			lastClicked = now;
			return;
		}
		
		long timePassed = now - lastClicked;
		System.out.println("Time passed: " + timePassed);
		lastClicked = now;
		
		if (timePassed < DOUBLE_CLICK_INTERVAL) {
	        openInTab();
		}
	}
	
	private void openInTab() {
		if (isQueryTabOpen()) return;
		
		Tab tab = queryTab();
		masterUI.getRightPane().getQueriesTab().getTabs().add(tab);
	}
	
	private Tab queryTab() {
		String wsName = query.getWorksheetName();
		
		Tab queryTab = new Tab();
		queryTab.setUserData(query);
		queryTab.setText(wsName);
		
		QueryTextEditor queryTextEditor = new QueryTextEditor();
		queryTextEditor.initCSS(masterUI.getStage().getScene());
		queryTextEditor.insertText(0, query.getQuery());
		
		queryTextEditor.setOnKeyReleased((e) -> {
			if (e.getCode() == KeyCode.S && e.isControlDown()) {
				queryTab.setText(query.getWorksheetName());
				query.setQuery(queryTextEditor.getText());
				try {
					masterUI.getDatabaseManager().updateQuery(query);
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				System.out.println("saved query");
				return;
			}
		});
		queryTextEditor.setOnKeyTyped((e) -> {
			queryTab.setText(query.getWorksheetName() + "*");
		});
		
		queryTab.setContent(queryTextEditor);
		return queryTab;
	}

	
	private boolean isQueryTabOpen() {
		String wsName = query.getWorksheetName();
		for (Tab tab : masterUI.getRightPane().getQueriesTab().getTabs()) {
			if (tab.getText().equalsIgnoreCase(wsName)) {
				return true;
			}
		}
		return false;
	}

}
