package io.jayms.dbsc.db;

import java.io.File;
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

import org.json.JSONObject;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.SQLiteDatabase;
import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.ConnectionConfig.CreationResult.Result;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.model.DoubleBandFormatHolder;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
import io.jayms.dbsc.model.StyleHolder;
import io.jayms.xlsx.db.Database;
import io.jayms.xlsx.db.OracleDatabase;
import io.jayms.xlsx.db.SQLServerDatabase;
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
			 + "UNIQUE(Hostname) "
			 + "ON CONFLICT IGNORE"
			 + ")";
	private static final String INSERT_CONNECTION = "INSERT INTO CONNECTION(Hostname) VALUES (?)";
	private static final String SELECT_CONNECTIONS = "SELECT * FROM CONNECTION LEFT JOIN DBS USING(ConnectionID) LEFT JOIN SSREPORT USING (DBID) LEFT JOIN SSREPORTQUERIES USING(ReportID)LEFT JOIN QUERIES USING (QueryID)";
	private static final String DELETE_CONNECTION = "DELETE FROM CONNECTION WHERE ConnectionID = ?";
	private static final String SELECT_CONNECTION = "SELECT * FROM CONNECTION WHERE ConnectionID = ?";
	private static final String UPDATE_CONNECTION = "UPDATE CONNECTION SET Hostname = ? WHERE ConnectionID = ?";
	
	private static final String DB_TBL = "DBS";
	private static final String CREATE_DB_TBL = "CREATE TABLE DBS ("
			+ "DBID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "ConnectionID INTEGER NOT NULL, "
			+ "DatabaseName TEXT NOT NULL, "
			+ "DatabaseType TEXT NOT NULL, "
			+ "Port INTEGER NOT NULL DEFAULT -1, "
			+ "Username TEXT DEFAULT NULL, "
			+ "Password TEXT DEFAULT NULL, "
			+ "DBFilePath TEXT DEFAULT NULL, "
			+ "ServerName TEXT DEFAULT NULL"
			+ ")";
	private static final String INSERT_DB_SQLITE = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, DBFilePath) VALUES (?, ?, ?, ?)";
	private static final String INSERT_DB_ORACLE = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, Port, Username, Password) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String INSERT_DB_SQLSERVER = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, Port, Username, Password, ServerName) VALUES (?, ?, ?, ?, ?, ?, ?)";
	private static final String SELECT_DB = "SELECT DBID, DatabaseName FROM DBS WHERE ConnectionID = ?";
	private static final String DELETE_DB = "DELETE FROM DBS WHERE DBID = ?";
	
	private static final String SSREPORT_TBL = "SSREPORT";
	private static final String CREATE_SSREPORT_TBL = "CREATE TABLE SSREPORT ("
			+ "ReportID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "DBID INTEGER NOT NULL, "
			+ "WorkbookName TEXT NOT NULL, "
			+ "DoubleBandFormat TEXT NOT NULL,"
			+ "TitleStyle TEXT NOT NULL"
			+ ")";
	private static final String INSERT_SSREPORT = "INSERT INTO SSREPORT(DBID, WorkbookName, DoubleBandFormat, TitleStyle) VALUES (?, ?, ?, ?)";
	private static final String SELECT_SSREPORT = "SELECT ReportID, WorkbookName FROM SSREPORT WHERE DBID = ?";
	private static final String DELETE_SSREPORT = "DELETE FROM SSREPORT WHERE ReportID = ?";
	
	private static final String SSREPORTQUERIES_TBL = "SSREPORTQUERIES";
	private static final String CREATE_SSREPORTQUERIES_TBL = "CREATE TABLE SSREPORTQUERIES ("
			+ "ReportQueryID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "ReportID INTEGER NOT NULL, "
			+ "QueryID INTEGER NOT NULL"
			+ ")";
	private static final String INSERT_SSREPORTQUERY = "INSERT INTO SSREPORTQUERIES(ReportID, QueryID) VALUES (?, ?)";
	private static final String SELECT_SSREPORTQUERY = "SELECT * FROM SSREPORTQUERIES WHERE ReportID = ?";
	private static final String DELETE_SSREPORTQUERY = "DELETE FROM SSREPORTQUERIES WHERE ReportID = ?";
	
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
	private static final String DELETE_QUERY = "DELETE FROM QUERIES WHERE QueryID = ?";
	
	@Getter private final DBSCGraphicalUserInterface masterUI;
	
	private SQLiteDatabase db;
	
	private Map<String, ConnectionConfig> connConfigCache = new HashMap<>();
	private Table<String, String, Database> connDatabaseCache = HashBasedTable.create();
	
	public DatabaseManager(DBSCGraphicalUserInterface masterUI, SQLiteDatabase db) {
		this.masterUI = masterUI;
		this.db = db;
		load();
	}
	
	private void ensureTable(String tblName, String createTable) {
		if (!db.tableExists(tblName)) {
			Connection conn = db.getConnection();
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
		Connection conn = db.getConnection();
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
		Connection conn = db.getConnection();
		PreparedStatement ps = null;
		if (dbItem.getType() == DBType.SQLITE) {
			ps = conn.prepareStatement(INSERT_DB_SQLITE, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, connId);
			ps.setString(2, dbItem.getDatabaseName());
			ps.setString(3, dbItem.getType().toString());
			ps.setString(4, dbItem.getSqliteDBFile().getAbsolutePath());
		} else  {
			ps = conn.prepareStatement((dbItem.getType() == DBType.SQL_SERVER) ? INSERT_DB_SQLSERVER :
				INSERT_DB_ORACLE, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, connId);
			ps.setString(2, dbItem.getDatabaseName());
			ps.setString(3, dbItem.getType().toString());
			ps.setInt(4, dbItem.getPort());
			ps.setString(5, dbItem.getUser());
			ps.setString(6, dbItem.getPass());
			ps.setString(7, dbItem.getType().toString());
			if (dbItem.getType() == DBType.SQL_SERVER) {
				ps.setString(8, dbItem.getServerName());
			}
		}
		
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1);
		ps.close();
		return id;
	}
	
	private int insertReport(int dbId, Report report) throws SQLException {
		JSONObject dbFormatJson = DoubleBandFormatHolder.toJSON(report.getDoubleBandFormat());
		JSONObject titleJson = StyleHolder.toJSON(report.getTitleStyle());
		
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(INSERT_SSREPORT, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, dbId);
		ps.setString(2, report.getWorkbookName());
		ps.setString(3, dbFormatJson.toString());
		ps.setString(4, titleJson.toString());
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1);
		ps.close();
		return id;
	}
	
	private int insertQuery(int reportId, Query query) throws SQLException {
		Connection conn = db.getConnection();
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
		Connection conn = db.getConnection();
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
		Connection conn = db.getConnection();
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
		Connection conn = db.getConnection();
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
	
	public void updateQuery(Query query) throws SQLException {
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(UPDATE_QUERY);
		ps.setString(1, query.getWorksheetName());
		ps.setString(2, query.getQuery());
		ps.setInt(3, query.getId());
		ps.executeUpdate();
		ps.close();
	}
	
	public ConnectionConfig.CreationResult createConnectionConfig(String host) {
		if (connConfigCache.containsKey(host)) return new ConnectionConfig.CreationResult(Result.ALREADY_EXIST, null);
		ConnectionConfig cc = new ConnectionConfig(masterUI, host);
		
		if (!ConnectionConfig.madeContactWith(cc)) return new ConnectionConfig.CreationResult(Result.CANT_CONTACT, null);
		
		connConfigCache.put(host, cc);
		return new ConnectionConfig.CreationResult(Result.SUCCESS, cc);
	}
	
	public void store(ConnectionConfig cc) {
		Connection conn = db.getConnection();
		try {
			PreparedStatement ps;
			int connId = cc.getId();
			System.out.println("start connId: " + connId);
			if (connId == -1) {
				ps = conn.prepareStatement(INSERT_CONNECTION, Statement.RETURN_GENERATED_KEYS);
			} else {
				ps = conn.prepareStatement(UPDATE_CONNECTION);
				ps.setInt(2, connId);
			}
			String host = cc.getHost();
			System.out.println("host: " + host);
			ps.setString(1, host);
			
			int affectedRows = ps.executeUpdate();
			System.out.println("affectedRows: " + affectedRows);
			
			if (affectedRows == 0) {
				System.out.println("Failed to store connection config.");
				return;
			}
			
			if (connId == -1) {
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (!generatedKeys.next()) {
					System.out.println("Failed to store connection config.");
					return;
				}
				connId = generatedKeys.getInt(1);
				System.out.println("generatedId: " + connId);
				cc.setId(connId);
			}
			ps.close();
			
			System.out.println("connId: " + connId);
			if (connId > 0) {
				List<DB> dbs = cc.getDbs();
				System.out.println("dbs: " + dbs);
				if (!dbs.isEmpty()) {
					Map<String, Integer> existingDBs = fetchDBs(connId);
					for (DB dbItem : dbs) {
						String dbName = dbItem.getDatabaseName();
						int dbId = existingDBs.containsKey(dbName) ? 
								existingDBs.get(dbName) : insertDB(connId, dbItem);
								
						Map<String, Integer> existingReports = fetchSSReports(dbId);
						for (Report report : dbItem.getReports()) {
							String wbName = report.getWorkbookName();
							int reportId = existingReports.containsKey(wbName) ?
									existingReports.get(wbName) : insertReport(dbId, report);
									
							for (Query query : report.getQueries()) {
								if (query.getId() == -1) {
									insertQuery(reportId, query);
								} else {
									updateQuery(query);
								}
							}
						}
					}
				}
			}
			connConfigCache.put(cc.getHost(), cc);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void storeConnectionConfigs() {
		Collection<ConnectionConfig> connConfig = getConnectionConfigs();
		connConfig.stream().forEach(c -> {
			store(c);
		});
	}
	
	public boolean loadConnectionConfigs() {
		Connection conn = db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_CONNECTIONS);
			ResultSet rs = ps.executeQuery();
			System.out.println("Querying database...");
			
			System.out.println("Retrieving cc records...");
			int id = -1;
			
			Map<Integer, Map<String, DBValue>> connMap = new HashMap<>();
			Map<String, DBValue> dbMap = new HashMap<>();
			while (rs.next()) {
				int nextId = rs.getInt("ConnectionID");
				String host = rs.getString("Hostname");
				
				if (id != nextId) {
					ConnectionConfig cc = constructConnectionConfig(id == -1 ? nextId : id, host, dbMap);
					connConfigCache.put(host, cc);
					id = nextId;
					dbMap.clear();
				}
				
				System.out.println("host: " + host);
				
				if (host == null) {
					System.out.println("Failed to load connection configuration with ID: " + id);
					continue;
				}
				
				int dbId = rs.getInt("DBID");
				String dbName = rs.getString("DatabaseName");
				String wbName = rs.getString("WorkbookName");
				int reportId = rs.getInt("ReportID");
				String wsName = rs.getString("WorksheetName");
				String query = rs.getString("QueryString");
				int queryId = rs.getInt("QueryID");
				
				System.out.println("dbId: " + dbId);
				System.out.println("dbName: " + dbName);
				System.out.println("wbName: " + wbName);
				System.out.println("reportId: " + reportId);
				System.out.println("wsName: " + wsName);
				System.out.println("query: " + query);
				System.out.println("queryId: " + queryId);
				
				String dbTypeStr = rs.getString("DatabaseType");
				String dbFilePath = rs.getString("DBFilePath"); //allowed to be null if not sqlite
				String serverName = rs.getString("ServerName"); //allowed to be null if not server required
				
				System.out.println("dbTypeStr: " + dbTypeStr);
				System.out.println("dbFilePath: " + dbFilePath);
				System.out.println("serverName: " + serverName);
				
				if (dbTypeStr == null) {
					System.out.println("Database Type is null.");
					continue;
				}
				
				int port = rs.getInt("Port");
				String user = rs.getString("Username");
				String pass = rs.getString("Password");
				
				DBType dbType = DBType.valueOf(dbTypeStr.toUpperCase());
				
				if (dbFilePath == null && dbType == DBType.SQLITE) {
					continue;
				}
				
				if ((serverName == null || port == -1 || user == null || pass == null) && (dbType == DBType.SQL_SERVER)) {
					continue;
				}
				
				if ((port == -1 || user == null || pass == null) && (dbType == DBType.ORACLE)) {
					continue;
				}
				
				DBValue dbVal;
				Map<ReportKey, List<QueryHolder>> queries;
				if (dbMap.containsKey(dbName)) { // If DBValue already initialized in the map, retrieve and populate local variables.
					dbVal = dbMap.get(dbName);
					queries = dbVal.getQueries();
				} else { // If not, initialize local variables.
					queries = new HashMap<>();
					File dbFile = dbFilePath == null ? null : new File(dbFilePath);
					dbVal = new DBValue(dbId, dbType, queries, dbFile, serverName, port, user, pass);
				}
				if (wbName != null) {
					ReportKey key = queries.keySet().stream().filter(k -> k.getReportName().equals(wbName)).findFirst().orElse(null);
					List<QueryHolder> queryList = queries.get(key);
					
					if (queryList == null) queryList = new ArrayList<>();
					
					if (queryId > 0 && wsName != null && query != null) {
						System.out.println("Adding QueryHolder to QueryList");
						System.out.println("QueryID: " + queryId);
						System.out.println("WSName: " + wsName);
						System.out.println("Query: " + query);
						queryList.add(new QueryHolder(queryId, wsName, query));
					}
					queries.put(new ReportKey(reportId, wbName), queryList);
				}
				dbMap.put(dbName, dbVal);
				
				ConnectionConfig cc = constructConnectionConfig(id, host, dbMap);
				connConfigCache.put(host, cc);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private ConnectionConfig constructConnectionConfig(int id, String host, Map<String, DBValue> dbMap) {
		ConnectionConfig cc = new ConnectionConfig(this.masterUI, id, host);
		for (String dbName : dbMap.keySet()) {
			DBValue dbVal = dbMap.get(dbName);
			List<Report> reports = new ArrayList<>();
			DBType dbType = dbVal.getDbType();
			
			DB db;
			int dbId = dbVal.getId();
			if (dbType == DBType.SQLITE) {
				File sqliteDBFile = dbVal.getSqliteDBFile();
				
				if (sqliteDBFile == null) {
					System.out.println("No SQLite database name.");
					return null;
				}
				db = new DB(dbId, cc, dbName, sqliteDBFile);
			} else {
				int port = dbVal.getPort();
				String serverName = dbVal.getServerName();
				String user = dbVal.getUser();
				String pass = dbVal.getPass();
				
				if (dbType == DBType.SQL_SERVER && serverName == null) {
					System.out.println("No server name to connect to SQL Server.");
					return null;
				}
				db = (dbType == DBType.SQL_SERVER) ? new DB(dbId, cc, dbName, port, user, pass, serverName) :
					new DB(dbId, cc, dbName, port, user, pass);
			}
			Map<ReportKey, List<QueryHolder>> queryMap = dbVal.getQueries();
			for (ReportKey repKey : queryMap.keySet()) {
				Collection<QueryHolder> queries = queryMap.get(repKey);
				int repId = repKey.getId();
				String wsName = repKey.getReportName();
				Report report = new Report(repId, db, wsName, DBSCGraphicalUserInterface.getDefaultDoubleBandFormat(), DBSCGraphicalUserInterface.getDefaultTitleStyle());
				for (QueryHolder qh : queries) {
					report.getQueries().add(new Query(qh.getId(), report, qh.getWorksheetName(), qh.getQuery()));
				}
				reports.add(report);
			}
			db.setReports(reports);
			cc.getDbs().add(db);
		}
		return cc;
	}
	
	public ConnectionConfig getConnectionConfig(String host) {
		return connConfigCache.get(host);
	}
	
	public Collection<ConnectionConfig> getConnectionConfigs() {
		return Collections.unmodifiableCollection(connConfigCache.values());
	}
	
	public Database getDatabaseConnection(DB db) {
		ConnectionConfig cc = db.getConnConfig();
		
		String host = cc.getHost();
		String dbName = db.getDatabaseName();
		Database result = this.connDatabaseCache.get(host, dbName);
		
		if (result == null) {
			switch (db.getType()) {
			case SQLITE:
				if (!cc.isLocalHost()) {
					System.out.println("Tried to remotely access a SQLite database.");
					return null;
				}
				result = new SQLiteDatabase(db.getSqliteDBFile());
				break;
			case SQL_SERVER:
				result = new SQLServerDatabase(db.getServerName(), cc.getHost(), Integer.toString(db.getPort()), db.getDatabaseName(), db.getUser(), db.getPass());
				break;
			case ORACLE:
				result = new OracleDatabase(db.getServerName(), cc.getHost(), Integer.toString(db.getPort()), db.getDatabaseName(), db.getUser(), db.getPass());
				break;
			default:
				break;
			}
		}
		
		return result;
	}
	
	public boolean deleteConnectionConfig(ConnectionConfig cc) {
		if (cc.getId() == -1) return false;
		
		Connection conn = db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_CONNECTION);
			ps.setInt(1, cc.getId());
			ps.executeUpdate();
			
			cc.getDbs().stream().forEach(db -> deleteDB(db));
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public boolean deleteDB(DB db) {
		System.out.println("Deleting DB...");
		System.out.println("DBID: " + db.getId());
		if (db.getId() == -1) return false;
		
		Connection conn = this.db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_DB);
			ps.setInt(1, db.getId());
			ps.executeUpdate();
			
			db.getReports().stream().forEach(report -> deleteReport(report));
			db.getConnConfig().getDbs().remove(db);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public boolean deleteReport(Report report) {
		if (report.getId() == -1) return false;
		
		Connection conn = this.db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_SSREPORT);
			ps.setInt(1, report.getId());
			ps.executeUpdate();
			
			report.getQueries().stream().forEach(query -> deleteQuery(query));
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public boolean deleteQuery(Query query) {
		if (query.getId() == -1) return false;
		
		Connection conn = this.db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_QUERY);
			ps.setInt(1, query.getId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public void close() {
		try {
			db.getConnection().close();
			System.out.println("database closed");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static class ReportKey {
		
		@Getter private int id;
		@Getter private String reportName;
		
		public ReportKey(int id, String reportName) {
			this.id = id;
			this.reportName = reportName;
		}
	}
	
	private static class DBValue {
		
		@Getter private int id;
		@Getter private DBType dbType;
		@Getter private Map<ReportKey, List<QueryHolder>> queries;
		@Getter private File sqliteDBFile;
		
		@Getter private int port;
		@Getter private String user;
		@Getter private String pass;
		@Getter private String serverName;
		
		public DBValue(int id, DBType dbType, Map<ReportKey, List<QueryHolder>> queries, File sqliteDBFile, String serverName, int port, String user, String pass) {
			this.id = id;
			this.dbType = dbType;
			this.queries = queries;
			this.sqliteDBFile = sqliteDBFile;
			this.serverName = serverName;
			this.port = port;
			this.user = user;
			this.pass = pass;
		}
	}
	
	private static class QueryHolder {
		
		@Getter private int id;
		@Getter private String worksheetName;
		@Getter private String query;
		
		public QueryHolder(int id, String worksheetName, String query) {
			this.id = id;
			this.worksheetName = worksheetName;
			this.query = query;
		}
	}
}
