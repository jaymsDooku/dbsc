package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

public abstract class AbstractUIModule implements UIModule {

	protected final DBSCGraphicalUserInterface masterUI;
	
	@Getter protected boolean initialized = false;
	
	protected AbstractUIModule(DBSCGraphicalUserInterface masterUI) {
		this.masterUI = masterUI;
	}
	
	protected Stage initStage(String title) {
		Stage uiStage = new Stage();
		uiStage.initModality(Modality.WINDOW_MODAL);
		uiStage.initOwner(masterUI.getStage().getScene().getWindow());
		uiStage.setTitle(title);
		return uiStage;
	}
	
	@Override
	public void init() {
		initialized = true;
		System.out.println("Initialized UIModule: " + this.getClass().getName());
	}
}
