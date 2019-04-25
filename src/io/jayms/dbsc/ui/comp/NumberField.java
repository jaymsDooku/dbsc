package io.jayms.dbsc.ui.comp;

import io.jayms.dbsc.util.Validation;
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
	
	public void setValue(int val) {
		setText(Integer.toString(val));
	}

	public int getValue() throws NumberFormatException {
		String text = this.getText();
		if (Validation.sanityString(text)) {
			return -1;
		}
		return Integer.parseInt(text);
	}
	
}
