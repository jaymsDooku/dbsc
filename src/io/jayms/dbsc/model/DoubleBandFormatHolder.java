package io.jayms.dbsc.model;

import org.json.JSONObject;

import io.jayms.xlsx.model.DoubleBandFormat;
import io.jayms.xlsx.model.Workbook;
import lombok.Getter;
import lombok.Setter;

public class DoubleBandFormatHolder {

	@Getter @Setter private StyleHolder style1;
	@Getter @Setter private StyleHolder style2;
	
	public DoubleBandFormatHolder(StyleHolder style1, StyleHolder style2) {
		this.style1 = style1;
		this.style2 = style2;
	}
	
	public DoubleBandFormat toDoubleBandFormat(Workbook wb) {
		return new DoubleBandFormat(style1.toStyle(wb), style2.toStyle(wb));
	}
	
	public static JSONObject toJSON(DoubleBandFormatHolder dbFormat) {
		JSONObject result = new JSONObject();
		result.put("style1", StyleHolder.toJSON(dbFormat.getStyle1()));
		result.put("style2", StyleHolder.toJSON(dbFormat.getStyle2()));
		return result;
	}
	
	public static DoubleBandFormatHolder fromJSON(JSONObject obj) {
		StyleHolder style1 = StyleHolder.fromJSON(obj.getJSONObject("style1"));
		StyleHolder style2 = StyleHolder.fromJSON(obj.getJSONObject("style2"));
		return new DoubleBandFormatHolder(style1, style2);
	}
	
}
