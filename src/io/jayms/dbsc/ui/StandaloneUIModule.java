package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.stage.Stage;

public abstract class StandaloneUIModule extends AbstractUIModule {

	protected Stage uiStage; 
	
	protected StandaloneUIModule(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	@Override
	public void show() {
		if(uiStage == null) {
			System.out.println("Tried to show QueryBuilderUI without initialization.");
			return;
		}
		uiStage.show();
	}
	
	@Override
	public void close() {
		if (!uiStage.isShowing()) {
			return;
		}
		uiStage.close();
	}
}
