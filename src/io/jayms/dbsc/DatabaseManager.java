package io.jayms.dbsc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import lombok.Getter;

public class DatabaseManager {

	/*
	 * VARCHAR = TEXT
	 * 
	 */
	
	private static final String CONNECTION_TBL = "CONNECTION";
	private static final String CREATE_CONNECTION_TBL = "CREATE TABLE CONNECTION ("
			 + "ConnectionID INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + "Hostname TEXT NOT NULL, "
			 + "Port INTEGER NOT NULL, "
			 + "Username TEXT NOT NULL, "
			 + "Password TEXT NOT NULL, "
			 + "UNIQUE(Hostname, Port) "
			 + "ON CONFLICT IGNORE"
			 + ")";
	private static final String INSERT_CONNECTION = "INSERT INTO CONNECTION(Hostname, Port, Username, Password) VALUES (?, ?, ?, ?)";
	private static final String SELECT_CONNECTIONS = "SELECT * FROM CONNECTION INNER JOIN DBS USING(ConnectionID) LEFT JOIN SSREPORT USING (DBID) LEFT JOIN SSREPORTQUERIES USING(ReportID)LEFT JOIN QUERIES USING (QueryID)";
	
	private static final String DB_TBL = "DBS";
	private static final String CREATE_DB_TBL = "CREATE TABLE DBS ("
			+ "DBID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "ConnectionID INTEGER NOT NULL, "
			+ "DatabaseName TEXT NOT NULL, "
			+ "DatabaseType TEXT NOT NULL, "
			+ "DBFilePath TEXT DEFAULT NULL"
			+ ")";
	private static final String INSERT_DB = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType) VALUES (?, ?, ?)";
	private static final String INSERT_DB_WITH_FILE = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, DBFilePath) VALUES (?, ?, ?, ?)";
	private static final String SELECT_DB = "SELECT DBID, DatabaseName FROM DBS WHERE ConnectionID = ?";
	
	private static final String SSREPORT_TBL = "SSREPORT";
	private static final String CREATE_SSREPORT_TBL = "CREATE TABLE SSREPORT ("
			+ "ReportID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "DBID INTEGER NOT NULL, "
			+ "WorkbookName TEXT NOT NULL"
			+ ")";
	private static final String INSERT_SSREPORT = "INSERT INTO SSREPORT(DBID, WorkbookName) VALUES (?, ?)";
	private static final String SELECT_SSREPORT = "SELECT ReportID, WorkbookName FROM SSREPORT WHERE DBID = ?";
	
	private static final String SSREPORTQUERIES_TBL = "SSREPORTQUERIES";
	private static final String CREATE_SSREPORTQUERIES_TBL = "CREATE TABLE SSREPORTQUERIES ("
			+ "ReportQueryID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "ReportID INTEGER NOT NULL, "
			+ "QueryID INTEGER NOT NULL"
			+ ")";
	private static final String INSERT_SSREPORTQUERY = "INSERT INTO SSREPORTQUERIES(ReportID, QueryID) VALUES (?, ?)";
	
	private static final String QUERIES_TBL = "QUERIES";
	private static final String CREATE_QUERIES_TBL = "CREATE TABLE QUERIES ("
			+ "QueryID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "WorksheetName TEXT NOT NULL, "
			+ "QueryString TEXT NOT NULL, "
			+ "UNIQUE (WorksheetName, QueryString) "
			+ "ON CONFLICT IGNORE"
			+ ")";
	private static final String INSERT_QUERY = "INSERT INTO QUERIES(WorksheetName, QueryString) VALUES (?, ?)";
	private static final String UPDATE_QUERY = "UPDATE QUERIES SET WorksheetName = ?, QueryString = ? WHERE QueryID = ?";
	
	private SQLiteDatabase db;
	
	private Map<String, ConnectionConfig> connCache = new HashMap<>();
	
	public DatabaseManager(SQLiteDatabase db) {
		this.db = db;
		load();
	}
	
	private void ensureTable(String tblName, String createTable) {
		if (!db.tableExists(tblName)) {
			Connection conn = db.connection();
			try {
				PreparedStatement ps = conn.prepareStatement(createTable);
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void dropTable(String tbl) {
		Connection conn = db.connection();
		try {
			PreparedStatement ps = conn.prepareStatement("DROP TABLE IF EXISTS " + tbl);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void ensureTables() {
		if (DBSCGraphicalUserInterface.DEBUG) {
			dropTable(CONNECTION_TBL);
			dropTable(DB_TBL);
			dropTable(SSREPORT_TBL);
			dropTable(SSREPORTQUERIES_TBL);
			dropTable(QUERIES_TBL);
		}
		
		ensureTable(CONNECTION_TBL, CREATE_CONNECTION_TBL);
		ensureTable(DB_TBL, CREATE_DB_TBL);
		ensureTable(SSREPORT_TBL, CREATE_SSREPORT_TBL);
		ensureTable(SSREPORTQUERIES_TBL, CREATE_SSREPORTQUERIES_TBL);
		ensureTable(QUERIES_TBL, CREATE_QUERIES_TBL);
	}
	
	public void load() {
		ensureTables();
	}
	
	private int insertDB(int connId, DB dbItem) throws SQLException {
		Connection conn = db.connection();
		PreparedStatement ps = conn.prepareStatement(INSERT_DB, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, connId);
		ps.setString(2, dbItem.getDatabaseName());
		ps.setString(3, dbItem.getType().toString());
		ps.setString(4, dbItem.getSqliteDBFile().getAbsolutePath());
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1);
		ps.close();
		return id;
	}
	
	private int insertReport(int dbId, Report report) throws SQLException {
		Connection conn = db.connection();
		PreparedStatement ps = conn.prepareStatement(INSERT_SSREPORT, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, dbId);
		ps.setString(2, report.getWorkbookName());
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1);
		ps.close();
		return id;
	}
	
	private int insertQuery(int reportId, Query query) throws SQLException {
		Connection conn = db.connection();
		PreparedStatement ps = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, query.getWorksheetName());
		ps.setString(2, query.getQuery());
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1);
		ps.close();
		insertReportQuery(reportId, id);
		return id;
	}
	
	private int insertReportQuery(int reportId, int queryId) throws SQLException {
		Connection conn = db.connection();
		PreparedStatement ps = conn.prepareStatement(INSERT_SSREPORTQUERY, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, reportId);
		ps.setInt(2, queryId);
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1);
		ps.close();
		return id;
	}
	
	private Map<String, Integer> fetchDBs(int connectionId) throws SQLException {
		Map<String, Integer> result = new HashMap<>();
		Connection conn = db.connection();
		PreparedStatement ps = conn.prepareStatement(SELECT_DB);
		ps.setInt(1, connectionId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			result.put(rs.getString("DatabaseName"), rs.getInt("DBID"));
		}
		rs.close();
		ps.close();
		return result;
	}
	
	private Map<String, Integer> fetchSSReports(int dbId) throws SQLException {
		Map<String, Integer> result = new HashMap<>();
		Connection conn = db.connection();
		PreparedStatement ps = conn.prepareStatement(SELECT_SSREPORT);
		ps.setInt(1, dbId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			result.put(rs.getString("WorkbookName"), rs.getInt("ReportID"));
		}
		rs.close();
		ps.close();
		return result;
	}
	
	private void updateQuery(int reportId, Query query) throws SQLException {
		Connection conn = db.connection();
		PreparedStatement ps = conn.prepareStatement(UPDATE_QUERY);
		ps.setString(1, query.getWorksheetName());
		ps.setString(2, query.getQuery());
		ps.setInt(3, query.getId());
		ps.executeUpdate();
		ps.close();
	}
	
	public void store(ConnectionConfig cc) {
		Connection conn = db.connection();
		try {
			PreparedStatement ps = conn.prepareStatement(INSERT_CONNECTION, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, cc.getHost());
			ps.setInt(2, cc.getPort());
			ps.setString(3, cc.getUser());
			ps.setString(4, cc.getPass()); // TODO: Encryption
			int connId = ps.executeUpdate();
			ps.close();
			
			if (connId > 0) {
				List<DB> dbs = cc.getDbs();
				if (!dbs.isEmpty()) {
					Map<String, Integer> existingDBs = fetchDBs(connId);
					for (DB dbItem : dbs) {
						String dbName = dbItem.getDatabaseName();
						int dbId = existingDBs.containsKey(dbName) ? 
								insertDB(connId, dbItem) : existingDBs.get(dbName);
								
						Map<String, Integer> existingReports = fetchSSReports(dbId);
						for (Report report : dbItem.getReports()) {
							String wbName = report.getWorkbookName();
							int reportId = existingReports.containsKey(wbName) ?
									insertReport(dbId, report) : existingReports.get(wbName);
									
							for (Query query : report.getQueries()) {
								if (query.getId() == -1) {
									insertQuery(reportId, query);
								} else {
									updateQuery(reportId, query);
								}
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean loadConnectionConfigs() {
		Connection conn = db.connection();
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_CONNECTIONS);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) return false;
			
			int id = rs.getInt("ConnectionID");
			String host = rs.getString("Hostname");
			int port = rs.getInt("Port");
			String user = rs.getString("Username");
			String pass = rs.getString("Password");
			Multimap<String, DBValue> dbMap = HashMultimap.create();
			while (rs.next()) {
				int nextId = rs.getInt("ConnectionID");
				if (id != nextId) {
					List<DB> dbs = new ArrayList<>();
					for (String dbName : dbMap.keySet()) {
						Collection<DBValue> dbValues = dbMap.get(dbName);
						List<Report> reports = new ArrayList<>();
						for (DBValue dbVal : dbValues) {
							String dbType = dbVal.getDbType();
							Multimap<String, Query> queryMap = dbVal.getQueries();
							for (String wsName : queryMap.keySet()) {
								Collection<Query> queries = queryMap.get(wsName);
								reports.add(new Report(wsName, queries.toArray(new Query[0])));
							}
						}
					}
					ConnectionConfig cc = new ConnectionConfig(host, port, user, pass, dbs);
					connCache.put(host, cc);
					id = nextId;
				}
			
				host = rs.getString("Hostname");
				port = rs.getInt("Port");
				user = rs.getString("Username");
				pass = rs.getString("Password");
				
				String dbName = rs.getString("DatabaseName");
				String wbName = rs.getString("WorkbookName");
				String wsName = rs.getString("WorksheetName");
				String query = rs.getString("QueryString");
				int queryId = rs.getInt("QueryID");
				
				String dbType = rs.getString("DatabaseType"); 
				dbMap.put(dbName, new DBValue(dbType, ImmutableMultimap.<String, Query>builder()
						.put(wbName, new Query(queryId, wsName, query)).build()));
			}
			
			List<DB> dbs = new ArrayList<>();
			for (String dbName : dbMap.keySet()) {
				Collection<DBValue> reports = dbMap.get(dbName);
				for (DBValue report : reports) {
					List<Report> reportList = new ArrayList<>();
					Multimap<String, Query> reportMap = report.getQueries();
					for (String wsName : reportMap.keySet()) {
						Collection<Query> reportQueries = reportMap.get(wsName);
						reportList.add(new Report(wsName, reportQueries.toArray(new Query[0])));
					}
					dbs.add(new DB(dbName, DBType.valueOf(report.getDbType()), reportList));
				}
			}
			
			ConnectionConfig cc = new ConnectionConfig(host, port, user, pass, dbs);
			connCache.put(host, cc);
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public ConnectionConfig getConnectionConfig(String host) {
		return connCache.get(host);
	}
	
	public Collection<ConnectionConfig> getConnectionConfigs() {
		return Collections.unmodifiableCollection(connCache.values());
	}
	
	public void close() {
		try {
			db.connection().close();
			System.out.println("database closed");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static class DBValue {
		
		@Getter private String dbType;
		@Getter private Multimap<String, Query> queries;
		
		public DBValue(String dbType, Multimap<String, Query> queries) {
			this.dbType = dbType;
			this.queries = queries;
		}
	}
}
