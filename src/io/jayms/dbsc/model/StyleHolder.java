package io.jayms.dbsc.model;

import java.awt.Color;

import org.json.JSONObject;

import io.jayms.xlsx.model.Fill;
import io.jayms.xlsx.model.Style;
import io.jayms.xlsx.model.Workbook;
import lombok.Getter;
import lombok.Setter;

public class StyleHolder {

	@Getter @Setter private FontHolder font;
	@Getter @Setter private Color fillColor;
	
	public StyleHolder(FontHolder font, Color fillColor) {
		this.font = font;
		this.fillColor = fillColor;
	}
	
	public Style toStyle(Workbook wb) {
		return new Style(font.toFont(wb), new Fill(fillColor));
	}
	
	public static JSONObject toJSON(StyleHolder style) {
		JSONObject obj = new JSONObject();
		obj.put("font", FontHolder.toJSON(style.font));
		obj.put("fillColor", Style.encodeRGB(style.fillColor));
		return obj;
	}
	
	public static StyleHolder fromJSON(JSONObject obj) {
		JSONObject fontJson = obj.getJSONObject("font");
		FontHolder font = FontHolder.fromJSON(fontJson);
		int encodedFill = obj.getInt("fillColor");
		Color fillColor = Style.decodeRGB(encodedFill);
		return new StyleHolder(font, fillColor);
	}
}
