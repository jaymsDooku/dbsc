package io.jayms.dbsc.ui.comp;

import io.jayms.dbsc.util.Validation;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * Textfield but only allows positive numbers to be entered into it. Used for all the numerical fields.
 */
public class NumberField extends TextField {

	public NumberField() {
		//listen for the key event
		EventHandler<KeyEvent> portTxtType = (KeyEvent e) -> {
			if (e.getCharacter().equals(".") && !getText().contains(".")) { // we can allow dots for decimal numbers but only if there isn't already a decimal point 
				return;
			}
			try {
				Integer.parseInt(e.getCharacter()); // try to parse an integer from this character
			} catch (NumberFormatException ex) { // throws exception if isn't an integer
				e.consume(); // consume event (don't allow key to be typed).
			}
		};
		this.addEventHandler(KeyEvent.KEY_TYPED, portTxtType);
	}
	
	public void setValue(double val) {
		setText(Double.toString(val));
	}
	
	public void setIntValue(int val) {
		setText(Integer.toString(val));
	}

	public double getValue() throws NumberFormatException {
		String text = this.getText();
		if (Validation.sanityString(text)) {
			return -1;
		}
		return Double.parseDouble(text);
	}
	
	public int getIntValue() {
		return (int) getValue();
	}
	
}
