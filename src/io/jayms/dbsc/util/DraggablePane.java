package io.jayms.dbsc.util;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import lombok.Getter;

public class DraggablePane extends Pane {

	public DraggablePane() {
		super();
		getChildren().addListener(new ListChangeListener<Node>() {
			@Override
			public void onChanged(Change<? extends Node> c) {
				if (c.next() && c.wasAdded()) {
					List<? extends Node> added = c.getAddedSubList();
					added.forEach(a -> {
						if (!(a instanceof DraggableNode)) {
							throw new IllegalArgumentException("Tried to add something other than a draggable node to the draggable pane!");
						}
					});
				}
			}
		});
	}
	
	public void addDraggable(DraggableNode dragNode) {
		getChildren().add(dragNode);
	}
	
	public List<DraggableNode> getDraggableNodes() {
		return getChildren().stream().map(n -> (DraggableNode) n).collect(Collectors.toList());
	}
	
	public Rectangle getRectangle(DraggableNode n) {
		return new Rectangle((int) n.getLayoutX(), (int) n.getLayoutY(), (int) n.getWidth(), (int) n.getHeight());
	}
	
	public Rectangle getRectangle(DraggableNode n, int newX, int newY) {
		return new Rectangle(newX, newY, (int) n.getWidth(), (int) n.getHeight());
	}
	
	public boolean hasCollided(DraggableNode dragNode, int newX, int newY) {
		Rectangle r1 = getRectangle(dragNode, newX, newY);
		
		return getDraggableNodes().stream().filter(n -> {
				if (n.equals(dragNode)) return false;
				
				Rectangle r2 = getRectangle(n);
				return r1.intersects(r2);
			}).findFirst().isPresent();
	}
	
	/*public CollisionResult hasCollided(DraggableNode dragNode, int newX, int newY) {
		Rectangle r1 = getRectangle(dragNode, newX, newY);
		
		DraggableNode collidedWith = getDraggableNodes().stream().filter(n -> {
				if (n.equals(dragNode)) return false;
				
				Rectangle r2 = getRectangle(n);
				return r1.intersects(r2);
			}).findFirst().orElse(null);
		
		if (collidedWith == null) return new CollisionResult(newX, newY, false);
		
		Rectangle r2 = getRectangle(collidedWith);
		
		int x = (int) r1.getMinX();
		int y = (int) r1.getMinY();
		int maxX = (int) r1.getMaxX();
		int maxY = (int) r1.getMaxY();
		int width = (int) r1.getWidth();
		int height = (int) r1.getHeight();
		
		int collidedX = (int) r2.getMinX();
		int collidedY = (int) r2.getMinY();
		int collidedMaxX = (int) r2.getMaxX();
		int collidedMaxY = (int) r2.getMaxY();
		
		int fixedX = newX;
		int fixedY = newY;
		if (maxY > collidedY && maxY < collidedMaxY) {
			fixedY = collidedY - height;
			System.out.println("maxY > collidedY");
			//return new CollisionResult(fixedX, fixedY, true);
		}
		if (y > collidedY && y < collidedMaxY) {
			fixedY = collidedMaxY;
			System.out.println("y < collidedMaxY");
		//	return new CollisionResult(fixedX, fixedY, true);
		}
		if (maxX > collidedX && maxX < collidedMaxX) {
			fixedX = collidedX - width;
			System.out.println("maxX > collidedX");
	//		return new CollisionResult(fixedX, fixedY, true);
		}
		if (x < collidedMaxX && x > collidedX) {
			fixedX = collidedMaxX + 1;
			System.out.println("x < collidedMaxX");
//			return new CollisionResult(fixedX, fixedY, true);
		}
		return new CollisionResult(fixedX, fixedY, true);
	}

	public static class CollisionResult {
		
		@Getter private int fixedX;
		@Getter private int fixedY;
		@Getter private boolean collided;
		
		public CollisionResult(int fixedX, int fixedY, boolean collided) {
			this.fixedX = fixedX;
			this.fixedY = fixedY;
			this.collided = collided;
		}
	}*/
}
