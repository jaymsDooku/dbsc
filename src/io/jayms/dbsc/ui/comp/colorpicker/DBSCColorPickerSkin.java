package io.jayms.dbsc.ui.comp.colorpicker;

import com.sun.javafx.scene.control.skin.ColorPickerSkin;

import io.jayms.dbsc.util.ReflectionUtils;
import javafx.scene.control.ColorPicker;

public class DBSCColorPickerSkin extends ColorPickerSkin {

	public DBSCColorPickerSkin(ColorPicker colorPicker) {
		super(colorPicker);
	}
	
	public void init(ColorPicker colorPicker) {
		try {
			DBSCColorPalette palette = new DBSCColorPalette(colorPicker);
			palette.init();
			ReflectionUtils.setField(ColorPickerSkin.class, this, "popupContent", palette);
			System.out.println("palette: " + palette);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
