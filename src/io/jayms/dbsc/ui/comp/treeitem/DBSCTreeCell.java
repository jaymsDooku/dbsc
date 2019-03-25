package io.jayms.dbsc.ui.comp.treeitem;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class DBSCTreeCell extends TreeCell<DBSCTreeItem> {
	
	private static final Color SELECTED_TREE_ITEM = Color.rgb(242, 83, 72);
	
	public DBSCTreeCell() {
		selectedProperty().addListener((ov, o, n) -> {
			Background bg = n ? new Background(new BackgroundFill(SELECTED_TREE_ITEM, CornerRadii.EMPTY, Insets.EMPTY)) :
				new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
			Color textColor = n ? Color.WHITE : Color.BLACK;
			
			setBGAndColor(this.getChildren(), bg, textColor);
			setBackground(bg);
		});
	}
	
	private void setBGAndColor(ObservableList<Node> nodes, Background bg, Paint textClr) {
		nodes.stream().forEach(c -> {
			System.out.println("c: " + c);
			if (c instanceof Button) {
				return;
			}
			if (c instanceof StackPane) {
				StackPane stackPane = (StackPane) c;
				if (stackPane.getStyleClass().contains("arrow")) {
					return;
				}
			}
			if (c instanceof Region) {
				Region region = (Region) c;
				region.setBackground(bg);
			}
			if (c instanceof Text) {
				Text text = (Text) c;
				if (!text.fillProperty().isBound()) {
					text.setFill(textClr);
				}
			}
			if (c instanceof Label) {
				Label lbl = (Label) c;
				lbl.setTextFill(textClr);
			}
			if (c instanceof Parent) {
				Parent parent = (Parent) c;
				setBGAndColor(parent.getChildrenUnmodifiable(), bg, textClr);
			}
		});
	}
	
	@Override
	protected void updateItem(DBSCTreeItem item, boolean empty) {
		super.updateItem(item, empty);
		
		setText(null);
		if (empty) {
			setGraphic(null);
		} else {
			setGraphic(getItem());
			
			Node disclosureNode = getDisclosureNode();
			disclosureNode.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
				if (!getItem().isActive()) {
					e.consume();
				}
			});
			
			Node arrow = ((StackPane) disclosureNode).getChildren().stream().filter(c -> c.getStyleClass().contains("arrow")).findFirst().orElse(null);
			arrow.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
				if (!getItem().isActive()) {
					System.out.println("hello");
					arrow.setRotate(0);
					e.consume();
				}
			});
			
			Background bg = getItem().isActive() ? new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)) :
				new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)); 
			setBackground(bg);
		}
	}
	
}
