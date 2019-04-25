package io.jayms.dbsc.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

/**
 * Helper methods relevant to validation.
 */
public final class Validation {

	/**
	 * Check if a string is really a string populated with characters.
	 * @param s - string to check.
	 * @return - Returns true if string is populated with characters, otherwise false.
	 */
	public static boolean sanityString(String s) {
		return s == null || s.isEmpty();
	}
	
	/**
	 * Pops up an alert box on the screen with the specified message.
	 * @param message - messsage to display in alert box.
	 */
	public static void alert(String message) {
		Alert alert = new Alert(AlertType.ERROR, message, ButtonType.OK);
		alert.showAndWait();
	}
}
