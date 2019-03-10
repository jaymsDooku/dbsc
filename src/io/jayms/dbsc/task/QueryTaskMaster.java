package io.jayms.dbsc.task;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Maps;

import io.jayms.dbsc.DatabaseManager;
import io.jayms.dbsc.model.Query;

public class QueryTaskMaster {

	private int queryTaskId = 1;
	private Map<Integer, QueryTask> queryTasks = Maps.newConcurrentMap();
	private ExecutorService executorService;
	private DatabaseManager dbManager;
	
	public QueryTaskMaster(DatabaseManager dbManager, int poolSize) {
		this.dbManager = dbManager;
		this.executorService = Executors.newFixedThreadPool(poolSize);
	}
	
	public int startQuery(Query query, File toSave) {
		int id = queryTaskId;
		QueryTask queryTask = new QueryTask(id, dbManager, query, toSave);
		
		executorService.submit(queryTask);
		
		queryTasks.put(id, queryTask);
		queryTaskId++;
		return id;
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
