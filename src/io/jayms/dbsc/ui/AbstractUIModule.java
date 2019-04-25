package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

public abstract class AbstractUIModule implements UIModule {

	/**
	 * Reference to main application.
	 */
	@Getter protected final DBSCGraphicalUserInterface masterUI;
	
	/**
	 * Indicates whether the {@link #init()} method has been called or not.
	 */
	@Getter protected boolean initialized = false;
	
	/**
	 * Protected constructor to ensure only instantiated by children classes.
	 * @param masterUI - reference to main application
	 */
	protected AbstractUIModule(DBSCGraphicalUserInterface masterUI) {
		this.masterUI = masterUI;
	}
	
	/**
	 * Instantiated a new stage - an abstracted version of a window - to be called by children classes (standalone modules).
	 * @param title - Title of stage
	 * @return
	 */
	protected Stage initStage(String title) {
		Stage uiStage = new Stage();
		uiStage.initModality(Modality.WINDOW_MODAL); // Allows user to swap between windows (non-blocking).
		uiStage.initOwner(masterUI.getStage().getScene().getWindow()); // This new stage belongs to the main application
		uiStage.setTitle(title); // Set title of window.
		return uiStage;
	}
	
	/**
	 * Initialize the UI components of the module, to be overriden by children classes.
	 */
	@Override
	public void init() {
		initialized = true;
		System.out.println("Initialized UIModule: " + this.getClass().getName()); // debug
	}
}
