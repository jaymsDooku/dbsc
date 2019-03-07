package io.jayms.dbsc.ui.comp.colorpicker;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Skin;

public class DBSCColorPicker extends ColorPicker {

	@Override
	protected Skin<?> createDefaultSkin() {
		return new DBSCColorPickerSkin(this);
	}
}
