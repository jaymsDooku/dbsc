package io.jayms.dbsc.ui.comp.treeitem;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.util.ComponentFactory;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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
	public void click() {
	}
	
	@Override
	public ContextMenu getContextMenu() {
		return null;
	}
}
