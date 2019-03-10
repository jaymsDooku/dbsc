package io.jayms.dbsc.task;

import io.jayms.xlsx.model.Workbook;
import io.jayms.xlsx.model.Worksheet;
import lombok.Getter;

public class QueryTaskResult {

	@Getter private Workbook workbook;
	@Getter private Worksheet worksheet;
	
	public QueryTaskResult(Workbook workbook, Worksheet worksheet) {
		this.workbook = workbook;
		this.worksheet = worksheet;
	}
}
