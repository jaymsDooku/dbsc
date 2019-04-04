package io.jayms.dbsc.task;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.collect.Maps;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.model.Query;
import javafx.application.Platform;

public class QueryTaskMaster {

	private int queryTaskId = 1;
	private Map<Integer, QueryTask> queryTasks = Maps.newConcurrentMap();
	private ExecutorService executorService;
	private DBSCGraphicalUserInterface masterUI;
	
	public QueryTaskMaster(DBSCGraphicalUserInterface masterUI) {
		this.masterUI = masterUI;
		this.executorService = Executors.newSingleThreadExecutor();
	}
	
	public void updateTaskStatus() {
		String status = queryTasks.isEmpty() ? "No Tasks Running" : "Running Tasks (" + (queryTasks.size()) + ")";
		masterUI.getRightPane().getActionBar().updateStatus(status);
	}
	
	public int startQuery(Query query, File toSave) {
		int id = queryTaskId;
		QueryTask queryTask = new QueryTask(id, masterUI, query, toSave);
		
		executorService.submit(queryTask);
		
		queryTasks.put(id, queryTask);
		queryTaskId++;
		return id;
	}
	
	public QueryTask getQueryTask(int taskId) {
		return queryTasks.get(taskId);
	}
	
	public void stopQuery(int taskId) {
		if (!queryTasks.containsKey(taskId)) {
			return;
		}
		
		QueryTask queryTask = queryTasks.remove(taskId);
		if (queryTask.cancel()) {
			System.out.println("QueryTask " + queryTask.getTaskId() + " has been cancelled.");
		}
	}
}
