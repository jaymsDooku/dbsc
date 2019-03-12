package io.jayms.dbsc;

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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import io.jayms.dbsc.model.ConnectionConfig;
import io.jayms.dbsc.model.DB;
import io.jayms.dbsc.model.DBType;
import io.jayms.dbsc.model.Query;
import io.jayms.dbsc.model.Report;
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
			 + "Port INTEGER NOT NULL, "
			 + "Username TEXT NOT NULL, "
			 + "Password TEXT NOT NULL, "
			 + "UNIQUE(Hostname, Port) "
			 + "ON CONFLICT IGNORE"
			 + ")";
	private static final String INSERT_CONNECTION = "INSERT INTO CONNECTION(Hostname, Port, Username, Password) VALUES (?, ?, ?, ?)";
	private static final String SELECT_CONNECTIONS = "SELECT * FROM CONNECTION LEFT JOIN DBS USING(ConnectionID) LEFT JOIN SSREPORT USING (DBID) LEFT JOIN SSREPORTQUERIES USING(ReportID)LEFT JOIN QUERIES USING (QueryID)";
	private static final String DELETE_CONNECTION = "DELETE FROM CONNECTION WHERE ConnectionID = ?";
	private static final String SELECT_CONNECTION = "SELECT * FROM CONNECTION WHERE ConnectionID = ?";
	private static final String UPDATE_CONNECTION = "UPDATE CONNECTION SET Hostname = ?, Port = ?, Username = ?, Password = ? WHERE ConnectionID = ?";
	
	private static final String DB_TBL = "DBS";
	private static final String CREATE_DB_TBL = "CREATE TABLE DBS ("
			+ "DBID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "ConnectionID INTEGER NOT NULL, "
			+ "DatabaseName TEXT NOT NULL, "
			+ "DatabaseType TEXT NOT NULL, "
			+ "DBFilePath TEXT DEFAULT NULL, "
			+ "ServerName TEXT DEFAULT NULL"
			+ ")";
	private static final String INSERT_DB = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType) VALUES (?, ?, ?)";
	private static final String INSERT_DB_WITH_FILE = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, DBFilePath) VALUES (?, ?, ?, ?)";
	private static final String INSERT_DB_WITH_SERVERNAME = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, ServerName) VALUES (?, ?, ?, ?)";
	private static final String SELECT_DB = "SELECT DBID, DatabaseName FROM DBS WHERE ConnectionID = ?";
	private static final String DELETE_DB = "DELETE FROM DBS WHERE DBID = ?";
	
	private static final String SSREPORT_TBL = "SSREPORT";
	private static final String CREATE_SSREPORT_TBL = "CREATE TABLE SSREPORT ("
			+ "ReportID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "DBID INTEGER NOT NULL, "
			+ "WorkbookName TEXT NOT NULL"
			+ ")";
	private static final String INSERT_SSREPORT = "INSERT INTO SSREPORT(DBID, WorkbookName) VALUES (?, ?)";
	private static final String SELECT_SSREPORT = "SELECT ReportID, WorkbookName FROM SSREPORT WHERE DBID = ?";
	private static final String DELETE_SSREPORT = "DELETE FROM SSREPORT WHERE ReportID";
	
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
		PreparedStatement ps;
		if (dbItem.getType() == DBType.SQLITE) {
			ps = conn.prepareStatement(INSERT_DB_WITH_FILE, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, connId);
			ps.setString(2, dbItem.getDatabaseName());
			ps.setString(3, dbItem.getType().toString());
			ps.setString(4, dbItem.getSqliteDBFile().getAbsolutePath());
		} else if (dbItem.getType() == DBType.SQL_SERVER) {
			ps = conn.prepareStatement(INSERT_DB_WITH_SERVERNAME, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, connId);
			ps.setString(2, dbItem.getDatabaseName());
			ps.setString(3, dbItem.getType().toString());
			ps.setString(4, dbItem.getServerName());
		} else {
			ps = conn.prepareStatement(INSERT_DB, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, connId);
			ps.setString(2, dbItem.getDatabaseName());
			ps.setString(3, dbItem.getType().toString());
		}
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1);
		ps.close();
		return id;
	}
	
	private int insertReport(int dbId, Report report) throws SQLException {
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(INSERT_SSREPORT, Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, dbId);
		ps.setString(2, report.getWorkbookName());
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
	
	private void updateQuery(int reportId, Query query) throws SQLException {
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(UPDATE_QUERY);
		ps.setString(1, query.getWorksheetName());
		ps.setString(2, query.getQuery());
		ps.setInt(3, query.getId());
		ps.executeUpdate();
		ps.close();
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
				ps.setInt(5, connId);
			}
			String host = cc.getHost();
			int port = cc.getPort();
			String user = cc.getUser();
			String pass = cc.getPass();
			System.out.println("host: " + host);
			System.out.println("port: " + port);
			System.out.println("user: " + user);
			System.out.println("pass: " + pass);
			ps.setString(1, host);
			ps.setInt(2, port);
			ps.setString(3, user);
			ps.setString(4, pass); // TODO: Encryption
			
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
									updateQuery(reportId, query);
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
			
			Map<String, DBValue> dbMap = new HashMap<>();
			while (rs.next()) {
				int nextId = rs.getInt("ConnectionID");
				String host = rs.getString("Hostname");
				int port = rs.getInt("Port");
				String user = rs.getString("Username");
				String pass = rs.getString("Password");
				
				if (id != nextId) {
					/*ConnectionConfig cc = constructConnectionConfig(id, host, port, user, pass, dbMap);
					connConfigCache.put(host, cc);*/
					id = nextId;
					dbMap.clear();
				}
				
				System.out.println("host: " + host);
				System.out.println("port: " + port);
				System.out.println("user: " + user);
				System.out.println("pass: " + pass);
				
				if (host == null || port == 0 || user == null || pass == null) {
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
				
				DBType dbType = DBType.valueOf(dbTypeStr.toUpperCase());
				
				if (dbFilePath == null && dbType == DBType.SQLITE) {
					continue;
				}
				
				if (serverName == null && (dbType == DBType.SQL_SERVER || dbType == DBType.ORACLE)) {
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
					dbVal = new DBValue(dbId, dbType, queries, dbFile, serverName);
				}
				if (wbName != null) {
					ReportKey key = queries.keySet().stream().filter(k -> k.getReportName().equals(wbName)).findFirst().orElse(null);
					List<QueryHolder> queryList = key == null ? null : queries.get(key);
					
					if (queryList == null) queryList = new ArrayList<>();
					
					if (queryId > 0 && wsName != null && query != null) {
						queryList.add(new QueryHolder(queryId, wsName, query));
					}
					queries.put(new ReportKey(reportId, wbName), queryList);
				}
				dbMap.put(dbName, dbVal);
				
				ConnectionConfig cc = constructConnectionConfig(id, host, port, user, pass, dbMap);
				connConfigCache.put(host, cc);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private ConnectionConfig constructConnectionConfig(int id, String host, int port, String user, String pass, Map<String, DBValue> dbMap) {
		ConnectionConfig cc = new ConnectionConfig(this.masterUI, id, host, port, user, pass);
		for (String dbName : dbMap.keySet()) {
			DBValue dbVal = dbMap.get(dbName);
			List<Report> reports = new ArrayList<>();
			DBType dbType = dbVal.getDbType();
			
			File sqliteDBFile = dbVal.getSqliteDBFile();
			String serverName = dbVal.getServerName();
			
			DB db;
			if (dbType == DBType.SQLITE) {
				if (sqliteDBFile == null) {
					System.out.println("No SQLite database name.");
					return null;
				}
				db = new DB(cc, dbName, sqliteDBFile);
			} else {
				if (serverName == null) {
					System.out.println("No server name to connect to SQL Server or Oracle.");
					return null;
				}
				db = new DB(cc, dbName, serverName, dbType);
			}
			Map<ReportKey, List<QueryHolder>> queryMap = dbVal.getQueries();
			for (ReportKey repKey : queryMap.keySet()) {
				Collection<QueryHolder> queries = queryMap.get(repKey);
				int repId = repKey.getId();
				String wsName = repKey.getReportName();
				Report report = new Report(repId, db, wsName, masterUI.getDefaultDoubleBandFormat());
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
	
	public Database getDatabaseConnection(ConnectionConfig cc, DB db) {
		
		if (!cc.getDbs().contains(db)) {
			System.out.println("This database isn't linked to this connection config!");
			return null;
		}
		
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
				result = new SQLServerDatabase(db.getServerName(), cc.getHost(), Integer.toString(cc.getPort()), db.getDatabaseName(), cc.getUser(), cc.getPass());
				break;
			case ORACLE:
				result = new OracleDatabase(db.getServerName(), cc.getHost(), Integer.toString(cc.getPort()), db.getDatabaseName(), cc.getUser(), cc.getPass());
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
		if (db.getId() == -1) return false;
		
		Connection conn = this.db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_DB);
			ps.setInt(1, db.getId());
			ps.executeUpdate();
			
			db.getReports().stream().forEach(report -> deleteReport(report));
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
		@Getter private String serverName;
		
		public DBValue(int id, DBType dbType, Map<ReportKey, List<QueryHolder>> queries, File sqliteDBFile, String serverName) {
			this.dbType = dbType;
			this.queries = queries;
			this.sqliteDBFile = sqliteDBFile;
			this.serverName = serverName;
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
