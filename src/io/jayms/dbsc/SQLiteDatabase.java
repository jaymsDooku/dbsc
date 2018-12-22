package io.jayms.dbsc;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.jayms.xlsx.db.AbstractDatabase;

public class SQLiteDatabase extends AbstractDatabase {

	private File dbFile;
	
	public SQLiteDatabase(File dbFile) {
		super(null, null, null, null, null, null);
		this.dbFile = dbFile;
		try {
			if (!dbFile.exists()) {
				if (dbFile.createNewFile()) {
					System.out.println("Created new SQLite Database: " + dbFile.getName());
				}
			}
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public File file() {
		return dbFile;
	}
	
	@Override
	protected void initConnection(String serverName, String host, String port, String databaseName, String user,
			String pass) throws SQLException, ClassNotFoundException {
	}
}
