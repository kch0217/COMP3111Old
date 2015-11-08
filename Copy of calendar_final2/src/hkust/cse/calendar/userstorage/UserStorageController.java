package hkust.cse.calendar.userstorage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Map;

import hkust.cse.calendar.unit.User;

//	Singleton Class to prevent multiple Controller 
// By Kelvin
public class UserStorageController {
	private static UserStorageController controllerIns = new UserStorageController();
	private UserStorage  mUserStorage;
	
	/* Remove the user from the storage */
	public final static int REMOVE = 1;

	/* Modify the user the storage */
	public final static int MODIFY = 2;

	/* Add a new user into the storage */
	public final static int NEW = 3;
	
	private UserStorageController(){
		mUserStorage = new UserStorageNullImpl();
	}
	
	public static UserStorageController getInstance(){
		return controllerIns;
	}
	
	public void ManageUser( User user, int action) {
		if( user == null)
			return;
		if( action == NEW)
			mUserStorage.SaveUser(user);
		else if ( action == MODIFY)
			mUserStorage.UpdateUser(user);
		else if( action == REMOVE)
			mUserStorage.RemoveUser(user);
	}

	public User RetrieveUserByName(String username){
		return mUserStorage.RetrieveUserByName( username);
	}

	public User[] RetrieveAllUsers() {
		return mUserStorage.RetrieveAllUsers();
	}
	
	public User isCorrectPassword(String username, char[] pass) throws Exception{
		
		char[] encryptedPass = encryptPassword( String.valueOf(pass)).toCharArray();
		User loginUser = mUserStorage.RetrieveUserByName( username);
		if( loginUser == null)
			throw new Exception("Incorrect Username or Password !");
		else if( loginUser.getPassword().length() != encryptedPass.length)
			throw new Exception("Incorrect Username or Password !");
		else{
			char[] realPass = loginUser.getPassword().toCharArray();
			for( int i = 0; i < encryptedPass.length; i++){
				if( realPass[i] != encryptedPass[i])
					throw new Exception("Incorrect Username or Password !");
			}
		}
		return loginUser;		
	}
	
	public String encryptPassword( String pass){
		
		MessageDigest md = null;
		byte[] buffer = pass.getBytes();
				
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		md.update(buffer);
		byte[] passByte = md.digest();
		
		StringBuilder sb = new StringBuilder();
		for( byte b: passByte){
			sb.append(String.format("%02x", b));
		}
				
		return sb.toString();
	}
		
	public int getNumOfUser(){
		return mUserStorage.numOfUser;
	}
	
	public String getNotifierString(User user){
		
		String returnString = "";
		User[] userList = mUserStorage.RetrieveAllUsers();
		for( User target: userList){
			if( target.getMyState() == User.TO_BE_DELETE){
				LinkedList<String> tempList = target.getMyNotifier();
				if( tempList.isEmpty()){
					ManageUser( target, UserStorageController.REMOVE);
				}
				else if( tempList.contains( user.getUsername())){
					returnString += " - " + target.getUsername() + "\n";
					tempList.remove(user.getUsername());
					if( tempList.isEmpty()){
						ManageUser( target, UserStorageController.REMOVE);
					}
					else{
						ManageUser( target, UserStorageController.MODIFY);
					}
				}
			}
		}		
		return returnString;		
	}
}
