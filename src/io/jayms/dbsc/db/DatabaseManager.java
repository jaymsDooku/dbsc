package io.jayms.dbsc.db;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

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
import io.jayms.dbsc.util.Validation;
import io.jayms.xlsx.db.DBTools;
import io.jayms.xlsx.db.Database;
import io.jayms.xlsx.db.DatabaseColumn;
import io.jayms.xlsx.db.OracleDatabase;
import io.jayms.xlsx.db.SQLServerDatabase;
import io.jayms.xlsx.model.FieldConfiguration;
import io.jayms.xlsx.util.JSONTools;
import lombok.Getter;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class DatabaseManager {

	/**
	 * Queries pertaining to the management of the local SQLite database.
	 */
	
	// Connection Table Name
	private static final String CONNECTION_TBL = "CONNECTION";
	
	/**
	 * Create Connection table with ConnectionID auto-increment primary key, and a unique Hostname.
	 */
	private static final String CREATE_CONNECTION_TBL = "CREATE TABLE CONNECTION ("
			 + "ConnectionID INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + "Hostname TEXT NOT NULL, "
			 + "UNIQUE(Hostname) "
			 + "ON CONFLICT IGNORE"
			 + ")";
	/**
	 * Insert record into connection table.
	 */
	private static final String INSERT_CONNECTION = "INSERT INTO CONNECTION(Hostname) VALUES (?)";
	
	/**
	 * Select all of the data in the database appropriately; utilizes joins to link each table together.
	 * 
	 * Data retrieved from this query is used to populate the connection tree view upon start up of the application.
	 */
	private static final String SELECT_CONNECTIONS = "SELECT * FROM CONNECTION "
			+ "LEFT JOIN DBS USING(ConnectionID) "
			+ "LEFT JOIN SSREPORT USING (DBID) "
			+ "LEFT JOIN QUERIES USING (ReportID)";
	
	/**
	 * Delete a connection record from connection table.
	 */
	private static final String DELETE_CONNECTION = "DELETE FROM CONNECTION WHERE ConnectionID = ?";
	
	/**
	 * Change the hostname of a connection record.
	 */
	private static final String UPDATE_CONNECTION = "UPDATE CONNECTION SET Hostname = ? WHERE ConnectionID = ?";
	
	// Database Table Name
	private static final String DB_TBL = "DBS";
	
	/**
	 * Create Database table with DBID auto-increment primary key, a foreign reference to the Connection table,
	 * 		a database name, textual representation of the database type.
	 * 	
	 * 		ORACLE AND SQLSERVER ONLY: Port, Username, Password
	 * 
	 * 		SQLSERVER ONLY: ServerName
	 * 
	 * 		SQLITE-ONLY: DBFilePath 
	 */
	private static final String CREATE_DB_TBL = "CREATE TABLE DBS ("
			+ "DBID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "ConnectionID INTEGER NOT NULL, "
			+ "DatabaseName TEXT NOT NULL, "
			+ "DatabaseType TEXT NOT NULL, "
			+ "Port INTEGER NOT NULL DEFAULT -1, "
			+ "Username TEXT DEFAULT NULL, "
			+ "Password TEXT DEFAULT NULL, "
			+ "DBFilePath TEXT DEFAULT NULL, "
			+ "ServerName TEXT DEFAULT NULL, "
			+ "UNIQUE(ConnectionID, DatabaseName) "
			+ "ON CONFLICT IGNORE"
			+ ")";
	/**
	 * SQLite version of insert statement to the Database table. Only specifies general fields and the DBFilePath.
	 */
	private static final String INSERT_DB_SQLITE = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, DBFilePath) VALUES (?, ?, ?, ?)";
	/**
	 * Oracle version of insert statement to the Database table. Only specifies general fields and port, username and password.
	 */
	private static final String INSERT_DB_ORACLE = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, Port, Username, Password) VALUES (?, ?, ?, ?, ?, ?)";
	/**
	 * SQLServer version of insert statement to the Database table. Only specifies general fields and port, username, password, and server name.
	 */
	private static final String INSERT_DB_SQLSERVER = "INSERT INTO DBS(ConnectionID, DatabaseName, DatabaseType, Port, Username, Password, ServerName) VALUES (?, ?, ?, ?, ?, ?, ?)";
	/**
	 * Update statement of database table includes all fields as it doesn't care about database type.
	 */
	private static final String UPDATE_DB = "UPDATE DBS SET DatabaseType = ?, Port = ?, Username = ?, Password = ?, DBFilePath = ?, ServerName = ? WHERE DBID = ?";
	/**
	 * Selects all databases belonging to the specified connection configuration.
	 */
	private static final String SELECT_DB = "SELECT DBID, DatabaseName FROM DBS WHERE ConnectionID = ?";
	/**
	 * Delete databse from the database table.
	 */
	private static final String DELETE_DB = "DELETE FROM DBS WHERE DBID = ?";
	
	// Report Table Name
	private static final String SSREPORT_TBL = "SSREPORT";
	
	/**
	 * Create Report table with ReportID auto-increment primary key, a foreign reference to the Database table,
	 * 		workbook name, and JSON representation of double band format, title style, and sub total style.
	 */
	private static final String CREATE_SSREPORT_TBL = "CREATE TABLE SSREPORT ("
			+ "ReportID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "DBID INTEGER NOT NULL, "
			+ "WorkbookName TEXT NOT NULL, "
			+ "DoubleBandFormat TEXT NOT NULL,"
			+ "TitleStyle TEXT NOT NULL,"
			+ "SubTotalStyle TEXT NOT NULL"
			+ ")";
	/**
	 * Insert report into report table.
	 */
	private static final String INSERT_SSREPORT = "INSERT INTO SSREPORT(DBID, WorkbookName, DoubleBandFormat, TitleStyle, SubTotalStyle) VALUES (?, ?, ?, ?, ?)";
	/**
	 * Update existing report record.
	 */
	private static final String UPDATE_SSREPORT = "UPDATE SSREPORT SET WorkbookName = ?, DoubleBandFormat = ?, TitleStyle = ?, SubTotalStyle = ? WHERE ReportID = ?";
	/**
	 * Select reports belonging to the specified database.
	 */
	private static final String SELECT_SSREPORT = "SELECT ReportID, WorkbookName FROM SSREPORT WHERE DBID = ?";
	/**
	 * Delete report from report table.
	 */
	private static final String DELETE_SSREPORT = "DELETE FROM SSREPORT WHERE ReportID = ?";
	
	//Queries Table Name
	private static final String QUERIES_TBL = "QUERIES";
	
	/**
	 * Create Queries Table with QueryID auto-increment primary key, a foreign reference to the Report table,
	 * 		worksheet name, contents of the query, and JSON representation of field configurations.
	 */
	private static final String CREATE_QUERIES_TBL = "CREATE TABLE QUERIES ("
			+ "QueryID INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "ReportID INTEGER NOT NULL, "
			+ "WorksheetName TEXT NOT NULL, "
			+ "QueryString TEXT NOT NULL, "
			+ "FieldConfigurations TEXT DEFAULT NULL, "
			+ "UNIQUE (WorksheetName, QueryString) "
			+ "ON CONFLICT IGNORE"
			+ ")";
	/**
	 * Insert query into Queries Table.
	 */
	private static final String INSERT_QUERY = "INSERT INTO QUERIES(ReportID, WorksheetName, QueryString, FieldConfigurations) VALUES (?, ?, ?, ?)";
	/**
	 * Update existing query record in Queries Table.
	 */
	private static final String UPDATE_QUERY = "UPDATE QUERIES SET WorksheetName = ?, QueryString = ?, FieldConfigurations = ? WHERE QueryID = ?";
	/**
	 * Delete query from Queries table.
	 */
	private static final String DELETE_QUERY = "DELETE FROM QUERIES WHERE QueryID = ?";
	
	/**
	 * Reference to main application.
	 */
	@Getter private final DBSCGraphicalUserInterface masterUI;
	
	private SQLiteDatabase db; // Local SQLite DB.
	
	/**
	 * Collections for storage and later retrieval. Not specialized cache types, but adequate for our usage.
	 */
	private Map<String, ConnectionConfig> connConfigCache = new HashMap<>(); // Holds loaded connection config - key is the hostname of connection config.
	private Table<String, String, Database> connDatabaseCache = HashBasedTable.create(); // Table data structure holding database connections for unique connection - database combos. 
	
	/**
	 * Instantiates DatabaseManager and loads up connection configs from the SQLiteDatabase.
	 * @param masterUI - reference to main application.
	 * @param db - local SQLite DB.
	 */
	public DatabaseManager(DBSCGraphicalUserInterface masterUI, SQLiteDatabase db) {
		this.masterUI = masterUI;
		this.db = db;
		ensureTables(); // Prepare tables of SQLiteDatabase.
	}
	
	/**
	 * Ensures the existance of specified table name.
	 * 
	 * If the specified table doesn't exist, executes the table creation query.
	 * 
	 * @param tblName - name of table
	 * @param createTable - table creation query
	 */
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
	
	/**
	 * Drop a table if exists; mainly for debugging purposes.
	 * 
	 * @param tbl - name of table to drop.
	 */
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
	
	/**
	 * Many calls to {@link #ensureTable(String, String)} to populate the local SQLite database with required tables.
	 */
	private void ensureTables() {
		if (DBSCGraphicalUserInterface.DEBUG) {
			dropTable(CONNECTION_TBL);
			dropTable(DB_TBL);
			dropTable(SSREPORT_TBL);
			dropTable(QUERIES_TBL);
		}
		
		ensureTable(CONNECTION_TBL, CREATE_CONNECTION_TBL);
		ensureTable(DB_TBL, CREATE_DB_TBL);
		ensureTable(SSREPORT_TBL, CREATE_SSREPORT_TBL);
		ensureTable(QUERIES_TBL, CREATE_QUERIES_TBL);
	}
	
	/**
	 * Insert database item into connection config.
	 * 
	 * Looks at the database type of the DB, and acts accordingly.
	 * 
	 * @param connId - Id of connection config (ConnectionID of DBS table).
	 * @param dbItem - DB to insert.
	 * @return - Id of DB's record after insertion.
	 */
	private int insertDB(int connId, DB dbItem) throws SQLException {
		Connection conn = db.getConnection();
		PreparedStatement ps = null;
		if (dbItem.getType() == DBType.SQLITE) {
			ps = conn.prepareStatement(INSERT_DB_SQLITE, Statement.RETURN_GENERATED_KEYS); // Prepare statement with SQLite version of insert statement.
			// Set parameters of query.
			ps.setInt(1, connId);
			ps.setString(2, dbItem.getDatabaseName());
			ps.setString(3, dbItem.getType().toString());
			ps.setString(4, dbItem.getSqliteDBFile().getAbsolutePath()); // Because it is a SQLite database, specify path of file.
		} else  {
			ps = conn.prepareStatement((dbItem.getType() == DBType.SQL_SERVER) ? INSERT_DB_SQLSERVER 
					: INSERT_DB_ORACLE, Statement.RETURN_GENERATED_KEYS); // Prepare statement with SQLServer or Oracle version of insert statement depending on database type.
			// Set paramters of query.
			ps.setInt(1, connId);
			ps.setString(2, dbItem.getDatabaseName());
			ps.setString(3, dbItem.getType().toString());
			ps.setInt(4, dbItem.getPort());
			ps.setString(5, dbItem.getUser()); // Because Oracle and SQLServer databases require authentication, set user and password.
			ps.setString(6, dbItem.getPass());
			if (dbItem.getType() == DBType.SQL_SERVER) { // Set server name for SQLServer.
				ps.setString(7, dbItem.getServerName());
			}
		}
		
		ps.executeUpdate(); // Execute
		int id = ps.getGeneratedKeys().getInt(1); // Retrieve generated id of record after insertion.
		ps.close();
		return id;
	}
	
	/**
	 * Insert report item into database.
	 * 
	 * Convert style items like the DoubleBandFormat and Styles for title and sub-total into JSON.
	 * 
	 * @param dbId - Id of DB the report belongs to.
	 * @param report - Report to insert.
	 * @return - Id of Report's record after insertion.
	 * @throws SQLException
	 */
	private int insertReport(int dbId, Report report) throws SQLException {
		// Call appropriate toJSON methods to convert into JSON representation.
		JSONObject dbFormatJson = DoubleBandFormatHolder.toJSON(report.getDoubleBandFormat());
		JSONObject titleJson = StyleHolder.toJSON(report.getTitleStyle());
		JSONObject subTotalJson = StyleHolder.toJSON(report.getSubTotalStyle());
		
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(INSERT_SSREPORT, Statement.RETURN_GENERATED_KEYS); // Prepare statement with insert report statement.
		// Set parameters of query.
		ps.setInt(1, dbId);
		ps.setString(2, report.getWorkbookName());
		// Insert JSON representations in string form.
		ps.setString(3, dbFormatJson.toString()); 
		ps.setString(4, titleJson.toString());
		ps.setString(5, subTotalJson.toString());
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1); // Retrieve generated id of record after insertion.
		ps.close();
		return id;
	}
	
	/**
	 * Insert query item into database.
	 * 
	 * @param reportId - Id of Report the query belongs to.
	 * @param query - Query to insert.
	 * @return - Id of Query's record after insertion.
	 * @throws SQLException
	 */
	private int insertQuery(int reportId, Query query) throws SQLException {
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS); // Prepare statement with insert query statement.
		//Set parameters of query.
		ps.setInt(1, reportId);
		ps.setString(2, query.getWorksheetName());
		ps.setString(3, query.getQuery());
		ps.setString(4, query.getFieldConfigs() != null ? 
				JSONTools.ToJSON.toJSON(query.getFieldConfigs()).toString() : null); // If field configurations exist for this query, then convert it into JSON, then into a string and set it; otherwise, leave it as null.
		ps.executeUpdate();
		int id = ps.getGeneratedKeys().getInt(1); // Retrieve generated id of query after insertion.
		ps.close();
		return id;
	}
	
	/**
	 * Fetch a collection of database names mapped to their corresponding IDs, using the ID of a specified connection.
	 * 
	 * @param connectionId - Id of Connection the databases belong to.
	 * @return - Map of database names to database IDs.
	 * @throws SQLException
	 */
	private Map<String, Integer> fetchDBs(int connectionId) throws SQLException {
		Map<String, Integer> result = new HashMap<>();
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(SELECT_DB); // Prepare statement with select query.
		//Set parameters of query.
		ps.setInt(1, connectionId);
		ResultSet rs = ps.executeQuery(); // Execute
		while (rs.next()) { // Using all the records, populate resultant map.
			result.put(rs.getString("DatabaseName"), rs.getInt("DBID"));
		}
		rs.close();
		ps.close();
		return result;
	}
	
	/**
	 * Fetch a collection of report/workbook names mapped to their corresponding IDs, using the ID of a specified database.
	 * 
	 * @param connectionId - Id of Database the reports belong to.
	 * @return - Map of database names to database IDs.
	 * @throws SQLException
	 */
	private Map<String, Integer> fetchSSReports(int dbId) throws SQLException {
		Map<String, Integer> result = new HashMap<>();
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(SELECT_SSREPORT); // Prepare statement with select query.
		//Set parameters of query.
		ps.setInt(1, dbId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) { // Using all the records, populate resultant map.
			result.put(rs.getString("WorkbookName"), rs.getInt("ReportID"));
		}
		rs.close();
		ps.close();
		return result;
	}
	
	/**
	 * Update existing DB record.
	 * 
	 * @param db - DB to update
	 * @throws SQLException
	 */
	public void updateDB(DB db) throws SQLException {
		Connection conn = this.db.getConnection();
		PreparedStatement ps = conn.prepareStatement(UPDATE_DB); // Prepare statement with update query.
		//Set parameters of query.
		ps.setString(1, db.getType().toString());
		ps.setInt(2, db.getPort());
		ps.setString(3, db.getUser());
		ps.setString(4, db.getPass());
		ps.setString(5, db.getSqliteDBFile() != null ? db.getSqliteDBFile().getAbsolutePath() : null); // Small tertiary operation to avoid nullpointer when calling method on null object.
		ps.setString(6, db.getServerName());
		ps.setInt(7, db.getId());
		ps.executeUpdate(); // Execute
		ps.close();
	}
	
	/**
	 * Update existing Report record.
	 * 
	 * @param report - Report to update
	 * @throws SQLException
	 */
	public void updateReport(Report report) throws SQLException {
		//Call appropriate toJSON methods to convert into JSON representation.
		JSONObject dbFormatJson = DoubleBandFormatHolder.toJSON(report.getDoubleBandFormat());
		JSONObject titleJson = StyleHolder.toJSON(report.getTitleStyle());
		JSONObject subTotalJson = StyleHolder.toJSON(report.getSubTotalStyle());
		
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(UPDATE_SSREPORT); // Prepare statement with update query.
		//Set parameters of query.
		ps.setString(1, report.getWorkbookName());
		//Set string form of JSON representation.
		ps.setString(2, dbFormatJson.toString());
		ps.setString(3, titleJson.toString());
		ps.setString(4, subTotalJson.toString());
		ps.setInt(5, report.getId());
		ps.executeUpdate(); // Execute
		ps.close();
	}
	
	/**
	 * Update existing Query record.
	 * 
	 * @param query - Query to update
	 * @throws SQLException
	 */
	public void updateQuery(Query query) throws SQLException {
		Connection conn = db.getConnection();
		PreparedStatement ps = conn.prepareStatement(UPDATE_QUERY); // Prepare statement with update query.
		ps.setString(1, query.getWorksheetName());
		ps.setString(2, query.getQuery());
		ps.setString(3, query.getFieldConfigs() != null ? JSONTools.ToJSON.toJSON(query.getFieldConfigs()).toString() : null); // If field configurations exist for this query, then convert it into JSON, then into a string; otherwise, leave it as null. 
		ps.setInt(4, query.getId());
		ps.executeUpdate(); // Execute
		ps.close();
	}
	
	/**
	 * Create connection config with specified hostname. Return value couples the result with a reason for the result, allowing for the reason to be reacted upon accordingly.
	 * 
	 * @see ConnectionConfig.CreationResult
	 * 
	 * @param host - hostname of connection config.
	 * @return - A POJO wrapping information regarding the Connection Config and the result of it's creation.
	 */
	public ConnectionConfig.CreationResult createConnectionConfig(String host) {
		if (connConfigCache.containsKey(host)) { // Checks if a connection config with this hostname already exists; if it does, reject.
			return new ConnectionConfig.CreationResult(Result.ALREADY_EXIST, null); // Give reason and return null.
		}
		ConnectionConfig cc = new ConnectionConfig(masterUI, host); // Instantiate connection config
		
		if (!ConnectionConfig.madeContactWith(cc)) { // Checks if a connection can actually be made to the host; if it can't, reject.
			return new ConnectionConfig.CreationResult(Result.CANT_CONTACT, null); // Give reason and return null.
		}
		
		connConfigCache.put(host, cc); // Store connection config for later retrieval.
		return new ConnectionConfig.CreationResult(Result.SUCCESS, cc); // Specify success and return result.
	}
	
	/**
	 * Save an individual connection config to the local SQLite Database for persistence.
	 * 
	 * @param cc - connection config to save.
	 */
	public void store(ConnectionConfig cc) {
		Connection conn = db.getConnection();
		try {
			PreparedStatement ps;
			int connId = cc.getId();
			if (connId == -1) { // If the connection ID stored in the connection config is -1, it's new so prepare statement with insert statement.
				ps = conn.prepareStatement(INSERT_CONNECTION, Statement.RETURN_GENERATED_KEYS); 
			} else { // Otherwise, it is an existing record, so it should be updated.
				ps = conn.prepareStatement(UPDATE_CONNECTION);
				ps.setInt(2, connId);
			}
			String host = cc.getHost();
			ps.setString(1, host); // Set hostname
			
			ps.executeUpdate(); // Execute
			
			if (connId == -1) { // If it's new, it doesn't have its ID yet; we need to retrieve the one generated after the insert statement was executed.
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (!generatedKeys.next()) { // Check it generated a key.
					System.out.println("Failed to store connection config.");
					return;
				}
				connId = generatedKeys.getInt(1); // Retrieve generated key after insertion.
				cc.setId(connId); // Set it for later use.
			}
			ps.close();
			
			if (connId > 0) { // After our connection record has been handled, move onto saving other data.
				List<DB> dbs = cc.getDbs();
				if (!dbs.isEmpty()) { // Check databases exist in the connection config to save.
					Map<String, Integer> existingDBs = fetchDBs(connId); // Fetch existing databases linked to the connection config.
					for (DB dbItem : dbs) {
						String dbName = dbItem.getDatabaseName(); 
						int dbId;
						if (existingDBs.containsKey(dbName)) { // If the database exists,
							dbId = existingDBs.get(dbName); // use the existing id
							updateDB(dbItem); // update the database record
						} else { // otherwise, 
							dbId = insertDB(connId, dbItem); // insert the new database.
						}
								
						Map<String, Integer> existingReports = fetchSSReports(dbId); // Fetch existing reports linked to the database.
						for (Report report : dbItem.getReports()) {
							String wbName = report.getWorkbookName();
							int reportId;
							if (existingReports.containsKey(wbName)) { // If the report exists,
								reportId = existingReports.get(wbName); // use the existing id
								updateReport(report); // update the report record
							} else { // otherwise,
								reportId = insertReport(dbId, report); // insert the new report.
							}
									
							for (Query query : report.getQueries()) {
								if (query.getId() == -1) { // If query doesn't exist,
									insertQuery(reportId, query); // insert query
								} else { // otherwise,
									updateQuery(query); // update query.
								}
							}
						}
					}
				}
			}
			connConfigCache.put(cc.getHost(), cc); // Store for later retrieval.
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Store all of the currently loaded connection configs.
	 */
	public void storeConnectionConfigs() {
		Collection<ConnectionConfig> connConfig = getConnectionConfigs();
		connConfig.stream().forEach(c -> { // Iterate over all the connection configs and store them.
			store(c);
		});
	}
	
	/**
	 * Load all of the connection configs in the local SQLite Database.
	 * @return
	 */
	public boolean loadConnectionConfigs() {
		Connection conn = db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(SELECT_CONNECTIONS); // Prepare statement with select query of all data.
			ResultSet rs = ps.executeQuery();
			
			
			Map<ConnectionKey, Map<String, DBValue>> connMap = new HashMap<>();
			while (rs.next()) { // Iterate over all records in the result set.
				int id = rs.getInt("ConnectionID"); // Get connection ID.
				String host = rs.getString("Hostname");
				
				Map<String, DBValue> dbMap = connMap.get(new ConnectionKey(id, host)); // get database store for this connection config
				
				if (dbMap == null) { // oh it doesn't exist yet, let's make new map
					dbMap = new HashMap<>();
				}
				System.out.println("dbMap: " + dbMap);
				
				//debug
				System.out.println("host: " + host);
				
				if (host == null) { // Ensure host isn't null.
					System.out.println("Failed to load connection configuration with ID: " + id);
					continue;
				}
				
				// Retrieve all data
				int dbId = rs.getInt("DBID");
				String dbName = rs.getString("DatabaseName");
				String wbName = rs.getString("WorkbookName");
				String dbFormatJson = rs.getString("DoubleBandFormat");
				String titleStyleJson = rs.getString("TitleStyle");
				String subTotalJson = rs.getString("SubTotalStyle");
				int reportId = rs.getInt("ReportID");
				String wsName = rs.getString("WorksheetName");
				String fieldConfigsJson = rs.getString("FieldConfigurations");
				String query = rs.getString("QueryString");
				int queryId = rs.getInt("QueryID");
				String dbTypeStr = rs.getString("DatabaseType");
				String dbFilePath = rs.getString("DBFilePath"); //allowed to be null if not sqlite
				String serverName = rs.getString("ServerName"); //allowed to be null if not server required
				int port = rs.getInt("Port");
				String user = rs.getString("Username");
				String pass = rs.getString("Password");
				
				//debug
				System.out.println("dbId: " + dbId);
				System.out.println("dbName: " + dbName);
				System.out.println("wbName: " + wbName);
				System.out.println("dbFormat: " + dbFormatJson);
				System.out.println("titleStyle: " + titleStyleJson);
				System.out.println("reportId: " + reportId);
				System.out.println("wsName: " + wsName);
				System.out.println("fieldConfigs: " + fieldConfigsJson);
				System.out.println("query: " + query);
				System.out.println("queryId: " + queryId);
				System.out.println("dbTypeStr: " + dbTypeStr);
				System.out.println("dbFilePath: " + dbFilePath);
				System.out.println("serverName: " + serverName);
				System.out.println("port: " + port);
				System.out.println("user: " + user);
				System.out.println("pass: " + pass);
				
				if (dbName != null) {
					DBType dbType = dbTypeStr != null ? DBType.valueOf(dbTypeStr.toUpperCase()) : null; // Convert textual representation of database type to enumeration.
				
					DBValue dbVal;
					Map<ReportKey, List<QueryHolder>> queries;
					if (dbMap.containsKey(dbName)) { // If DBValue already initialized in the map, retrieve and populate local variables.
						dbVal = dbMap.get(dbName);
						queries = dbVal.getQueries();
					} else { // If not, initialize local variables.
						queries = new HashMap<>();
						File dbFile = dbFilePath == null ? null : new File(dbFilePath);
						dbVal = new DBValue(dbId, dbType, queries, dbFile, serverName, port, user, pass); // New DBVal with all the retrieved data.
					}
					if (wbName != null) { // If there is a report name for this record, there's a report so let's handle it.
						ReportKey key = queries.keySet().stream().filter(k -> k.getReportName().equals(wbName)).findFirst().orElse(null); // Get report.
						List<QueryHolder> queryList = queries.get(key); // Get list of queries for report.
						
						if (queryList == null) queryList = new ArrayList<>(); // Instantiate new list of queries if doesn't exist yet.
						
						if (queryId > 0 && wsName != null && query != null) { // If this record has the data required for a query, let's keep it.
							Map<String, FieldConfiguration> fileConfigs = fieldConfigsJson != null ? JSONTools.FromJSON.fieldConfigs(new JSONObject(fieldConfigsJson)) : null;
							queryList.add(new QueryHolder(queryId, wsName, query, fileConfigs)); // Add query data to list of queries.
						}
						DoubleBandFormatHolder dbFormat = DoubleBandFormatHolder.fromJSON(new JSONObject(dbFormatJson));
						StyleHolder titleStyle = StyleHolder.fromJSON(new JSONObject(titleStyleJson));
						StyleHolder subTotalStyle = StyleHolder.fromJSON(new JSONObject(subTotalJson));
						
						queries.put(new ReportKey(reportId, wbName, dbFormat, titleStyle, subTotalStyle), queryList); // Add report data to report/queries map.
					}
					dbMap.put(dbName, dbVal);
				}
				System.out.println("dbMap2: " + dbMap);
				connMap.put(new ConnectionKey(id, host), dbMap);
			}
			
			for (Entry<ConnectionKey, Map<String, DBValue>> connEntry : connMap.entrySet()) {
				ConnectionKey key = connEntry.getKey();
				Map<String, DBValue> dbVals = connEntry.getValue();
				ConnectionConfig cc = constructConnectionConfig(key.getId(), key.getHostname(), dbVals);
				connConfigCache.put(key.getHostname(), cc);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Instantiate connection config from data collected from the local SQLite database.
	 * @param id - Id of connection config
	 * @param host - Hostname
	 * @param dbMap - Databases, Reports, Queries
	 * @return - A fully constructed connection config.
	 */
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
				Report report = new Report(repId, db, wsName, repKey.getDbFormat(), repKey.getTitleStyle(), repKey.getSubTotalStyle());
				for (QueryHolder qh : queries) {
					report.getQueries().add(new Query(qh.getId(), report, qh.getWorksheetName(), qh.getQuery(), qh.getFieldConfigs()));
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
		return Collections.unmodifiableCollection(connConfigCache.values()); //unmodifiable collection to maintain encapsulation.
	}
	
	/**
	 * Gets database connection of database item.
	 * 
	 * Looks to see if an existing database object is cached; if not, create one and store it for later retrieval.
	 * 
	 * @param db
	 * @return
	 */
	public Database getDatabaseConnection(DB db) {
		ConnectionConfig cc = db.getConnConfig();
		
		String host = cc.getHost();
		String dbName = db.getDatabaseName();
		Database result = this.connDatabaseCache.get(host, dbName); // Fetch database; this returns null if it doesn't exist.
		
		if (result == null) { // If doesn't exist
			switch (db.getType()) { // Switch statement to handle each unique case.
				case SQLITE:
					if (!cc.isLocalHost()) {
						System.out.println("Tried to remotely access a SQLite database.");
						return null;
					}
					result = new SQLiteDatabase(db.getSqliteDBFile()); // Instantiate SQLiteDatabase with DB File.
					break;
				case SQL_SERVER:
					result = new SQLServerDatabase(db.getServerName(), cc.getHost(), Integer.toString(db.getPort()), db.getDatabaseName(), db.getUser(), db.getPass()); // Instantiate SQLServerDatabase with details.
					break;
				case ORACLE:
					result = new OracleDatabase(cc.getHost(), Integer.toString(db.getPort()), db.getDatabaseName(), db.getUser(), db.getPass()); // Instantiate OracleDatabase with details.
					break;
				default:
					break;
			}
			this.connDatabaseCache.put(host, dbName, result); // Put newly created database into cache for later retrieval.
		}
		
		return result;
	}
	
	/**
	 * Delete a connection config from database.
	 * @param cc - connection config to delete.
	 * @return Returns true if successfully deleted connection config, otherwise false.
	 */
	public boolean deleteConnectionConfig(ConnectionConfig cc) {
		if (cc.getId() == -1) return false;
		
		Connection conn = db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_CONNECTION); // Prepare statement with delete statement.
			ps.setInt(1, cc.getId());
			ps.executeUpdate(); // Execute
			
			cc.getDbs().stream().forEach(db -> deleteDB(db)); // Invoke delete on all databases contained within this connection config.
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Delete a database item from database.
	 * @param db - database item to delete.
	 * @return Returns true if successfully deleted database item, otherwise false.
	 */
	public boolean deleteDB(DB db) {
		if (db.getId() == -1) return false;
		
		Connection conn = this.db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_DB); // Prepare statement with delete statement.
			ps.setInt(1, db.getId());
			ps.executeUpdate(); // Execute
			
			db.getReports().stream().forEach(report -> deleteReport(report)); // Invoke delete on all reports contained within this connection config.
			db.getConnConfig().getDbs().remove(db); // Ensure that the connection config doesn't think this database exists anymore.
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Delete a report from database.
	 * @param report - report to delete.
	 * @return Returns true if successfully deleted report, otherwise false.
	 */
	public boolean deleteReport(Report report) {
		if (report.getId() == -1) return false;
		
		Connection conn = this.db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_SSREPORT); // Prepare statement with delete statement.
			ps.setInt(1, report.getId());
			ps.executeUpdate(); // Execute
			
			report.getQueries().stream().forEach(query -> deleteQuery(query)); // Invoke delete on all queries contained within this connection config.
			report.getDb().getReports().remove(report); // Ensure that the database doesn't think this report exists anymore.
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Delete a query from database.
	 * @param query - query to delete.
	 * @return Returns true if successfully deleted query, otherwise false.
	 */
	public boolean deleteQuery(Query query) {
		if (query.getId() == -1) return false;
		
		Connection conn = this.db.getConnection();
		try {
			PreparedStatement ps = conn.prepareStatement(DELETE_QUERY); // Prepare statement with delete statement.
			ps.setInt(1, query.getId());
			ps.executeUpdate(); // Execute
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Closes connections to any databases.
	 */
	public void close() {
		try {
			for (Database dbs : connDatabaseCache.values()) { // Close down cached database connections.
				dbs.close();
			}
			db.getConnection().close(); // Close down connection to local SQLite database.
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static class ConnectionKey {
		
		@Getter private int id;
		@Getter private String hostname;
		
		public ConnectionKey(int id, String hostname) {
			this.id = id;
			this.hostname = hostname;
		}
		
		/**
		 * Override hashcode and equals to enable desired behaviour when inserted into hash-based collections.
		 * 
		 * Similarity/equality of this object is dependent on the value of the id and reportName of the report. 
		 */
		
		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ConnectionKey) && !(obj instanceof Integer)) {
				return false;
			}
			
			int id = (obj instanceof ConnectionKey) ? ((ConnectionKey) obj).getId() : (Integer) obj;
			return this.id == id;
		}
	}
	
	/**
	 * POJO used during the loading of reports from the database. Data is temporarily stored in this object for later construction of connection configs.
	 */
	private static class ReportKey {
		
		@Getter private int id;
		@Getter private String reportName;
		@Getter private DoubleBandFormatHolder dbFormat;
		@Getter private StyleHolder titleStyle;
		@Getter private StyleHolder subTotalStyle;
		
		public ReportKey(int id, String reportName, DoubleBandFormatHolder dbFormat, StyleHolder titleStyle, StyleHolder subTotalStyle) {
			this.id = id;
			this.reportName = reportName;
			this.dbFormat = dbFormat;
			this.titleStyle = titleStyle;
			this.subTotalStyle = subTotalStyle;
		}
		
		/**
		 * Override hashcode and equals to enable desired behaviour when inserted into hash-based collections.
		 * 
		 * Similarity/equality of this object is dependent on the value of the id and reportName of the report. 
		 */
		
		@Override
		public int hashCode() {
			return Objects.hash(id, reportName);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ReportKey)) {
				return false;
			}
			
			ReportKey rk = (ReportKey) obj;
			return rk.id == id && rk.reportName.equals(reportName);
		}
	}
	
	/**
	 * POJO used during the loading of database items from the database. Data is temporarily stored in this object for later construction of connection configs.
	 */
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
	
	/**
	 * POJO used during the loading of queries from the database. Data is temporarily stored in this object for later construction of connection configs.
	 */
	private static class QueryHolder {
		
		@Getter private int id;
		@Getter private String worksheetName;
		@Getter private String query;
		@Getter private Map<String, FieldConfiguration> fieldConfigs;
		
		public QueryHolder(int id, String worksheetName, String query, Map<String, FieldConfiguration> fieldConfigs) {
			this.id = id;
			this.worksheetName = worksheetName;
			this.query = query;
			this.fieldConfigs = fieldConfigs;
		}
	}
	
	/**
	 * Retrieves the fields/columns specified in a query. The part within square brackets is the referred part: "SELECT [field1, field2, field3] FROM table1;".
	 * 
	 * @param masterUI - reference to main application.
	 * @param query - query to retrieve the fields of.
	 * @return Returns an array of DatabaseColumn; each representing a field/column.
	 */
	public static DatabaseColumn[] getTableFields(DBSCGraphicalUserInterface masterUI, Query query) {
		String queryContent = query.getQuery();
		try {
			net.sf.jsqlparser.statement.Statement stmt = CCJSqlParserUtil.parse(queryContent); // Parse the contents of the query.
			if (!(stmt instanceof Select)) { // Query must be a select statement.
				Validation.alert("Must be a select statement.");
				return null;
			}
			
			StringBuilder selectFields = new StringBuilder();
			
			//Iterate over fields in the select part of the select query, and build them into a string; separated by a ','.
			Select selectStmt = (Select) stmt;
			PlainSelect selectBody = (PlainSelect) selectStmt.getSelectBody();
			List<SelectItem> selectItems = selectBody.getSelectItems();
			for (int i = 0; i < selectItems.size(); i++) {
				SelectItem selectItem = selectItems.get(i);
				selectFields.append(selectItem.toString());
				if (i < selectItems.size() - 1) {
					selectFields.append(", ");
				}
			}
			
			// Retrieve the parts after 'FROM'; the tables and the joins, and then rebuild them into a string as well.
			StringBuilder tableParts = new StringBuilder();
			FromItem fromItem = selectBody.getFromItem();
			tableParts.append(fromItem);
			List<Join> joins = selectBody.getJoins();
			if (joins != null && !joins.isEmpty()) {
				joins.stream().forEach(j -> tableParts.append(" " + j));
			}
			
			//This new query serves to merely scout the column/field data of the tables; not to query actually any data.
			String scoutQuery = "SELECT " + selectFields.toString() + " FROM "  + tableParts + " WHERE 1<0"; // Concatenate the selectFields and tableParts back into a SELECT query, but with an improper condition.
			
			DatabaseManager dbManager = masterUI.getDatabaseManager();
			Database connDB = dbManager.getDatabaseConnection(query.getReport().getDb());
			Connection conn = connDB.getConnection();
			try {
				ResultSet rs = conn.createStatement().executeQuery(scoutQuery); // Execute
				ResultSetMetaData meta = rs.getMetaData();
				DatabaseColumn[] dbCols = DBTools.getDatabaseColumns(meta); // Retrieve an array of DatabaseColumn from the meta data from the scout query.
				return dbCols;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} catch (JSQLParserException e1) {
			Validation.alert("Failed to parse query.");
		}
		return null;
	}
}
