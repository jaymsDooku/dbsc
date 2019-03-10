package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.ui.NewReportUI;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;

public class DBTreeItem extends DBSCTreeItem {

	private DB db;
	
	public DBTreeItem(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI, new Label(db.getDatabaseName()), ComponentFactory.createButton("+", e -> {
					new NewReportUI(masterUI, db).show();
				}));
		this.db = db;
	}

	@Override
	public void click() {
	}
	
	@Override
	public ContextMenu getContextMenu() {
		return newDatabaseCM();
	}
	
	private ContextMenu newDatabaseCM() {
		ContextMenu databaseCM = new ContextMenu();
		MenuItem newDB = new MenuItem("New Report");
		newDB.setOnAction(e -> {
			new NewReportUI(masterUI, db).show();
		});
		MenuItem deleteConn = new MenuItem("Delete Database");
		deleteConn.setOnAction(e -> {
			masterUI.getDatabaseManager().deleteDB(db);
		});
		databaseCM.getItems().addAll(newDB, deleteConn);
		return databaseCM;
	}
}
