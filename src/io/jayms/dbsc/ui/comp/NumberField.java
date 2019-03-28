package io.jayms.dbsc.ui.comp;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class NumberField extends TextField {

	public NumberField() {
		EventHandler<KeyEvent> portTxtType = (KeyEvent e) -> {
			try {
				Integer.parseInt(e.getCharacter());
			} catch (NumberFormatException ex) {
				e.consume();
			}
		};
		this.addEventHandler(KeyEvent.KEY_TYPED, portTxtType);
	}

	public int getValue() throws NumberFormatException {
		return Integer.parseInt(this.getText());
	}
	
}
