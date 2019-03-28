package io.jayms.dbsc.model;

import java.awt.Color;

import org.json.JSONObject;

import io.jayms.xlsx.model.Style;
import lombok.Getter;

public class DoubleBandFormatHolder {

	@Getter private Color color1;
	@Getter private Color color2;
	
	public DoubleBandFormatHolder(Color color1, Color color2) {
		this.color1 = color1;
		this.color2 = color2;
	}
	
	public static JSONObject toJSON(DoubleBandFormatHolder dbFormat) {
		JSONObject result = new JSONObject();
		result.put("color1", Style.encodeRGB(dbFormat.color1));
		result.put("color2", Style.encodeRGB(dbFormat.color2));
		return result;
	}
	
	public static DoubleBandFormatHolder fromJSON(JSONObject obj) {
		int colorEncoded1 = obj.getInt("color1");
		int colorEncoded2 = obj.getInt("color2");
		Color color1 = Style.decodeRGB(colorEncoded1);
		Color color2 = Style.decodeRGB(colorEncoded2);
		return new DoubleBandFormatHolder(color1, color2);
	}
	
}
