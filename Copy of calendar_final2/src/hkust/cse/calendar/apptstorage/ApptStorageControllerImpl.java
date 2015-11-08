package hkust.cse.calendar.apptstorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import hkust.cse.calendar.gui.TemperatureDialog;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

/* This class is for managing the Appt Storage according to different actions */
public class ApptStorageControllerImpl {

	/* Remove the Appt from the storage */
	public final static int REMOVE = 1;

	/* Modify the Appt the storage */
	public final static int MODIFY = 2;

	/* Add a new Appt into the storage */
	public final static int NEW = 3;
	
	/*
	 * Add additional flags which you feel necessary
	 */
	
	/* The Appt storage */
	private ApptStorage mApptStorage;

	/* Create a new object of ApptStorageControllerImpl from an existing storage of Appt */
	public ApptStorageControllerImpl(ApptStorage storage) {
		mApptStorage = storage;
		TemperatureDialog g=new TemperatureDialog(this);

		g.dispose();
//		if (LoadUsersApptFromXml(storage.getDefaultUser())!=null)
//
//			mApptStorage= LoadUsersApptFromXml(mApptStorage.getDefaultUser());
	}

	///test
		public Appt RetrieveById(int id){
			return mApptStorage.RetrieveApptById(id);
		}
		public int getid(){
			return mApptStorage.getId();
		}
	/* Retrieve the Appt's in the storage for a specific user within the specific time span */
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {
		return mApptStorage.RetrieveAppts(entity, time);
	}

	// overload method to retrieve appointment with the given joint appointment id
	public Appt RetrieveAppts(int joinApptID) {
		return mApptStorage.RetrieveJoinAppts(joinApptID);
	}
	
	public Appt[] RetrieveAllAppts(){ //By Hin, method to retrieve all appointments
		return mApptStorage.RetrieveAllAppts();
	}
	
	/* Manage the Appt in the storage
	 * parameters: the Appt involved, the action to take on the Appt */
	public void ManageAppt(Appt appt, int action) {

		if (action == NEW) {				// Save the Appt into the storage if it is new and non-null
			if (appt == null)
				return;
			mApptStorage.SaveAppt(appt);
		} else if (action == MODIFY) {		// Update the Appt in the storage if it is modified and non-null
			if (appt == null)
				return;
			mApptStorage.UpdateAppt(appt);
		} else if (action == REMOVE) {		// Remove the Appt from the storage if it should be removed
			mApptStorage.RemoveAppt(appt);
		} 
	}

	/* Get the defaultUser of mApptStorage */
	public User getDefaultUser() {
		return mApptStorage.getDefaultUser();
		
	}

	// method used to load appointment from xml record into hash map
	public void LoadApptFromXml(){
		mApptStorage.LoadApptFromXml();
	}
	
	public Location[] getLocationList(){ //ken, return the location list
		return mApptStorage.getLocationList();
	}
	
	public void setLocationList(Location[] locations){ // set the location list
		mApptStorage.setLocationList(locations);
	}
	
	public void updateLocation(){
		mApptStorage.updateLocation();
	}
	
	public void updateAppt(){
		mApptStorage.updateLocation();
	}
	
	public void save(){ //perform a save action
		mApptStorage.saveAction();
	}
	
	public Appt[] RetrieveWaitingList(){
		return mApptStorage.RetrieveApptInWait(mApptStorage.getDefaultUser());
	}
	
	public void refreshState(){
		mApptStorage.self_check_of_appt();
	}
	
	public Appt[] RetrieveApptsbyLocation(Location _location){ //return all Appts with a particular location
		return mApptStorage.RetrieveAppts(_location);
	}
	
	public Appt[] RetrieveApptsbyLocationTime(TimeSpan time, Location _location){ //return all Appts with a particular location at a particular time
			return mApptStorage.RetrieveApptsAtTimeLocation(time, _location);
	}
	
	public LinkedList<String> RemoveApptAndNotifyTheInitializer(User user){ //all appts related to "user" is removed, all appts having user in waiting list, reject list and attend list have the name removed
		return mApptStorage.RemoveApptAndNotifyInitializer(user); //return null if no one has to be informed, otherwise those who have to be informed are returned in the LinkedList
	}
	
	public LinkedList<String> RemoveLocationAndNotifyTheInitializer(Location _location){ //all appts related to "user" is removed, all appts having user in waiting list, reject list and attend list have the name removed
		return mApptStorage.RemoveApptAndNotifyInitializer(_location); //return null if no one has to be informed, otherwise those who have to be informed are returned in the LinkedList
	}
	
	public Appt[] RetrieveApptForChecking(TimeSpan timespan, User user){ //retrieve all appts related to "user" except the user is in reject list
		return mApptStorage.RetrieveApptsForChecking(timespan, user);
	}
	
	public Appt[] RetrieveFrequencyAppts(User user, TimeSpan d, int frequency){
		return mApptStorage.RetrieveFrequencyAppts(user, d, frequency);
		
	}
	
	public Appt[] RetrieveAllPublicAppt(TimeSpan d){
		return mApptStorage.RetrieveAllPublicAppt(d);
	}
	

	public Map<Integer,Double> getTemperature(){
		return mApptStorage.getTemperature();
	}
	
	public Map<Integer,Double> getHumidity(){
		return mApptStorage.getHumidity();
	}
	
	public Map<Integer,String> getSummary(){
		return mApptStorage.getSummary();
	}
	
	

	public String getNotifierString(User user){
		
		String returnString = "";
		Location[] locationList = mApptStorage.getLocationList();
		for( Location target: locationList){
			if( target.getMyState() == false){
				LinkedList<String> tempList = target.getMyNotifier();
				if( tempList.isEmpty()){
					Location[] temp = removeTargetLocation( target);
					if( temp != null)
						setLocationList( temp);
				}
				else if( tempList.contains( user.getUsername())){
					returnString += " - " + target.getName() + "\n";
					tempList.remove(user.getUsername());
					if( tempList.isEmpty()){
						Location[] temp = removeTargetLocation( target);
						if( temp != null)
							setLocationList( temp);
					}
					else{
						setLocationList(locationList);
					}
				}
			}
		}		
		return returnString;		
	}
	
	public Location[] removeTargetLocation(Location target){
		if( target == null)
			return null;
		
		Location[] locationList = mApptStorage.getLocationList();
		Location[] newLocationList = new Location[ locationList.length - 1];
		int i = 0;
		for( Location loc: locationList){
			if( !loc.getName().equals( target.getName())){
				newLocationList[i] = loc;
				i++;
			}
		}
		return newLocationList;
	}
	
	public LinkedList<String> getUserpending(){
		return mApptStorage.getUserpending();
	}
	
	public void refreshList(){
		mApptStorage.refreshList();
	}
//	public boolean occupyLocationSuccess(String _location){ //useless, to be deleted
//		return mApptStorage.occupyLocation(_location);
//	}
	
//	public ApptStorage LoadUsersApptFromXml(User user){
//		XStream xstream = new XStream();
//		ApptStorage loadFromDisk = null;
//		try {
//			
//			FileInputStream fis = new FileInputStream("Appts");
//			loadFromDisk =  new ApptStorageNullImpl(user);
//			xstream.fromXML(fis, loadFromDisk);
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
//		return loadFromDisk;
//	}
	
//	public void saveStoragetoXml(){
//		mApptStorage
//		
//	}
	
	
}
