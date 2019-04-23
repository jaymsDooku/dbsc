package io.jayms.dbsc.task;

import java.io.File;
import java.util.Map;
import java.util.Set;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.db.DatabaseManager;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.xlsx.db.Database;
import io.jayms.xlsx.db.DatabaseColumn;
import io.jayms.xlsx.db.DatabaseConverter;
import io.jayms.xlsx.model.FieldConfiguration;
import io.jayms.xlsx.model.Workbook;
import io.jayms.xlsx.model.Worksheet;
import io.jayms.xlsx.model.WorksheetDescriptor;
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
		wb.setTitleStyle(report.getTitleStyle().toStyle(wb));
		wb.setSubTotalStyle(report.getTitleStyle().toStyle(wb));
		wb.setColourFormat(report.getDoubleBandFormat().toDoubleBandFormat(wb));
		Worksheet worksheet = converter.addQueryToWorksheet(wb, worksheetName, query.getQuery());
		
		masterUI.getRightPane().getActionBar().updateStatus("Running Query Task " + taskId +  " - Writing to file");
		File file = masterUI.getRightPane().getChosenFile();
		
		DatabaseColumn[] fields = DatabaseManager.getTableFields(masterUI, query);
		Map<String, FieldConfiguration> fieldConfigs = FieldConfiguration.getDefaultFieldConfigs(fields, query.getFieldConfigs());
		query.setFieldConfigs(fieldConfigs);
		
		Set<WorksheetDescriptor> wsDescs = query.getReport().getWorksheetDescriptors();
		wb.save(file, wsDescs);

		QueryTaskResult result = new QueryTaskResult(wb, worksheet);
		masterUI.getQueryTaskMaster().stopQuery(taskId);
		masterUI.getQueryTaskMaster().updateTaskStatus();
		return result;
	}
}
