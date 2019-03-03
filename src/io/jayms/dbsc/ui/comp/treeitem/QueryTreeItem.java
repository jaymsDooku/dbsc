package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class QueryTreeItem extends DBSCTreeItem {

	public QueryTreeItem(DBSCGraphicalUserInterface masterUI, String queryName) {
		super(masterUI, new Label(queryName), new Button("x"));
	}

}
