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
import javafx.scene.input.MouseEvent;

public class ConnectionTreeItem extends DBSCTreeItem {

	public ConnectionTreeItem(DBSCGraphicalUserInterface masterUI, ConnectionConfig connConfig) {
		super(masterUI, new Label(connConfig.getHost()),
				ComponentFactory.createButton("+", e -> {
					new RegisterDatabaseUI(masterUI, connConfig).show();
				}));
				
				/*ComponentFactory.createButton("x", e -> {
					Object obj = e.getSource();
					
					if (!(obj instanceof Button)) {
						return;
					}
					
					Button xBtn = (Button) obj;
					Node parent = xBtn.getParent().getParent();

					if (!(parent instanceof ConnectionTreeItem)) {
						return;
					}
					
					ConnectionTreeItem connTreeItem = (ConnectionTreeItem) parent;
					TreeCell<ConnectionTreeItem> treeCell = (TreeCell<ConnectionTreeItem>) connTreeItem.getParent();
					TreeItem<ConnectionTreeItem> treeItem = treeCell.getTreeItem();
					treeItem.getParent().getChildren().remove(treeItem);
				}));*/
	}

}
