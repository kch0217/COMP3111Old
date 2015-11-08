package hkust.cse.calendar.apptstorage;//

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;


public abstract class ApptStorage {

	public Map<Integer, Appt> mAppts;		//a hashmap to save every thing to it, write to memory by the memory based storage implementation	
	//public Map<Integer, Appt> mJointAppts;
	public User defaultUser;	//a user object, now is single user mode without login
	public int mAssignedApptID;	//a global appointment ID for each appointment record

	public ApptStorage() {	//default constructor
	}
	//test
	public abstract Appt RetrieveApptById(int id);
	public abstract int getId();
	//
	public abstract Location[] getLocationList();
	
	public abstract void setLocationList(Location[] locations); 
	
	public abstract void updateLocation();	//test
	
	public abstract void SaveAppt(Appt appt);	//abstract method to save an appointment record

	public abstract Appt[] RetrieveAppts(TimeSpan d);	//abstract method to retrieve an appointment record by a given timespan

	public abstract Appt[] RetrieveAppts(User entity, TimeSpan time);	//overloading abstract method to retrieve an appointment record by a given user object and timespan
	
	public abstract Appt RetrieveJoinAppts(int joinApptID);					// overload method to retrieve appointment with the given joint appointment id
	
	public abstract Appt[] RetrieveAllAppts();
	
	public abstract Appt[] RetrieveAppts(Location location);
	
	public abstract Appt[] RetrieveApptsAtTimeLocation(TimeSpan time, Location location);
	
	public abstract void UpdateAppt(Appt appt);	//abstract method to update an appointment record

	public abstract void RemoveAppt(Appt appt);	//abstract method to remove an appointment record
	
	public abstract void saveAction(); //save
	
	public abstract void self_check_of_appt(); //refresh state
	
	public abstract LinkedList<String> RemoveApptAndNotifyInitializer(User user);
	
	public abstract LinkedList<String> RemoveApptAndNotifyInitializer(Location _location);
	
	public abstract Appt[] RetrieveFrequencyAppts(User user, TimeSpan d, int frequency);
	
	public abstract Appt[] RetrieveApptInWait(User user);
	
	public abstract Appt[] RetrieveApptsForChecking(TimeSpan d, User user);
	
	public abstract Appt[] RetrieveAllPublicAppt(TimeSpan d);
	
	public abstract User getDefaultUser();		//abstract method to return the current user object
	
	public abstract void LoadApptFromXml();		//abstract method to load appointment from xml reocrd into hash map
	
	public abstract Map<Integer,Double> getTemperature();

	
	public abstract Map<Integer,Double> getHumidity();

	public abstract Map<Integer,String> getSummary();

	public abstract LinkedList<String> getUserpending();
	
	public abstract void refreshList();
//	public abstract boolean occupyLocation(String _location);
	/*
	 * Add other methods if necessary
	 */

}
