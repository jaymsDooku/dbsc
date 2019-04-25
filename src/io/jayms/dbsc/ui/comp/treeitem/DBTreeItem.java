package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.ui.DatabaseUI;
import io.jayms.dbsc.ui.ReportUI;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class DBTreeItem extends DBSCTreeItem {

	private DB db;
	
	public DBTreeItem(DBSCGraphicalUserInterface masterUI, DB db) {
		super(masterUI, new Label(db.getDatabaseName()), ComponentFactory.createButton("+", e -> {
					new ReportUI(masterUI, db, null).show();
				}));
		this.db = db;
	}
	
	@Override
	public boolean hasSubItems() {
		return !db.getReports().isEmpty();
	}
	
	@Override
	public boolean isActive() {
		boolean isActive = true;
		if (db.getType() == DBType.SQLITE) {
			isActive = db.getSqliteDBFile().exists();
		}
		return isActive;
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
			new ReportUI(masterUI, db, null).show();
		});
		MenuItem editDB = new MenuItem("Edit DB");
		editDB.setOnAction(e -> {
			new DatabaseUI(masterUI, null, db).show();
		});
		MenuItem deleteConn = new MenuItem("Delete Database");
		deleteConn.setOnAction(e -> {
			masterUI.getLeftPane().getConnections().removeTreeItem(this);
			masterUI.getDatabaseManager().deleteDB(db);
		});
		databaseCM.getItems().addAll(newDB, editDB, deleteConn);
		return databaseCM;
	}
}
