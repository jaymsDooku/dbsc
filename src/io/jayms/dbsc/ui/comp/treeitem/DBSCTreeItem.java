package io.jayms.dbsc.ui.comp.treeitem;

import java.util.Objects;
import java.util.UUID;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import lombok.Getter;

/**
 * Base class for all tree items, extends BorderPane so it can align text on left side and a small button on right side.
 */
public abstract class DBSCTreeItem extends BorderPane {

	/**
	 * Reference to main application.
	 */
	protected DBSCGraphicalUserInterface masterUI;
	
	/**
	 * Unique ID of tree item for later testing of equality/similarity between tree items.
	 */
	@Getter private final UUID itemID;
	@Getter private Label txt; // text displayed in tree item
	
	@Getter private Button btn; // button aligned to far right of tree item
	
	public DBSCTreeItem(DBSCGraphicalUserInterface masterUI, Label txt, Button btn) {
		this.masterUI = masterUI;
		this.itemID = UUID.randomUUID();
		this.txt = txt;
		this.btn = btn;
		btn.setPadding(new Insets(3, 4, 3, 4));
		
		this.setLeft(txt);
		this.setRight(btn);
	}
	
	public boolean isActive() {
		return true;
	}
	
	/**
	 * @return Whether the tree item has children / sub items.
	 */
	public abstract boolean hasSubItems();
	
	/**
	 * Called upon clicking this tree item.
	 */
	public abstract void click();
	
	/**
	 * @return Context menu of the tree item.
	 */
	public abstract ContextMenu getContextMenu();

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DBSCTreeItem)) return false;
		
		DBSCTreeItem dbscTI = (DBSCTreeItem) obj;
		return dbscTI.itemID.equals(itemID) && dbscTI.txt.getText().equalsIgnoreCase(txt.getText());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(itemID, txt.getText());
	}
}
