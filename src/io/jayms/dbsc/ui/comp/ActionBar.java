package io.jayms.dbsc.ui.comp;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.ui.AbstractUIModule;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import lombok.Getter;

public class ActionBar extends AbstractUIModule {

	@Getter private HBox actionBar;
	private Button runQueryBtn;
	private Button stopQueryBtn;
	private Button openQueryBuilderBtn;
	
	public ActionBar(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void init() {
		super.init();
		
		actionBar = new HBox();
		
		runQueryBtn = new Button("Run Query");
		runQueryBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			masterUI.getRightPane().runQuery(e);
		});
		
		stopQueryBtn = new Button("Stop Query");
		openQueryBuilderBtn = new Button("Open Query Builder");
		openQueryBuilderBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
			
		});
		actionBar.getChildren().addAll(runQueryBtn, stopQueryBtn, openQueryBuilderBtn);
	}
	
	@Override
	public void show() {
		
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
