package io.jayms.dbsc.qb;

import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.util.Vec2;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;

public class JoinContext {

	@Getter private QueryBuilderUI qbUI;
	@Getter @Setter private Line joinLine;
	@Getter @Setter private JoinCircle joinCircle;
	
	public JoinContext(QueryBuilderUI qbUI, Line joinLine, JoinCircle joinCircle) {
		this.qbUI = qbUI;
		this.joinLine = joinLine;
		this.joinCircle = joinCircle;
		
		ReadOnlyObjectProperty<Bounds> circleBounds = joinCircle.boundsInLocalProperty();
		circleBounds.addListener((ov, o, n) -> {
			setLineStart(joinCircle);
		}); 
	}
	
	public boolean canJoinWith(JoinCircle jc) {
		if (jc.equals(joinCircle)) return false;
		if (jc.getTable().equals(joinCircle.getTable())) return false;
		return true;
	}
	
	public Join joinWith(JoinCircle jc) {
		if (!canJoinWith(jc)) {
			return null;
		}
		return new Join(qbUI, joinCircle, jc);
	}
	
	public void setLineStart(Node node) {
		Vec2 pos = Vec2.getRealPosition(node);
		double startX = pos.getX();
		double startY = pos.getY();
		
		joinLine.setStartX(startX);
		joinLine.setStartY(startY);
	}
	
	public void setLineEnd(Node node) {
		Vec2 pos = Vec2.getRealPosition(node);
		double endX = pos.getX();
		double endY = pos.getY();
		
		joinLine.setStartX(endX);
		joinLine.setStartY(endY);
	}
}
