package io.jayms.dbsc.model;

import java.awt.Color;

import org.json.JSONObject;

import io.jayms.xlsx.model.Font;
import io.jayms.xlsx.model.FontManager;
import io.jayms.xlsx.model.Style;
import io.jayms.xlsx.model.Workbook;
import lombok.Getter;
import lombok.Setter;

public class FontHolder {

	@Getter @Setter private String familyName;
	@Getter @Setter private int size;
	@Getter @Setter private boolean bold;
	@Getter @Setter private Color color;
	
	public FontHolder(String familyName, int size, boolean bold, Color color) {
		this.familyName = familyName;
		this.size = size;
		this.bold = bold;
		this.color = color;
	}
	
	public Font toFont(Workbook wb) {
		FontManager fm = wb.getFontManager();
		return fm.getFont(fm.createFont(familyName, size, bold, color));
	}

	public static JSONObject toJSON(FontHolder font) {
		JSONObject obj = new JSONObject();
		obj.put("familyName", font.familyName);
		obj.put("size", font.size);
		obj.put("bold", font.bold);
		obj.put("color", Style.encodeRGB(font.color));
		return obj;
	}
	
	public static FontHolder fromJSON(JSONObject obj) {
		String familyName = obj.getString("familyName");
		int size = obj.getInt("size");
		boolean bold = obj.getBoolean("bold");
		int colorEncoded = obj.getInt("color");
		Color color = Style.decodeRGB(colorEncoded);
		return new FontHolder(familyName, size, bold, color);
	}
}
