package io.jayms.dbsc.model;

import java.util.ArrayList;
import java.util.List;

import io.jayms.dbsc.DBSCGraphicalUserInterface;
import io.jayms.dbsc.util.GeneralUtils;
import lombok.Getter;
import lombok.Setter;

public class ConnectionConfig {

	@Getter @Setter private int id;
	@Getter @Setter private String host;
	@Getter @Setter private List<DB> dbs;
	
	@Getter private final DBSCGraphicalUserInterface masterUI;
	
	public ConnectionConfig(DBSCGraphicalUserInterface masterUI, String host) {
		this(masterUI, -1, host);
	}
	
	public ConnectionConfig(DBSCGraphicalUserInterface masterUI, int id, String host) {
		this.masterUI = masterUI;
		this.id = id;
		this.host = host;
		this.dbs = new ArrayList<>();
	}
	
	public boolean isLocalHost() {
		return host.equalsIgnoreCase("127.0.0.1") || host.equalsIgnoreCase("localhost");
	}
	
	@Override
	public String toString() {
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
	
	public static boolean madeContactWith(ConnectionConfig cc) {
		if (cc.isLocalHost()) return true;
		return GeneralUtils.ping(cc.getHost(), 7);
	}
	
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
