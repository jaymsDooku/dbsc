package io.jayms.dbsc.util;

import javafx.scene.paint.Color;

public class GeneralUtils {

	public static Color awtToJavaFXColor(java.awt.Color awtColor) {
		int r = awtColor.getRed();
		int g = awtColor.getGreen();
		int b = awtColor.getBlue();
		int a = awtColor.getAlpha();
		double opacity = a / 255.0 ;
		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
		return fxColor;
	}
	
	public static java.awt.Color javafxToAwtColor(Color fx) {
		java.awt.Color awtColor = new java.awt.Color((float) fx.getRed(),
                (float) fx.getGreen(),
                (float) fx.getBlue(),
                (float) fx.getOpacity());
		return awtColor;
	}
}
