package daos;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import Validations.PasswordValidation;
import databaseConnection.databaseConnector;

public class UserDao {
	
	public static void setUserName(String userName, int id) {
		String query = "update users set userName = ? where userId = ?";
		
		try (PreparedStatement preparedStm = databaseConnector.getConnection().prepareStatement(query)) {
			preparedStm.setString(1, userName);
			preparedStm.setInt(2, id);
		}
		
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setEmail(String email, int id) {
		String query = "update users set email = ? where userId = ?";
		
		if (email == null || !email.contains("@")) {
			System.out.println("იმეილი არ არსებობს, ან არასწორი ფორმატია გამოყენებული!");
		}
		
		try (PreparedStatement preparedStm = databaseConnector.getConnection().prepareStatement(query)) {
			preparedStm.setString(1, email);
			preparedStm.setInt(2, id);
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
}
