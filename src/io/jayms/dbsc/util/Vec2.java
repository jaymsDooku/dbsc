package io.jayms.dbsc.util;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import lombok.Getter;
import lombok.Setter;

public class Vec2 {

	@Getter @Setter private double x;
	@Getter @Setter private double y;
	
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vec2 getRealPosition(Node node) {
		Bounds bounds = node.localToScene(node.getBoundsInLocal());
		return getRealPosition(bounds);
	}
	
	public static Vec2 getRealPosition(Bounds bounds) {
		double x = bounds.getMinX() + (bounds.getWidth()/2);
		double y = bounds.getMinY() + (bounds.getHeight()/2);
		
		return new Vec2(x, y);
	}
}
