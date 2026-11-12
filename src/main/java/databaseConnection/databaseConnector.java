package databaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnector {
	private static final String url = "jdbc:mysql://localhost:3306/astrosphere";
	private static final String root = "root";
	private static final String password = "Irakli2007!";
	
	public static Connection getConnection() {
		
		try(Connection connection = DriverManager.getConnection(url, root, password)) {
			System.out.println("ბაზას წარმატებით დაუკავშირდა!");
			return connection;
		}
		
		catch(SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
