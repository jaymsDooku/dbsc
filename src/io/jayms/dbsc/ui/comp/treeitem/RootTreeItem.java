package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

/**
 * Represents the root tree item of the connection tree view. The first and top tree item.
 * 
 * Used to access the create connection UI to create new connection configs.
 */
public class RootTreeItem extends DBSCTreeItem {
	
	public RootTreeItem(DBSCGraphicalUserInterface masterUI) {
		super(masterUI, new Label("Connection"), ComponentFactory.createButton("+", new EventHandler<MouseEvent>() {
			
			@Override
			public void handle(MouseEvent event) {
				masterUI.getCreateConnectionUI().show();
			}
			
		}));
	}
	
	@Override
	public boolean hasSubItems() {
		return !masterUI.getDatabaseManager().getConnectionConfigs().isEmpty();
	}
	
	@Override
	public void click() {
	}
	
	@Override
	public ContextMenu getContextMenu() {
		ContextMenu rootCM = new ContextMenu();
		MenuItem newConn = new MenuItem("New Connection");
		newConn.setOnAction(e -> {
			masterUI.getCreateConnectionUI().show();
		});
		rootCM.getItems().addAll(newConn);
		return rootCM;
	}
}
