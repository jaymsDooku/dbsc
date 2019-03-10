package io.jayms.dbsc.ui.comp.colorpicker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.sun.javafx.scene.control.skin.ColorPalette;

import io.jayms.dbsc.util.GeneralUtils;
import io.jayms.dbsc.util.ReflectionUtils;
import io.jayms.xlsx.model.Style;
import io.jayms.xlsx.model.StyleTable;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class DBSCColorPalette extends ColorPalette {
	
	private static final int NUM_OF_COLUMNS = 12;
	private final List<Node> squares = FXCollections.observableArrayList();
	
	public DBSCColorPalette(ColorPicker colorPicker) {
		super(colorPicker);
	}
	
	public void init() {
		try {
			GridPane colorGridPane = (GridPane) ReflectionUtils.getField(ColorPalette.class, this, "colorPickerGrid");
			colorGridPane.getChildren().clear();
			
			Class<?> colorSquareClazz = ReflectionUtils.getDeclaredClass(ColorPalette.class, "com.sun.javafx.scene.control.skin.ColorPalette$ColorSquare");
			Constructor<?> colorSquareConstructor = colorSquareClazz.getDeclaredConstructor(ColorPalette.class, Color.class, int.class);
			colorSquareConstructor.setAccessible(true);
			
			Style[] styles = StyleTable.STYLE_TABLE.getStyles();
			for (int i = 0; i < styles.length; i++) {
				Style s = styles[i];
				Node square = (Node) colorSquareConstructor.newInstance(this, GeneralUtils.awtToJavaFXColor(s.getFill().getColor()), i);
				squares.add(square);
			}
			
			int columnIndex = 0, rowIndex = 0;
            for (Node square : squares) {
                colorGridPane.add(square, columnIndex, rowIndex);
                columnIndex++;
                if (columnIndex == NUM_OF_COLUMNS) {
                    columnIndex = 0;
                    rowIndex++;
                }
            }
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

}
