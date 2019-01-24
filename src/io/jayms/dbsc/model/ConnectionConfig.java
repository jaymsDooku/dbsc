package io.jayms.dbsc.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ConnectionConfig {

	@Getter @Setter private String host;
	@Getter @Setter private int port;
	@Getter @Setter private String user;
	@Getter @Setter private String pass;
	@Getter @Setter private List<DB> dbs;
	
	public ConnectionConfig(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.dbs = new ArrayList<>();
	}
	
	public boolean isLocalHost() {
		return host.equalsIgnoreCase("127.0.0.1") || host.equalsIgnoreCase("localhost");
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
