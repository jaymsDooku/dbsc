package io.jayms.dbsc.qb;

import io.jayms.dbsc.ui.QueryBuilderUI;
import io.jayms.dbsc.util.Vec2;
import javafx.geometry.Bounds;
import javafx.scene.shape.Line;
import lombok.Getter;

public class Join {

	@Getter private QueryBuilderUI qbUI;
	@Getter private JoinCircle joinCircle1;
	@Getter private JoinCircle joinCircle2;
	@Getter private Line joinLine;
	
	public Join(QueryBuilderUI qbUI, JoinCircle joinCircle1, JoinCircle joinCircle2) {
		this.qbUI = qbUI;
		this.joinCircle1 = joinCircle1;
		this.joinCircle2 = joinCircle2;
		joinCircle1.setJoin(this);
		joinCircle2.setJoin(this);
		
		Vec2 jcPos1 = Vec2.getRealPosition(joinCircle1);
		Vec2 jcPos2 = Vec2.getRealPosition(joinCircle2);
		
		this.joinLine = new Line(jcPos1.getX(), jcPos1.getY(), jcPos2.getX(), jcPos2.getY());
		qbUI.getQueryBuilderPane().getChildren().add(joinLine);
	}
	
	public void swap() {
		JoinCircle temp = joinCircle1;
		joinCircle1 = joinCircle2;
		joinCircle2 = temp;
	}
	
	public void dismantle() {
		qbUI.getQueryBuilderPane().getChildren().remove(joinLine);
	}
	
	public void updateLine() {
		Vec2 jcPos1 = Vec2.getRealPosition(joinCircle1);
		Vec2 jcPos2 = Vec2.getRealPosition(joinCircle2);
		
		joinLine.setStartX(jcPos1.getX());
		joinLine.setStartY(jcPos1.getY());
		
		joinLine.setEndX(jcPos2.getX());
		joinLine.setEndY(jcPos2.getY());
	}
}
