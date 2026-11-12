package entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "users")
public class UserEntity {
	@Id
	@Column (name = "id")
	private int id;
	
	@Column (name = "userName")
	private String userName;
	
	@Column (name = "firstName")
	private String firstName;
	
	@Column (name = "lastName")
	private String lastName;
	
	@Column (name = "passwordHash")
	private String passwordHash;
	
	@Column (name = "email")
	private String email;
	
	public int getId() {
		return this.id;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public String getPasswordHash() {
		return this.passwordHash;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
