package hkust.cse.calendar.unit;

import hkust.cse.calendar.userstorage.UserStorageController;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedList;

public class Appt implements Serializable, Cloneable {

	private TimeSpan mTimeSpan;					// Include day, start time and end time of the appointments

	private int weekday;						// the day of week of the appointments, Sunday is 0
	
	private String mTitle;						// The Title of the appointments

	private String mInfo;						// Store the content of the appointments description

	private int mApptID;						// The appointment id
	
	private int joinApptID;						// The join appointment id

	private boolean isjoint;					// The appointment is a joint appointment
	
	private LinkedList<String> attend;			// The Attendant list
	
	private LinkedList<String> reject;			// The reject list
	
	private LinkedList<String> waiting;			// The waiting list
	
	private Location mLocation;					// The Location of the appointments
	
	private boolean ReminderNeed;				// True if a reminder is set by user
	
	private Timestamp mReminder;				// The reminder of the appointment
	
	private boolean ReminderSettle;				// False if a reminder has displayed or reminder is not required
	
	private Timestamp lastReminder;				// store the time when the last reminder was issued
	
	private int mFrequency;
	
	private String currentUser;					//the user of the appointment
	
	private boolean state;						//if it is true, it is put on schedule, if not,  it is on hold
	
	private boolean publicPrivate;				//true = public, false = private
	
	private User[] invited;
	private User[] remained;
	private boolean groupEventcreate;
	private boolean initiator;
	private boolean dialog;
	
	public Appt() {								// A default constructor used to set all the attribute to default values
		mApptID = 0;
		mTimeSpan = null;
		mTitle = "Untitled";
		mInfo = "";
		isjoint = false;
		attend = new LinkedList<String>();
		reject = new LinkedList<String>();
		waiting = new LinkedList<String>();
		joinApptID = -1;
		mLocation = null;
		mReminder = null;
		mFrequency = 0;
		
		ReminderSettle = false;
		lastReminder = null;
		//
		invited=new User[0];
	    
		remained = UserStorageController.getInstance().RetrieveAllUsers();
		User[] temp = new User[remained.length];
		int count = 0;
		for (int i= 0 ; i< remained.length; i++){
			if (remained[i].getMyState() != User.TO_BE_DELETE){
				temp[count] = remained[i];
				count++;
			}
		}
		remained = new User[count];
		for (int i = 0; i<count;i++){
			remained[i] = temp[i];
		}
		
		
	
		groupEventcreate=false;
		initiator=false;
		dialog=false;
	}
	
	public Object clone(){  
	    try{  
	        return super.clone();  
	    }catch(Exception e){ 
	        return null; 
	    }
	}

	public boolean getDialog(){
		return dialog;
	}
	public void setDialog(boolean d){
		dialog=d;
	}
	
	public User[] getInvited(){
		return invited;
	}
	
	public User[] getRemained(){
		return remained;
	}
	
	public void setInvited(User[] invite){
		invited=invite;
	}
	
	public void setRemained(User[] remain){
		remained=remain;
	}
	
	public boolean getGroupEventStatus(){
		return groupEventcreate;
	}
	public void setGroupEventStatus(boolean sta){
		groupEventcreate=sta;
	}
	public boolean getInitiatorStatus(){
		return initiator;
	}
	public void setInitiatorStatus(boolean status){
		initiator=status;
	}

	
	//testing
	// Getter of the mTimeSpan
	public TimeSpan TimeSpan() {
		return mTimeSpan;
	}
	
	// Getter of the appointment title
	public String getTitle() {
		return mTitle;
	}

	// Getter of appointment description
	public String getInfo() {
		return mInfo;
	}

	// Getter of the appointment id
	public int getID() {
		return mApptID;
	}
	
	// Getter of the join appointment id
	public int getJoinID(){
		return joinApptID;
	}

	public void setJoinID(int joinID){
		this.joinApptID = joinID;
	}
	// Getter of the attend LinkedList<String>
	public LinkedList<String> getAttendList(){
		return attend;
	}
	
	// Getter of the reject LinkedList<String>
	public LinkedList<String> getRejectList(){
		return reject;
	}
	
	// Getter of the waiting LinkedList<String>
	public LinkedList<String> getWaitingList(){
		return waiting;
	}
	
	public LinkedList<String> getAllPeople(){
		LinkedList<String> allList = new LinkedList<String>();
		allList.addAll(attend);
		allList.addAll(reject);
		allList.addAll(waiting);
		return allList;
	}
	
	public LinkedList<String> getPossiblePeople(){ //including initiator
		LinkedList<String> allList = new LinkedList<String>();
		allList.addAll(attend);
		allList.addAll(waiting);
		return allList;
	}
	
	public Location getLocation(){
		return mLocation;
	}
	
	public int getFrequency(){
		return mFrequency;
	}
	
	public Timestamp getReminder(){
		return mReminder;
	}
	
	public void addAttendant(String addID){
		if (attend == null)
			attend = new LinkedList<String>();
		attend.add(addID);
	}
	
	public void addReject(String addID){
		if (reject == null)
			reject = new LinkedList<String>();
		reject.add(addID);
	}
	
	public void addWaiting(String addID){
		if (waiting == null)
			waiting = new LinkedList<String>();
		waiting.add(addID);
	}
	
	public void setWaitingList(LinkedList<String> waitingList){
		waiting = waitingList;
	}
	
	public void setWaitingList(String[] waitingList){
		LinkedList<String> tempLinkedList = new LinkedList<String>();
		if (waitingList !=null){
			for (int a=0; a<waitingList.length; a++){
				tempLinkedList.add(waitingList[a].trim());
			}
		}
		waiting = tempLinkedList;
	}
	
	public void setRejectList(LinkedList<String> rejectLinkedList) {
		reject = rejectLinkedList;
	}
	
	public void setRejectList(String[] rejectList){
		LinkedList<String> tempLinkedList = new LinkedList<String>();
		if (rejectList !=null){
			for (int a=0; a<rejectList.length; a++){
				tempLinkedList.add(rejectList[a].trim());
			}
		}
		reject = tempLinkedList;
	}
	
	public void setAttendList(LinkedList<String> attendLinkedList) {
		attend = attendLinkedList;
	}
	
	public void setAttendList(String[] attendList){
		LinkedList<String> tempLinkedList = new LinkedList<String>();
		if (attendList !=null){
			for (int a=0; a<attendList.length; a++){
				tempLinkedList.add(attendList[a].trim());
			}
		}
		attend = tempLinkedList;
	}
	// Getter of the appointment title
	public String toString() {
		return mTitle;
	}

	// Setter of the appointment title
	public void setTitle(String t) {
		mTitle = t;
	}

	// Setter of the appointment description
	public void setInfo(String in) {
		mInfo = in;
	}

	// Setter of the mTimeSpan
	public void setTimeSpan(TimeSpan d) {
		mTimeSpan = d;
	}

	// Setter if the appointment id
	public void setID(int id) {
		mApptID = id;
	}
	
	// check whether this is a joint appointment
	public boolean isJoint(){
		return isjoint;
	}

	// setter of the isJoint
	public void setJoint(boolean isjoint){
		this.isjoint = isjoint;
	}

	public void setLocation( Location location){
		mLocation = location;
	}
	
	public void setFrequency( int freq){
		mFrequency = freq;
	}
	
	public void setReminder( Timestamp time){
		mReminder = time;
		ReminderSettle = true;
	}
	
	public boolean getReminderStatus(){
		return ReminderSettle;
	}
	public void setReminderStatus(boolean choice){
		ReminderSettle = choice;
	}
	
	public void setWeekday(int setweekday)
	{
		weekday = setweekday;
	}
	
	public int getWeekday(){
		return weekday;
	}
	
	public Timestamp getLastReminder(){
		return lastReminder;
	}
	
	public void setLastReminder(Timestamp timeDuration){
		lastReminder = timeDuration;
	}
	public boolean getReminderNeed(){
		return ReminderNeed;
	}
	
	public void setReminderNeed(boolean decision){
		ReminderNeed = decision;
	}
	
	public void setUser(String myuser){
		currentUser = myuser;
	}
	
	public String getUser(){
		return currentUser;
	}
	
	public void setState(boolean mystate){
		state = mystate;
	}
	
	public boolean getState(){
		return state;
	}
	
	public void setPublicity(boolean set){
		publicPrivate = set;
	}
	
	public boolean getPublicity(){
		return publicPrivate;
	}
}
