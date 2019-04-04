package io.jayms.dbsc.task;

import java.io.File;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.db.DatabaseManager;
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
	
	private DBSCGraphicalUserInterface masterUI;
	
	public QueryTask(int taskId, DBSCGraphicalUserInterface masterUI, Query query, File toSave) {
		this.taskId = taskId;
		this.masterUI = masterUI;
		this.query = query;
	}

	@Override
	protected QueryTaskResult call() throws Exception {
		System.out.println("Running Query Task " + taskId);
		Report report = query.getReport();
		DB db = report.getDb();
		
		DatabaseManager dbManager = masterUI.getDatabaseManager();
		Database connDB = dbManager.getDatabaseConnection(db);
		DatabaseConverter converter = new DatabaseConverter(connDB);
		
		masterUI.getRightPane().getActionBar().updateStatus("Running Query Task " + taskId +  " - Constructing Worksheet from Query");
		String worksheetName = query.getWorksheetName();
		Workbook wb = new Workbook(report.getWorkbookName());
		Worksheet worksheet = converter.addQueryToWorksheet(wb, worksheetName, query.getQuery());
		
		masterUI.getRightPane().getActionBar().updateStatus("Running Query Task " + taskId +  " - Writing to file");
		File file = masterUI.getRightPane().getChosenFile();
		wb.save(file, query.getReport().getWorksheetDescriptors());

		QueryTaskResult result = new QueryTaskResult(wb, worksheet);
		masterUI.getQueryTaskMaster().stopQuery(taskId);
		masterUI.getQueryTaskMaster().updateTaskStatus();
		return result;
	}
}
