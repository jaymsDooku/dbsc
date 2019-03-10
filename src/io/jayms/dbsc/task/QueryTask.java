package io.jayms.dbsc.task;

import java.io.File;

import io.jayms.dbsc.DatabaseManager;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.xlsx.db.Database;
import io.jayms.xlsx.db.DatabaseConverter;
import io.jayms.xlsx.model.Workbook;
import io.jayms.xlsx.model.Worksheet;
import javafx.concurrent.Task;
import lombok.Getter;

public class QueryTask extends Task<QueryTaskResult> {

	@Getter private final int taskId;
	@Getter private Query query;
	
	private DatabaseManager dbManager;
	
	public QueryTask(int taskId, DatabaseManager dbManager, Query query, File toSave) {
		this.taskId = taskId;
		this.dbManager = dbManager;
		this.query = query;
	}

	@Override
	protected QueryTaskResult call() throws Exception {
		System.out.println("Running Query Task " + taskId);
		Report report = query.getReport();
		DB db = report.getDb();
		ConnectionConfig cc = db.getConnConfig();
		
		Database connDB = dbManager.getDatabaseConnection(cc, db);
		DatabaseConverter converter = new DatabaseConverter(connDB);
		
		String worksheetName = query.getWorksheetName();
		Workbook wb = new Workbook(report.getWorkbookName());
		Worksheet worksheet;
		if (wb.hasWorksheet(worksheetName)) {
			worksheet = wb.getWorksheet(worksheetName);
		} else {
			worksheet = converter.addQueryToWorksheet(wb, worksheetName, query.getQuery());
		}
		
		QueryTaskResult result = new QueryTaskResult(wb, worksheet);
		return result;
	}
}
