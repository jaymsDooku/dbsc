package io.jayms.dbsc.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ConnectionConfig {

	@Getter @Setter private String host;
	@Getter @Setter private int port;
	@Getter @Setter private String user;
	@Getter @Setter private String pass;
	@Getter @Setter private List<DB> dbs;
	
	public ConnectionConfig(String host, int port, String user, String pass, List<DB> dbs) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.dbs = dbs;
	}
	
	@Override
	public String toString() {
		String s = "{" +
				"Hostname = " + getHost() + "|" +
				"Port = " + getPort() + "|" +
				"User = " + getUser() + "|" +
				"Pass = " + getPass() + "|" +
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
}
