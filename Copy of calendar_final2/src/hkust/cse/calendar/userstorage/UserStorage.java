package hkust.cse.calendar.userstorage;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.util.Map;


// By Kelvin
public abstract class UserStorage {
	
	public Map<String, User> mUsers;
	
	public int numOfUser;
	
	public UserStorage(){		
	}
	
	public abstract void SaveUser(User user);	//abstract method to save an user record
	
	public abstract void UpdateUser(User user);	//abstract method to update an user record

	public abstract void RemoveUser(User user);	//abstract method to remove an user record
	
	public abstract User RetrieveUserByName( String username); 		// Retrieve appointment with the given username
	
	public abstract User[] RetrieveAllUsers();
	
	public abstract void LoadUserFromXml();		//abstract method to load user record from xml reocrd into hash map
	
	public abstract void SaveUserToXml();		//abstract method to save user record in hash map to xml
}
