package io.jayms.dbsc.model;

import java.util.List;

public class ConnectionConfig {

	private String host;
	private int port;
	private String user;
	private String pass;
	private List<DB> dbs;
	
	public ConnectionConfig(String host, int port, String user, String pass, List<DB> dbs) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.dbs = dbs;
	}
	
	public String host() {
		return host;
	}
	
	public void host(String host) {
		this.host = host;
	}
	
	public int port() {
		return port;
	}
	
	public void port(int port) {
		this.port = port;
	}
	
	public String user() {
		return user;
	}
	
	public void user(String user) {
		this.user = user;
	}
	
	public String pass() {
		return pass;
	}
	
	public void pass(String pass) {
		this.pass = pass;
	}
	
	public List<DB> dbs() {
		return dbs;
	}
	
	public void dbs(List<DB> dbs) {
		this.dbs = dbs;
	}
	
	@Override
	public String toString() {
		String s = "{" +
				"Hostname = " + host() + "|" +
				"Port = " + port() + "|" +
				"User = " + user() + "|" +
				"Pass = " + pass() + "|" +
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
