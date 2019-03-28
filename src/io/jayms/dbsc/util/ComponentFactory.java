package io.jayms.dbsc.util;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;

public final class ComponentFactory {

	public static Button createButton(String txt, EventHandler<MouseEvent> e) {
		Button btn = new Button(txt);
		btn.setOnMouseClicked(e);
		return btn;
	}

}
