package io.jayms.dbsc.util;

import java.awt.Rectangle;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
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
						if (!(a instanceof DraggableNode) && !(a instanceof Line)) {
							throw new IllegalArgumentException("Tried to add something other than a draggable node to the draggable pane!");
						}
					});
				}
			}
		});
	}
	
	private Random random = new Random();
	
	private Coords getFreeCoords(DraggableNode dragNode) {
		int x = random.nextInt((int) (this.getWidth() - dragNode.getBoundsInParent().getWidth()));
		int y = random.nextInt((int) (this.getHeight() - dragNode.getBoundsInParent().getHeight()));
		
		while (hasCollided(dragNode, x, y)) {
			x = random.nextInt((int) (this.getWidth() - dragNode.getBoundsInParent().getWidth()));
			y = random.nextInt((int) (this.getHeight() - dragNode.getBoundsInParent().getHeight()));
		}
		
		return new Coords(x, y);
	}
	
	public void addDraggable(DraggableNode dragNode) {
		getChildren().add(dragNode);
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				int x = (int) dragNode.getLayoutX();
				int y = (int) dragNode.getLayoutY();
				if (getChildren().size() > 1) {
					x--;
					y--;
				}
				if (hasCollided(dragNode, x, y)) {
					Coords freeCoords = getFreeCoords(dragNode);
					dragNode.setLayoutX(freeCoords.getX());
					dragNode.setLayoutY(freeCoords.getY());
				}
			}
			
		}, 25L);
	}
	
	public List<DraggableNode> getDraggableNodes() {
		return getChildren().stream()
				.filter(n -> n instanceof DraggableNode)
				.map(n -> (DraggableNode) n).collect(Collectors.toList());
	}
	
	public Rectangle getRectangle(DraggableNode n) {
		return new Rectangle((int) n.getLayoutX(), (int) n.getLayoutY(), (int) n.getBoundsInParent().getWidth(), (int) n.getBoundsInParent().getHeight());
	}
	
	public Rectangle getRectangle(DraggableNode n, int newX, int newY) {
		return new Rectangle(newX, newY, (int) n.getBoundsInParent().getWidth(), (int) n.getBoundsInParent().getHeight());
	}
	
	public boolean hasCollided(DraggableNode dragNode, int newX, int newY) {
		Rectangle r1 = getRectangle(dragNode, newX, newY);

		return getDraggableNodes().stream().filter(n -> {
				if (n.equals(dragNode)) return false;
				
				Rectangle r2 = getRectangle(n);
				return r1.intersects(r2);
			}).findFirst().isPresent();
	}
	
	private static class Coords {
		
		@Getter private int x;
		@Getter private int y;
		
		public Coords(int x, int y) {
			this.x = x;
			this.y = y;
		}
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
