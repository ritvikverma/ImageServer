import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;//Imports for date input
import java.util.Date;//Same as above

import javax.xml.bind.DatatypeConverter;
/**
 * @author ritvikverma 3035453610
 * This is the User class the defines the blueprint of the User and initialises all variables when a new user is made
 */
public class User implements Serializable{
	private String username; //Stores the username of the user
	private String hashedPassword;//Stores the hashedpassword of the user using the given protocol
	/**
	 *  Initialise all values to default or default null initially
	 */
	public User() {
		username = "";
		hashedPassword = "";
	}
	
	/**
	 * @param username the username to be registered
	 * @param hashedPassword the hashed password to be registered
	 */
	public User(String username, String hashedPassword)
	{
		this.username=username;
		this.hashedPassword=hashedPassword;

	}
	public User(String username, String hashedPassword, boolean hash)
	{
		this.username=username;
		if(hash) {
			try {
				this.hashedPassword=hashPassword(hashedPassword);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		else {
			this.hashedPassword=hashedPassword;
		}
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the hashedPassword
	 */
	public String getHashedPassword() {
		return hashedPassword;
	}
	/**
	 * @param hashedPassword the hashedPassword to set
	 */
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	
	/* 
	 * @return string for hashed password
	 * @param the password for hashing
	 * This is the implementation of the interface that we have created in Hash.java
	 * It hashes the passed password
	 */
	public String hashPassword(String password) throws NoSuchAlgorithmException 
	{
		MessageDigest md = MessageDigest.getInstance("MD5");//We will be using the MD5 hashing algorithm
		md.update(password.getBytes());
		return DatatypeConverter.printHexBinary(md.digest()).toString().toLowerCase();//This will return the String
	}
}
