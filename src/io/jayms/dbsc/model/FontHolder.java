package io.jayms.dbsc.model;

import java.awt.Color;

import org.json.JSONObject;

import lombok.Getter;

public class FontHolder {

	@Getter private String familyName;
	@Getter private int size;
	@Getter private boolean bold;
	@Getter private Color color;
	
	public FontHolder(String familyName, int size, boolean bold, Color color) {
		this.familyName = familyName;
		this.size = size;
		this.bold = bold;
		this.color = color;
	}

	public static JSONObject toJSON(FontHolder font) {
		JSONObject obj = new JSONObject();
		obj.put("familyName", font.familyName);
		obj.put("size", font.size);
		obj.put("bold", font.bold);
		obj.put("color", font.color);
		return obj;
	}
	
	public static FontHolder fromJSON(JSONObject obj) {
		String familyName = obj.getString("familyName");
		int size = obj.getInt("size");
		boolean bold = obj.getBoolean("bold");
		int colorEncoded = obj.getInt("color");
		Color color = io.jayms.xlsx.model.Style.decodeRGB(colorEncoded);
		return new FontHolder(familyName, size, bold, color);
	}
}
