package io.jayms.dbsc;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.jayms.xlsx.db.AbstractDatabase;

public class SQLiteDatabase extends AbstractDatabase {

	/**
	 * Represents DB file of SQLite DB.
	 */
	private File dbFile;
	
	/**
	 * Instantiates the class and initializes a connection to the SQLite DB if possible.
	 * @param dbFile - represents DB file of SQLite DB
	 */
	public SQLiteDatabase(File dbFile) {
		super(null, null, null, null, null, null);
		this.dbFile = dbFile;
		try {
			if (!dbFile.exists()) {
				if (dbFile.createNewFile()) { // Create new SQLite DB File if one given doesn't exist yet.
					System.out.println("Created new SQLite Database: " + dbFile.getName());
				}
			}
			
			Class.forName("org.sqlite.JDBC"); // Initialize SQLite drivers.
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath()); // Initialize connection to SQLite DB.
		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public File file() {
		return dbFile;
	}
	
	/**
	 * Abstract method left empty because isn't required for a flat-file SQLite database.
	 */
	@Override
	protected void initConnection(String serverName, String host, String port, String databaseName, String user,
			String pass) throws SQLException, ClassNotFoundException {
	}
}
