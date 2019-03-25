package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.ui.RegisterDatabaseUI;
import io.jayms.dbsc.ui.comp.ConnectionTreeView;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class ConnectionTreeItem extends DBSCTreeItem {

	private ConnectionConfig connConfig;
	
	public ConnectionTreeItem(DBSCGraphicalUserInterface masterUI, ConnectionConfig connConfig) {
		super(masterUI, new Label(connConfig.getHost()),
				ComponentFactory.createButton("+", e -> {
					System.out.println("ConnectionTreeItem connConfig: " + connConfig);
					new RegisterDatabaseUI(masterUI, connConfig).show();
				}));
		this.connConfig = connConfig;
	}
	
	@Override
	public boolean hasSubItems() {
		return !connConfig.getDbs().isEmpty();
	}

	@Override
	public void click() {
	}
	
	@Override
	public ContextMenu getContextMenu() {
		return newConnectionCM();
	}
	
	private ContextMenu newConnectionCM() {
		ContextMenu connectionCM = new ContextMenu();
		MenuItem newDB = new MenuItem("New DB");
		newDB.setOnAction(e -> {
			new RegisterDatabaseUI(masterUI, connConfig).show();
		});
		MenuItem deleteConn = new MenuItem("Delete Connection");
		deleteConn.setOnAction(e -> {
			masterUI.getDatabaseManager().deleteConnectionConfig(connConfig);
			
			ConnectionTreeView connView = masterUI.getLeftPane().getConnections();
			connView.removeTreeItem(this);
		});
		connectionCM.getItems().addAll(newDB, deleteConn);
		return connectionCM;
	}
}
