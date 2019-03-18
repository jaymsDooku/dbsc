package io.jayms.dbsc.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Validation {

	public static boolean sanityString(String s) {
		return s == null || s.isEmpty();
	}
	
	public static void alert(String message) {
		Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
		alert.showAndWait();
	}
}
