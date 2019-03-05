package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Query;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;
import lombok.Getter;

public class QueryTreeItem extends DBSCTreeItem {

	private static final long DOUBLE_CLICK_INTERVAL = 500;
	
	@Getter private final Query query;
	@Getter private long lastClicked = Long.MAX_VALUE;
	
	public QueryTreeItem(DBSCGraphicalUserInterface masterUI, Query query) {
		super(masterUI, new Label(query.getWorksheetName()), new Button("x"));
		this.query = query;
	}
	
	@Override
	public void click(MouseButton mouseButton) {
		long now = System.currentTimeMillis();
		long timePassed = now - lastClicked;
		System.out.println("Time passed: " + timePassed);
		lastClicked = now;
		
		if (timePassed < DOUBLE_CLICK_INTERVAL) {
	        if (query != null) {
	        	if (!isQueryTabOpen(query)) {
	        		Tab tab = queryTab(query);
	        		masterUI.getRightPane().getQueriesTab().getTabs().add(tab);
	        	}
	        }
		}
	}
	
	private Tab queryTab(Query query) {
		String wsName = query.getWorksheetName();
		
		Tab queryTab = new Tab();
		queryTab.setUserData(query);
		queryTab.setText(wsName);
		
		TextField queryTextBox = new TextField();
		queryTextBox.setMaxWidth(Double.MAX_VALUE);
		queryTextBox.setMaxHeight(Double.MAX_VALUE);
		queryTextBox.setAlignment(Pos.TOP_LEFT);
		queryTextBox.setText(query.getQuery());
		queryTextBox.setFont(Font.loadFont(DBSCGraphicalUserInterface.EDITOR_FONT, 14));
		queryTextBox.selectPositionCaret(0);
		queryTab.setContent(queryTextBox);
		return queryTab;
	}

	
	private boolean isQueryTabOpen(Query query) {
		String wsName = query.getWorksheetName();
		for (Tab tab : masterUI.getRightPane().getQueriesTab().getTabs()) {
			if (tab.getText().equalsIgnoreCase(wsName)) {
				return true;
			}
		}
		return false;
	}

}
