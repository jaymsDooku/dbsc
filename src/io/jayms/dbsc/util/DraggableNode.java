package io.jayms.dbsc.util;

import java.awt.Rectangle;
import java.util.function.Consumer;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

/**
 * Simple draggable node.
 * 
 * Dragging code based on {@link http://blog.ngopal.com.np/2011/06/09/draggable-node-in-javafx-2-0/}
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>, (modifications by) James Leaver
 */
public class DraggableNode extends Pane {

    // node position
    @Getter private double x = 0;
    @Getter private double y = 0;
    @Getter private double prevX = -1;
    @Getter private double prevY = -1;
    // mouse position
    @Getter private double mousex = 0;
    @Getter private double mousey = 0;
    @Getter @Setter private Node view;
    @Getter @Setter private boolean dragging = false;
    @Getter @Setter private boolean moveToFront = true;
    
    @Getter private Consumer<Node> onDrag;

    public DraggableNode() {
        init();
    }
    
    public DraggableNode(Node view) {
    	this(view, null);
    }

    public DraggableNode(Node view, Consumer<Node> onDrag) {
        this.view = view;
        this.onDrag = onDrag;

        getChildren().add(view);
        init();
    }
    
    protected DraggablePane getDraggablePane() {
    	return (this.getParent() instanceof DraggablePane) ? (DraggablePane) this.getParent() : null;
    }

    private void init() {

        onMousePressedProperty().set((e) -> {
            // record the current mouse X and Y position on Node
            mousex = e.getSceneX();
            mousey = e.getSceneY();

            x = getLayoutX();
            y = getLayoutY();

            if (isMoveToFront()) {
                toFront();
            }
        });

        //Event Listener for MouseDragged
        onMouseDraggedProperty().set((e) -> {
            // Get the exact moved X and Y

            double offsetX = e.getSceneX() - mousex;
            double offsetY = e.getSceneY() - mousey;

            double tempX = x + offsetX;
            double tempY = y + offsetY;

            double scaledX = tempX;
            double scaledY = tempY;
            
            boolean hasCollided = getDraggablePane().hasCollided(this, (int) scaledX, (int) scaledY);
            if (hasCollided) {
            	if (prevX == -1 || prevY == -1) return;
            	
            	scaledX = prevX; 
            	scaledY = prevY;
            }
            
            BoundsResult boundsCheck = inBounds((int) scaledX, (int) scaledY);
            boolean inBounds = boundsCheck.isInBounds();
            if (!inBounds) {
            	scaledX = boundsCheck.getFixedX();
            	scaledY = boundsCheck.getFixedY();
            }
            
            prevX = x;
            prevY = y;
            
            x = scaledX;
            y = scaledY;

            setLayoutX(scaledX);
            setLayoutY(scaledY);

            dragging = true;

            // again set current Mouse x AND y position
            mousex = e.getSceneX();
            mousey = e.getSceneY();
            
            applyOnDrag(this);

            e.consume();
        });

        onMouseClickedProperty().set((e) -> {
        	dragging = false;
        });

        onMouseEnteredProperty().set((e) -> {
        	setCursor(Cursor.CROSSHAIR);
        });
        
        onMouseExitedProperty().set((e) -> {
        	setCursor(Cursor.OPEN_HAND);
        });
    }
    
    private void applyOnDrag(Parent p) {
    	p.getChildrenUnmodifiable().stream().forEach(n -> {
    		if (n instanceof Parent) {
    			applyOnDrag((Parent) n);
    		}
    		onDrag.accept(n);
    	});
    }
    
    private BoundsResult inBounds(int newX, int newY) {
    	DraggablePane parent = this.getDraggablePane();
    	
    	Rectangle thisRect = parent.getRectangle(this, newX, newY);
    	
    	int parentX = (int) parent.getLayoutX();
    	int parentY = (int) parent.getLayoutY();
    	int parentWidth = (int) parent.getWidth();
    	int parentHeight = (int) parent.getHeight();
    	Rectangle parentRect = new Rectangle(parentX, parentY, parentWidth, parentHeight);
    	
    	if (parentRect.contains(thisRect)) {
    		return new BoundsResult(newX, newY, true);
    	}
    	
    	int width = (int) this.getWidth();
    	int height = (int) this.getHeight();
    	int maxNewX = newX + width;
    	int maxNewY = newY + height;
    	
    	int maxParentX = parentX + parentWidth;
    	int maxParentY = parentY + parentHeight;
    	
    	int fixedX = newX;
    	int fixedY = newY;
    	if (newX < parentX) {
    		fixedX = parentX;
    	}
    	if (newY < parentY) {
    		fixedY = parentY;
    	}
    	if (maxNewX > maxParentX) {
    		fixedX = maxParentX - width;
    	}
    	if (maxNewY > maxParentY) {
    		fixedY = maxParentY - height;
    	}
    	return new BoundsResult(fixedX, fixedY, false);
    }
    
    public void removeNode(Node n) {
        getChildren().remove(n);
    }
    
    private static class BoundsResult {
    	
    	@Getter private int fixedX;
    	@Getter private int fixedY;
    	@Getter private boolean inBounds;
    	
    	public BoundsResult(int fixedX, int fixedY, boolean inBounds) {
    		this.fixedX = fixedX;
    		this.fixedY = fixedY;
    		this.inBounds = inBounds;
    	}
    }
}

