package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.stage.Stage;

/**
 * Subclass of AbstractUIModule, representing a component requiring it's own window.
 */
public abstract class StandaloneUIModule extends AbstractUIModule {

	/**
	 * Represents an abstraction of a window for UI module.
	 */
	protected Stage uiStage; 
	
	/**
	 * Subclass-protected constructor to ensure this class is only instantiated via a subclass.
	 * 
	 * @param masterUI - reference to main application, passed to parent class.
	 */
	protected StandaloneUIModule(DBSCGraphicalUserInterface masterUI) {
		super(masterUI);
	}
	
	/**
	 * Called to show the window of UI module.
	 */
	@Override
	public void show() {
		if(uiStage == null) {
			init();
		}
		masterUI.openUIModule(this);
		uiStage.show();
	}
	
	/**
	 * Called to close down a UI module.
	 */
	@Override
	public void close() {
		if (uiStage == null) return;
		if (!uiStage.isShowing()) {
			return;
		}
		masterUI.closeUIModule(this);
		uiStage.close();
	}
}
