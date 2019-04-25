package io.jayms.dbsc.model;

import java.util.ArrayList;
import java.util.List;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.util.GeneralUtils;
import lombok.Getter;
import lombok.Setter;

public class ConnectionConfig {

	/**
	 * Id of connection config stored in database.
	 */
	@Getter @Setter private int id;
	/**
	 * Hostname.
	 */
	@Getter @Setter private String host;
	/**
	 * List of Databases contained in the connection config.
	 */
	@Getter @Setter private List<DB> dbs;
	
	/**
	 * Reference to main application.
	 */
	@Getter private final DBSCGraphicalUserInterface masterUI;
	
	/**
	 * Instantiates a connection config. Intended for new creation of connection configs.
	 * @param masterUI
	 * @param host
	 */
	public ConnectionConfig(DBSCGraphicalUserInterface masterUI, String host) {
		this(masterUI, -1, host);
	}
	
	/**
	 * Instantiates a connection config. Intended for both internal use and for loading connection configs.
	 * @param masterUI - reference to main application
	 * @param id
	 * @param host
	 */
	public ConnectionConfig(DBSCGraphicalUserInterface masterUI, int id, String host) {
		this.masterUI = masterUI;
		this.id = id;
		this.host = host;
		this.dbs = new ArrayList<>();
	}
	
	/**
	 * Checks if the hostname can be considered a localhost.
	 * @return Returns true if hostname is localhost, otherwise false.
	 */
	public boolean isLocalHost() {
		return host.equalsIgnoreCase("127.0.0.1") || host.equalsIgnoreCase("localhost");
	}
	
	/**
	 * Checks if database with name is already contained within connection config. 
	 * @param dbName - name to check.
	 * @return Returns true if database exists with same name, otherwise false.
	 */
	public boolean hasDB(String dbName) {
		return dbs.stream().filter(db -> db.getDatabaseName().equals(dbName)).findFirst().orElse(null) != null;
	}
	
	@Override
	public String toString() { // Mainly debugging purposes
		String s = "{" +
				"Id = " + getId() + "|" +
				"Hostname = " + getHost() + "|" +
				"DBS = [";
		for (int i = 0; i < dbs.size(); i++) {
			DB db = dbs.get(i);
			s += db.toString();
			if (i < dbs.size() - 1) {
				s += ", ";
			}
		}
		s += "]}";
		return s;
	}
	
	/**
	 * Checks if contact is possible to be made with given connection config.
	 * @param cc
	 * @return - Returns true if contact is made with the hostname of connection config, otherwise false.
	 */
	public static boolean madeContactWith(ConnectionConfig cc) {
		if (cc.isLocalHost()) return true; // Don't bother trying if we already know it's reachable.
		return GeneralUtils.isReachable(cc.getHost());
	}
	
	/**
	 * Couples a connection config and result reason as a way to return more than one value in a method call. 
	 * @see DatabaseManager
	 */
	public static class CreationResult {
		
		public enum Result {
			SUCCESS, ALREADY_EXIST, CANT_CONTACT;
		}
		
		@Getter private Result result;
		@Getter private ConnectionConfig connectionConfig;
		
		public CreationResult(Result result, ConnectionConfig connectionConfig) {
			this.result = result;
			this.connectionConfig = connectionConfig;
		}
		
	}
}
