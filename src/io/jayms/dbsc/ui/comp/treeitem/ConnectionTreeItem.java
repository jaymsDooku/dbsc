package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.ui.RegisterDatabaseUI;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ConnectionTreeItem extends DBSCTreeItem {

	public ConnectionTreeItem(DBSCGraphicalUserInterface masterUI, ConnectionConfig connConfig) {
		super(masterUI, new Label(connConfig.getHost()),
				ComponentFactory.createButton("+", e -> {
					System.out.println("ConnectionTreeItem connConfig: " + connConfig);
					new RegisterDatabaseUI(masterUI, connConfig).show();
				}));
	}

	@Override
	public void click(MouseButton mouseButton) {
		
	}
}
