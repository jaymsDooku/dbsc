package io.jayms.dbsc.ui;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import lombok.Getter;

public abstract class AbstractUIModule implements UIModule {

	protected final DBSCGraphicalUserInterface masterUI;
	
	@Getter protected boolean initialized = false;
	
	protected AbstractUIModule(DBSCGraphicalUserInterface masterUI) {
		this.masterUI = masterUI;
	}
	
	@Override
	public void init() {
		initialized = true;
		System.out.println("Initialized UIModule: " + this.getClass().getName());
	}
}
