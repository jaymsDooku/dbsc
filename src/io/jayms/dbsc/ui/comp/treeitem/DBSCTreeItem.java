package io.jayms.dbsc.ui.comp.treeitem;

import java.util.Objects;
import java.util.UUID;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Getter;

public abstract class DBSCTreeItem extends BorderPane {

	protected DBSCGraphicalUserInterface masterUI;
	
	@Getter private final UUID itemID;
	@Getter private HBox leftSide;
	@Getter private Label txt;
	//@Getter private Button rmBtn;
	
	@Getter private Button addBtn;
	
	public DBSCTreeItem(DBSCGraphicalUserInterface masterUI, Label txt, Button addBtn) {
		this.masterUI = masterUI;
		this.itemID = UUID.randomUUID();
		this.txt = txt;
		this.addBtn = addBtn;
		addBtn.setPadding(new Insets(3, 4, 3, 4));
		
		this.setLeft(txt);
		this.setRight(addBtn);
	}
	
	public abstract void click(MouseButton mouseButton);
	
	/*public DBSCTreeItem(DBSCGraphicalUserInterface masterUI, Label txt, Button rmBtn) {
		this.leftSide = new HBox();
		this.txt = txt;
		//this.rmBtn = rmBtn;
		this.leftSide.getChildren().addAll(rmBtn, txt);
		this.leftSide.setAlignment(Pos.CENTER_LEFT);
		
		this.setLeft(leftSide);
	}
	
	public DBSCTreeItem(DBSCGraphicalUserInterface masterUI, Label txt, Button addBtn, Button rmBtn) {
		this.leftSide = new HBox();
		this.txt = txt;
		this.rmBtn = rmBtn;
		rmBtn.setPadding(new Insets(3, 4, 3, 4));
		this.leftSide.getChildren().addAll(rmBtn, txt);
		this.leftSide.setAlignment(Pos.CENTER_LEFT);
		
		this.addBtn = addBtn;
		addBtn.setPadding(new Insets(3, 4, 3, 4));
		
		this.setLeft(leftSide);
		this.setRight(addBtn);
	}*/

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
