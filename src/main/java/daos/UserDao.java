package daos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import Validations.EmailValidation;
import Validations.PasswordValidation;
import databaseConnection.databaseConnector;

public class UserDao {
	
	public static void setUserName(String userName, int id) {
		String query = "update users set userName = ? where userId = ?";
		
		try (PreparedStatement preparedStm = databaseConnector.getConnection().prepareStatement(query)) {
			preparedStm.setString(1, userName);
			preparedStm.setInt(2, id);
			
			preparedStm.executeUpdate();
		}
		
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static String getUserNameById(int id) throws SQLException {
		String sql = "SELECT userName FROM users WHERE userId = ?";
		try (PreparedStatement statement = databaseConnector.getConnection().prepareStatement(sql)) {
			statement.setInt(1, id);
			try (ResultSet result = statement.executeQuery()) {
				return result.next() ? result.getString("userName") : "";
			}
		}
	}
	
	public static void setEmail(String email, int id) {
		String query = "update users set email = ? where userId = ?";
		
		if (!EmailValidation.isValid(email)) {
			System.out.println("იმეილი არ არსებობს, ან არასწორი ფორმატია გამოყენებული!");
		}
		
		try (PreparedStatement preparedStm = databaseConnector.getConnection().prepareStatement(query)) {
			preparedStm.setString(1, email);
			preparedStm.setInt(2, id);
			
			preparedStm.executeUpdate();
		}
		
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setPassword(String password, int id) {
		
		Transaction transaction = null;
		Configuration configuration = new Configuration();
		
		try (
				SessionFactory sessionFactory = configuration.configure().buildSessionFactory();
				Session session = sessionFactory.openSession();
				
			) 
		{
			transaction = session.beginTransaction();
			
			String hqlQuery = "update users set passwordHash = :passwordHash where userId = :userId";
			
			if (PasswordValidation.isValidPassword(password)) {
				session.createMutationQuery(hqlQuery).setParameter("passwordHash", password).setParameter("userId", id).executeUpdate();
				transaction.commit();
				
			}
			
			
		}
		
		catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
	}
	
	public static void insertInto(String userName, String firstName, String lastName, String passwordHash, String email) {
		String sqlQuery = "insert into users(userName, firstName, lastName, passwordHash, email) values (?, ?, ?, ?, ?)";
		
		try (PreparedStatement preparedStm = databaseConnector.getConnection().prepareStatement(sqlQuery)) {
			preparedStm.setString(1, userName);
			preparedStm.setString(2, firstName);
			preparedStm.setString(3, lastName);
			preparedStm.setString(4, passwordHash);
			preparedStm.setString(5, email);
			
			preparedStm.executeUpdate();
		}
		
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean userWithEmailExists(String email) throws SQLException {
		String sqlQuery = "select 1 from users where email = ? limit 1";
		
		try (PreparedStatement preparedStm = databaseConnector.getConnection().prepareStatement(sqlQuery)) {
			preparedStm.setString(1, email);

	        try (ResultSet rs = preparedStm.executeQuery()) {
	            return rs.next();
	        }

		} 
		
		catch (SQLException e) {
			e.printStackTrace();
		
		}
		return false;
		
	}
	
	public static boolean userWithUsernameExists(String userName) {

	    String sqlQuery = "SELECT 1 FROM users WHERE TRIM(userName) = TRIM(?) LIMIT 1";

	    try (PreparedStatement preparedStm =
	            databaseConnector.getConnection().prepareStatement(sqlQuery)) {

	        preparedStm.setString(1, userName.trim());

	        try (ResultSet rs = preparedStm.executeQuery()) {
	            boolean exists = rs.next();

	            System.out.println("Username found in database: " + exists);

	            return exists;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	
	public static int authenticate(String email, String password) throws SQLException {
        String sql = "SELECT userId FROM users WHERE email = ? AND passwordHash = ? LIMIT 1";
        try (PreparedStatement statement = databaseConnector.getConnection().prepareStatement(sql)) {
            statement.setString(1, email.trim());
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt("userId") : -1;
            }
        }
    }
}
