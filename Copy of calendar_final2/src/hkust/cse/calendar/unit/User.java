package hkust.cse.calendar.unit;

import hkust.cse.calendar.UserRight.NormalUserRight;
import hkust.cse.calendar.UserRight.UserRight;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedList;

public class User implements Serializable {

	public static final int ADMIN = 1;
	public static final int NEW = 2;
	public static final int OLD = 3;
	public static final int TO_BE_DELETE = 4;
	
	private String mUsername;
	private String mPassword;				// User password
	private String mFirstname;
	private String mLastname;
	private String mEmail;
	private Timestamp mBirthday;
	private int mState;
	private LinkedList<String> mNotifier; 
	private UserRight myDeclaredRight;

	// Constructor
	public User(String username, int state){
		mUsername = username;
		mPassword = null;
		mFirstname = null;
		mLastname = null;
		mEmail = null;
		mBirthday = null;
		mState = state;
		mNotifier = null;
		myDeclaredRight = null;
	}

	// Another getter of the user id
	public String toString() {
		return mUsername;
	}

	// Getter of the user password
	public String getPassword() {
		return mPassword;
	}
	
	public String getEmail(){
		return mEmail;
	}
	
	public String getName(){
		return mFirstname + ", " + mLastname;
	}
	
	public String getFirstname(){
		return mFirstname;
	}
	
	public String getLastname(){
		return mLastname;
	}
	
	public Timestamp getBirthday(){
		return mBirthday;
	}
	
	public String getUsername(){
		return mUsername;
	}
	
	public String getBirthdayString(){
		String Year;
		String Month;
		String Date;
		int month;
		int date;
		Year = String.valueOf(mBirthday.getYear() + 1900);
		month = mBirthday.getMonth() + 1;
		if( month + 1 < 10)
			Month = "0" + String.valueOf(month);
		else
			Month = String.valueOf(month);
		date = mBirthday.getDate();
		if( date < 10)
			Date = "0" + String.valueOf(date);
		else
			Date = String.valueOf(date);
		return Year+"-"+Month+"-"+Date;
	}
	
	public int getMyState(){
		return mState;
	}
	
	public LinkedList<String> getMyNotifier(){
		return mNotifier;
	}
	
	// Setter of the user password
	public void setPassword(String pass) {
		mPassword = pass;
	}
	
	public void setEmail( String email){
		mEmail = email;
	}
	
	public void setName( String firstname, String lastname){
		mFirstname = firstname;
		mLastname = lastname;
	}
	
	public void setBirthday( Timestamp birthday){
		mBirthday = birthday;
	}
	
	public void setMyState( int state){
		mState = state;
	}
	
	public void setMyNotifier( LinkedList<String> notifier){
		mNotifier = notifier;
	}
	
	public void setUserRight(UserRight userRight){
		myDeclaredRight = userRight;
	}
	
	public void ShowUserRight(){
		myDeclaredRight.getUserRight();
	}
	
}
