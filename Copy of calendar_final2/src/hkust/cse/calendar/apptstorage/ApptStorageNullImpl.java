package hkust.cse.calendar.apptstorage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import hkust.cse.calendar.gui.TemperatureDialog;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

public class ApptStorageNullImpl extends ApptStorage {

	private transient static ApptStorageNullImpl oneInstance = new ApptStorageNullImpl();
	
	@Override
	public Location[] getLocationList() {
		// TODO Auto-generated method stub
		return _locations;
	}

	@Override
	public void setLocationList(Location[] locations) {
		// TODO Auto-generated method stub
		_locations = locations;
		SaveApptToXml();
	}
	public void updateLocation(){
		SaveApptToXml();
	}

	private static User defaultUser = null;
	
	private final int InitialID = 0;
	
	private int numOfAppt;
	
	private int joinID;
	
	private transient ApptStorage loadFromDisk;
	
	private Location[] _locations;

	public Map<Integer,String> summary;

	public Map<Integer,Double> humidity;

	//
	public Map<Integer,Double> temperature;	//key is the date ,e.g. 20141205
	
	private LinkedList<String> UserpendingToDel;
	private int numOfUser;
	
//	public static Map<Integer,Double> temperature;	//key is the date ,e.g. 20141205
//	public static Map<Integer,Double> humidity;
	
	//test
		@Override
		public Appt RetrieveApptById(int id){
			return mAppts.get(id);
		}
		
		public int getId(){
			return mAssignedApptID;
		}
		/////
	private ApptStorageNullImpl( ) //constructor, initializing all the attributes
	{
		//defaultUser = new User(user.ID(),user.Password());
		temperature = new HashMap<Integer,Double>();
		humidity =new HashMap<Integer,Double>();
		summary =  new HashMap<Integer,String>();
		
		mAssignedApptID = InitialID;
		numOfAppt = 0;
		joinID = 0;
		UserStorageController usercon = UserStorageController.getInstance();
		numOfUser = usercon.getNumOfUser();
		mAppts = new HashMap<Integer, Appt>();
		UserpendingToDel = new LinkedList<String>();
		//mJointAppts = new HashMap<Integer, Appt>();
		_locations=new Location[10];
		_locations[0]=new Location(" ");
		for(int i=1;i<6;i++){
			_locations[i]=new Location("common room"+i);
		}
		_locations[6]=new Location("atrium");
		_locations[7]=new Location("sports hall");
		_locations[8]=new Location("lab");
		_locations[9]=new Location("learning common");
	}
	
	
	public static ApptStorageNullImpl getStorageInstance(User user){
		defaultUser = user;
		return oneInstance;
	}
	
	@Override
	public void SaveAppt(Appt appt) { //by Hin
		// TODO Auto-generated method stub
		
		if (appt!=null){ //put the appt in the hashmap with its ID as the key
			appt.setUser(defaultUser.getUsername());
			appt.setID(mAssignedApptID);
			if (appt.isJoint()) 
			{
				appt.setJoinID(joinID);
				joinID++;
				
				if (appt.getWaitingList().size()==0 && appt.getRejectList().size() ==0){
					appt.setState(true);
				}
				else
					appt.setState(false);
			}
			else{
				appt.setJoinID(-1);
				appt.setState(true);
			}
				
			mAppts.put(mAssignedApptID, appt);
			mAssignedApptID += 1;
			numOfAppt++;
			SaveApptToXml();
		}
		
	}

	@Override
	public Appt[] RetrieveAppts(TimeSpan d) {//by Hin, return all appointments of the current user at a particular timespan, including attend list
		// TODO Auto-generated method stub
		
		//System.out.println("The size of hashmap: "+mAppts.size()); //testing
		Appt[] matchApptList = new Appt[mAppts.size()];
		int listCounter = 0;
		/*System.out.println(d.StartTime().getYear()+" "+ d.StartTime().getMonth()+" "+
						 d.StartTime().getDate()+" " +d.StartTime().getHours()+ " "+ 
						 d.StartTime().getMinutes());*/ //testing
		for (int i = InitialID ;i<(InitialID+ numOfAppt); i++) //Go through every record to check if the Timespan of the appointment is within the specified time
		{
			Appt extractedOne = mAppts.get(i);
			if (!extractedOne.getUser().equals(defaultUser.getUsername())){
				boolean appearInAttend = false;
				if (extractedOne.getAttendList().isEmpty() && extractedOne.getWaitingList().isEmpty()) //handle the case when the user is in the attend list
					continue;
				else
				{
					for (int j = 0; j< extractedOne.getAttendList().size();j++)
						if (extractedOne.getAttendList().get(j).equals(defaultUser.getUsername()))
						{
							appearInAttend = true;
							break;
						}
					for (int j = 0; j< extractedOne.getWaitingList().size();j++)
						if (extractedOne.getWaitingList().get(j).equals(defaultUser.getUsername()))
						{
							appearInAttend = true;
							break;
						}
					if (!appearInAttend)
						continue;
				}
			}
			if (d.Overlap(extractedOne.TimeSpan())){
				matchApptList[listCounter] = extractedOne;
				listCounter++;
			}
			else{ //handling events with frequency
				TimeSpan ApptTime;
				TimeSpan ApptTime2;
				if (d.StartTime().getYear() < extractedOne.TimeSpan().StartTime().getYear()) //when handling frequency, those appointments with latter time span are ignored
					continue;
				else
					if (d.StartTime().getYear() == extractedOne.TimeSpan().StartTime().getYear())
						if (d.StartTime().getMonth() < extractedOne.TimeSpan().StartTime().getMonth())
							continue;
						else 
							if (d.StartTime().getMonth() == extractedOne.TimeSpan().StartTime().getMonth())
								if (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())
									continue;
				
				switch (extractedOne.getFrequency()){
				case 1:	
						if ((d.EndTime().getYear() > d.StartTime().getYear())|| (d.EndTime().getMonth() > d.StartTime().getMonth())|| (d.EndTime().getDate()- d.StartTime().getDate() > 1))
						{
							matchApptList[listCounter] = extractedOne; //if the time span is at least 3 days, the appointment must overlap with the designated time
							listCounter++;
						}
						else
						{
							ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
								 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
								 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
								 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
								 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0)); //create a time span in which a daily appointment happens
																						//on the specified day of the starting time of the input
							ApptTime2 = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));//create a time span in which a daily appointment happens
																					//on the specified day of the ending time of the input
							
							if (d.Overlap(ApptTime)|| d.Overlap(ApptTime2)){ //check if the daily appointment is within the timespan
								matchApptList[listCounter] = extractedOne;
								listCounter++;
							}
						}
						break;
				case 2:
						int specifiedStartWeekday = hkust.cse.calendar.gui.Utility.getweekday(d.StartTime());
						int specifiedEndWeekday = hkust.cse.calendar.gui.Utility.getweekday(d.EndTime());
						if (((specifiedStartWeekday > extractedOne.getWeekday())&& (specifiedEndWeekday > extractedOne.getWeekday()))|| //check x..A..B
							((specifiedStartWeekday < extractedOne.getWeekday())&& (specifiedEndWeekday < extractedOne.getWeekday()))|| //check A..B..x
							((specifiedStartWeekday > extractedOne.getWeekday())&& (specifiedEndWeekday < extractedOne.getWeekday())))  //check B..x..A
							break; //if the weekday is not in range, break
						else
							
							if ( specifiedStartWeekday== extractedOne.getWeekday()){ 
								ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
									 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
									 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
							
								if (d.Overlap(ApptTime)){ //check if the weekly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
							}
						else if (specifiedEndWeekday == extractedOne.getWeekday()){
								ApptTime = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
								if (d.Overlap(ApptTime)){ //check if the weekly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
							}
						else
						{ //the weekday is within the start time and end time
							matchApptList[listCounter] = extractedOne;
							listCounter++;
						}
						
					
						break;
				case 3:
						if (d.StartTime().getDate() == extractedOne.TimeSpan().StartTime().getDate()){
							ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
									 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
									 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
							if (d.Overlap(ApptTime)){ //check if the monthly appointment is within the timespan
								matchApptList[listCounter] = extractedOne;
								listCounter++;
							}
						}
						else 
							if (d.EndTime().getDate() == extractedOne.TimeSpan().EndTime().getDate()){
								ApptTime = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
								
								if (d.Overlap(ApptTime)){ //check if the monthly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;				
								}
							}
							else
								if (((d.EndTime().getDate() > extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()< d.EndTime().getDate()))|| //check A.x.B
										((d.EndTime().getDate() < extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()> d.EndTime().getDate()) )|| //check B.A.x
										((d.EndTime().getDate() > extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() > extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()> d.EndTime().getDate()))){ //check x.B.A
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
						
							
						break;
				}
			}
		}
		Appt[] returnList = new Appt[listCounter]; //copy all the appointments back to an array with appropriate size
		for (int j = 0; j< listCounter; j++)
		{
			returnList[j] = matchApptList[j];
		}
		if (listCounter == 0)
		{
			return null;
		}
		else
			return returnList;
	}
	
	
	public Appt[] RetrieveApptsForChecking(TimeSpan d, User user) {// return all appointments of the current user at a particular timespan, including attend list, waiting list
		// TODO Auto-generated method stub
		
		//System.out.println("The size of hashmap: "+mAppts.size()); //testing
		Appt[] matchApptList = new Appt[mAppts.size()];
		int listCounter = 0;
		/*System.out.println(d.StartTime().getYear()+" "+ d.StartTime().getMonth()+" "+
						 d.StartTime().getDate()+" " +d.StartTime().getHours()+ " "+ 
						 d.StartTime().getMinutes());*/ //testing
		for (int i = InitialID ;i<(InitialID+ numOfAppt); i++) //Go through every record to check if the Timespan of the appointment is within the specified time
		{
			Appt extractedOne = mAppts.get(i);
			if (!extractedOne.getUser().equals(user.getUsername())){
				boolean appearInList = false;
				if (extractedOne.getAttendList().isEmpty()&&extractedOne.getWaitingList().isEmpty()) //handle the case when the user is in the attend list or waiting list
					continue;
				else
				{
					if (!extractedOne.getAttendList().isEmpty()){
						for (int j = 0; j< extractedOne.getAttendList().size();j++)
							if (extractedOne.getAttendList().get(j).equals(user.getUsername()))
							{
								appearInList = true;
								break;
							}
//						if (!appearInAttend)
//							continue;
					}
					if (!extractedOne.getWaitingList().isEmpty()){
						for (int k = 0; k< extractedOne.getWaitingList().size();k++)
							if (extractedOne.getWaitingList().get(k).equals(user.getUsername()))
							{
								appearInList = true;
								break;
							}
					}
					if (!appearInList)
						continue;						
				}
			}
			if (d.Overlap(extractedOne.TimeSpan())){
				matchApptList[listCounter] = extractedOne;
				listCounter++;
			}
			else{ //handling events with frequency
				TimeSpan ApptTime;
				TimeSpan ApptTime2;
				if (d.StartTime().getYear() < extractedOne.TimeSpan().StartTime().getYear()) //when handling frequency, those appointments with latter time span are ignored
					continue;
				else
					if (d.StartTime().getYear() == extractedOne.TimeSpan().StartTime().getYear())
						if (d.StartTime().getMonth() < extractedOne.TimeSpan().StartTime().getMonth())
							continue;
						else 
							if (d.StartTime().getMonth() == extractedOne.TimeSpan().StartTime().getMonth())
								if (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())
									continue;
				
				switch (extractedOne.getFrequency()){
				case 1:	
						if ((d.EndTime().getYear() > d.StartTime().getYear())|| (d.EndTime().getMonth() > d.StartTime().getMonth())|| (d.EndTime().getDate()- d.StartTime().getDate() > 1))
						{
							matchApptList[listCounter] = extractedOne; //if the time span is at least 3 days, the appointment must overlap with the designated time
							listCounter++;
						}
						else
						{
							ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
								 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
								 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
								 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
								 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0)); //create a time span in which a daily appointment happens
																						//on the specified day of the starting time of the input
							ApptTime2 = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));//create a time span in which a daily appointment happens
																					//on the specified day of the ending time of the input
							
							if (d.Overlap(ApptTime)|| d.Overlap(ApptTime2)){ //check if the daily appointment is within the timespan
								matchApptList[listCounter] = extractedOne;
								listCounter++;
							}
						}
						break;
				case 2:
						int specifiedStartWeekday = hkust.cse.calendar.gui.Utility.getweekday(d.StartTime());
						int specifiedEndWeekday = hkust.cse.calendar.gui.Utility.getweekday(d.EndTime());
						if (((specifiedStartWeekday > extractedOne.getWeekday())&& (specifiedEndWeekday > extractedOne.getWeekday()))|| //check x..A..B
							((specifiedStartWeekday < extractedOne.getWeekday())&& (specifiedEndWeekday < extractedOne.getWeekday()))|| //check A..B..x
							((specifiedStartWeekday > extractedOne.getWeekday())&& (specifiedEndWeekday < extractedOne.getWeekday())))  //check B..x..A
							break; //if the weekday is not in range, break
						else
							
							if ( specifiedStartWeekday== extractedOne.getWeekday()){ 
								ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
									 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
									 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
							
								if (d.Overlap(ApptTime)){ //check if the weekly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
							}
						else if (specifiedEndWeekday == extractedOne.getWeekday()){
								ApptTime = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
								if (d.Overlap(ApptTime)){ //check if the weekly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
							}
						else
						{ //the weekday is within the start time and end time
							matchApptList[listCounter] = extractedOne;
							listCounter++;
						}
						
					
						break;
				case 3:
						if (d.StartTime().getDate() == extractedOne.TimeSpan().StartTime().getDate()){
							ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
									 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
									 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
							if (d.Overlap(ApptTime)){ //check if the monthly appointment is within the timespan
								matchApptList[listCounter] = extractedOne;
								listCounter++;
							}
						}
						else 
							if (d.EndTime().getDate() == extractedOne.TimeSpan().EndTime().getDate()){
								ApptTime = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
								
								if (d.Overlap(ApptTime)){ //check if the monthly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;				
								}
							}
							else
								if (((d.EndTime().getDate() > extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()< d.EndTime().getDate()))|| //check A.x.B
										((d.EndTime().getDate() < extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()> d.EndTime().getDate()) )|| //check B.A.x
										((d.EndTime().getDate() > extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() > extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()> d.EndTime().getDate()))){ //check x.B.A
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
						
							
						break;
				}
			}
		}
		Appt[] returnList = new Appt[listCounter]; //copy all the appointments back to an array with appropriate size
		for (int j = 0; j< listCounter; j++)
		{
			returnList[j] = matchApptList[j];
		}
		if (listCounter == 0)
		{
			return null;
		}
		else
			return returnList;
	}
	
	public Appt[] RetrieveFrequencyAppts(User user, TimeSpan e, int frequency){ //Only check all Appts with Timespan in frequency
		Appt[] matchApptList = new Appt[mAppts.size()];
		
		
		int listCounter = 0;
		/*System.out.println(d.StartTime().getYear()+" "+ d.StartTime().getMonth()+" "+
						 d.StartTime().getDate()+" " +d.StartTime().getHours()+ " "+ 
						 d.StartTime().getMinutes());*/ //testing
		for (int i = InitialID ;i<(InitialID+ numOfAppt); i++) //Go through every record to check if the Timespan of the appointment is within the specified time
		{
			Appt extractedOne = mAppts.get(i);
			if (!extractedOne.getUser().equals(user.getUsername())){
				boolean appearInList = false;
				if (extractedOne.getAttendList().isEmpty()&&extractedOne.getWaitingList().isEmpty()) //handle the case when the user is in the attend list or waiting list
					continue;
				else
				{
					if (!extractedOne.getAttendList().isEmpty()){
						for (int j = 0; j< extractedOne.getAttendList().size();j++)
							if (extractedOne.getAttendList().get(j).equals(user.getUsername()))
							{
								appearInList = true;
								break;
							}
//						if (!appearInAttend)
//							continue;
					}
					if (!extractedOne.getWaitingList().isEmpty()){
						for (int k = 0; k< extractedOne.getWaitingList().size();k++)
							if (extractedOne.getWaitingList().get(k).equals(user.getUsername()))
							{
								appearInList = true;
								break;
							}
					}
					if (!appearInList)
						continue;						
				}
			}
			Timestamp inputStart = new Timestamp(e.StartTime().getTime());
			Timestamp inputEnd = new Timestamp(e.EndTime().getTime());
			TimeSpan d = new TimeSpan(inputStart, inputEnd);
			if (frequency == 0){
				if ( extractedOne.getFrequency()==0 && extractedOne.TimeSpan().StartTime().getDate() == d.StartTime().getDate()&&extractedOne.TimeSpan().StartTime().getMonth() == d.StartTime().getMonth()&& extractedOne.TimeSpan().StartTime().getYear() == d.StartTime().getYear()){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
				else if (extractedOne.getFrequency()==1 && d.StartTime().getTime() > extractedOne.TimeSpan().StartTime().getTime()){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
				else if (extractedOne.getFrequency()==2 && d.StartTime().getTime() > extractedOne.TimeSpan().StartTime().getTime() && d.StartTime().getDay()== extractedOne.TimeSpan().StartTime().getDay()){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
				else if (extractedOne.getFrequency() == 3 && d.StartTime().getTime() > extractedOne.TimeSpan().StartTime().getTime() && d.StartTime().getDay()== extractedOne.TimeSpan().StartTime().getDay()){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
			}
			else if (frequency == 1){
				if (d.StartTime().getTime() < extractedOne.TimeSpan().StartTime().getTime()&& extractedOne.getFrequency()==0){ //check whether the timespan clashes with the start of a one-off event
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
		
				}
				else
				{	//Daily event has been checked by another function
					if (extractedOne.getFrequency() == 1 || extractedOne.getFrequency() == 2 ||extractedOne.getFrequency() == 3)
					{
						d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
						d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
						d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
						d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
						d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
						d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
						
						if (d.Overlap(extractedOne.TimeSpan())){
							matchApptList[listCounter] = extractedOne;
							listCounter++;
						}
					}
					
				}
			}
			else if (frequency ==2)
			{
				if (d.StartTime().getTime() < extractedOne.TimeSpan().StartTime().getTime() && d.StartTime().getDay()== extractedOne.TimeSpan().StartTime().getDay() && extractedOne.getFrequency()==0){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
				else if (extractedOne.getFrequency() == 1 ||extractedOne.getFrequency() == 3){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
				else if (extractedOne.getFrequency() == 2)
				{
					if (d.StartTime().getDay()== extractedOne.TimeSpan().StartTime().getDay()){
						d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
						d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
						d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
						d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
						d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
						d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
						
						if (d.Overlap(extractedOne.TimeSpan())){
							matchApptList[listCounter] = extractedOne;
							listCounter++;
						}
					}
				}
			}
			else if (frequency ==3)
			{
				if (d.StartTime().getTime() < extractedOne.TimeSpan().StartTime().getTime()&& extractedOne.getFrequency()==0 && extractedOne.TimeSpan().StartTime().getDate() == d.StartTime().getDate()){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
				else if (extractedOne.getFrequency() == 1 ||extractedOne.getFrequency() == 2){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
				else if (extractedOne.getFrequency() == 3 && extractedOne.TimeSpan().StartTime().getDate() == d.StartTime().getDate()){
					d.StartTime().setYear(extractedOne.TimeSpan().StartTime().getYear()); 	
					d.StartTime().setMonth(extractedOne.TimeSpan().StartTime().getMonth());
					d.StartTime().setDate(extractedOne.TimeSpan().StartTime().getDate());
					d.EndTime().setYear(extractedOne.TimeSpan().EndTime().getYear());
					d.EndTime().setMonth(extractedOne.TimeSpan().EndTime().getMonth());
					d.EndTime().setDate(extractedOne.TimeSpan().EndTime().getDate());
					
					if (d.Overlap(extractedOne.TimeSpan())){
						matchApptList[listCounter] = extractedOne;
						listCounter++;
					}
				}
			}
			
		}
		Appt[] returnList = new Appt[listCounter]; //copy all the appointments back to an array with appropriate size
		for (int j = 0; j< listCounter; j++)
		{
			returnList[j] = matchApptList[j];
		}
		
		if (listCounter == 0)
		{
			return null;
		}
		else
			return returnList;
		
	
	}
	

	@Override
	public Appt[] RetrieveAppts(User entity, TimeSpan time) {//by Hin
		// TODO Auto-generated method stub
		if (entity == null)
			return null;
		if (entity.getUsername().equals(defaultUser.getUsername())) //check the user identity
		{
			return RetrieveAppts(time);
		}
		else{
			User tempUser = defaultUser;
			defaultUser = entity;
			Appt[] ApptList = RetrieveAppts(time);
			defaultUser = tempUser;
			return ApptList;
		}
		

	}
	
	public Appt[] RetrieveAppts(Location location) {//retrieve all appts with a particular location
		// TODO Auto-generated method stub
		Appt[] returnList = new Appt[mAppts.size()];
		int counter = 0;
		for (int i = 0; i< mAppts.size(); i++){
			if (location.getName().equals(mAppts.get(i).getLocation().getName())){
				returnList[counter] =  mAppts.get(i);
				counter++;
			}
		}
		if (counter ==0)
			return null;
		else					
			return Arrays.copyOf(returnList, counter);
		
	}
	
	public Appt[] RetrieveApptsAtTimeLocation(TimeSpan time, Location location){//retrieve all appts with a particular time and location
		Appt[] returnList = RetrieveAppts(location);
		if (returnList==null)
			return null;
		else
		{
			UserStorageController userController = UserStorageController.getInstance();
			int userNum = userController.getNumOfUser();
			User[] allUser = Arrays.copyOf(userController.RetrieveAllUsers(), userNum);
			Appt[] temp;
			Appt[] finalList = new Appt[numOfAppt];
			int counter = 0;
			boolean repeated;
			for (int i = 0; i<allUser.length; i++){ //go through all users and retrieve all appts with a particular time
				if (allUser[i]!=null)
				{
					temp = RetrieveAppts(allUser[i], time);
					if (temp!=null)
						for (int j = 0; j<temp.length ; j++){ //go through every appt from a single user
							repeated = false;
							for (int k = 0; k < counter; k++){ //check if the appt has extracted previously or not
								if (finalList[k].getID()==temp[j].getID()){
									repeated = true;
									break;
								}	 
							}
							if (!repeated){
								finalList[counter] = temp[j];
								counter++;
							}
						}
				}
			}
			if (counter>0){
				Appt[] finalReturnList = new Appt[numOfAppt];
				int finalcounter = 0;
				for (int i = 0; i< counter; i++){ //compare the list of appts with a particular time and that with a particular location
					for (int j = 0; j< returnList.length; j++){
						if (finalList[i].getID()==returnList[j].getID()){
							boolean repeat = false;
							for (int k = 0; k<finalcounter; k++){
								if (finalReturnList[k].getID() == finalList[i].getID()){
									repeat= true;
									break;
								}
							}
							if (!repeat){
								finalReturnList[finalcounter] = finalList[i];
								finalcounter++;
							}
						}
					}
				}
				return Arrays.copyOf(finalReturnList, finalcounter);
			}
			else
				return null;
		}

		
	}
	
	public Appt[] RetrieveAllAppts(){ //return all the appointments stored
		
		if (mAppts.size() >0){
			
		
			Appt[] returnList = new Appt[mAppts.size()];
			for (int i = 0; i< mAppts.size();i++)
			{
				returnList[i] = mAppts.get(i);
			}
			return returnList;
		}
		else
			return null;
	}

	
	public Appt[] RetrieveAllPublicAppt(TimeSpan d){ //return all public appointments within the timespan
	// TODO Auto-generated method stub
		
		//System.out.println("The size of hashmap: "+mAppts.size()); //testing
		Appt[] matchApptList = new Appt[mAppts.size()];
		int listCounter = 0;
		/*System.out.println(d.StartTime().getYear()+" "+ d.StartTime().getMonth()+" "+
						 d.StartTime().getDate()+" " +d.StartTime().getHours()+ " "+ 
						 d.StartTime().getMinutes());*/ //testing
		for (int i = InitialID ;i<(InitialID+ numOfAppt); i++) //Go through every record to check if the Timespan of the appointment is within the specified time
		{
			Appt extractedOne = mAppts.get(i);
			if (!extractedOne.getPublicity()){
				continue;
			}
			if (d.Overlap(extractedOne.TimeSpan())){
				matchApptList[listCounter] = extractedOne;
				listCounter++;
			}
			else{ //handling events with frequency
				TimeSpan ApptTime;
				TimeSpan ApptTime2;
				if (d.StartTime().getYear() < extractedOne.TimeSpan().StartTime().getYear()) //when handling frequency, those appointments with latter time span are ignored
					continue;
				else
					if (d.StartTime().getYear() == extractedOne.TimeSpan().StartTime().getYear())
						if (d.StartTime().getMonth() < extractedOne.TimeSpan().StartTime().getMonth())
							continue;
						else 
							if (d.StartTime().getMonth() == extractedOne.TimeSpan().StartTime().getMonth())
								if (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())
									continue;
				
				switch (extractedOne.getFrequency()){
				case 1:	
						if ((d.EndTime().getYear() > d.StartTime().getYear())|| (d.EndTime().getMonth() > d.StartTime().getMonth())|| (d.EndTime().getDate()- d.StartTime().getDate() > 1))
						{
							matchApptList[listCounter] = extractedOne; //if the time span is at least 3 days, the appointment must overlap with the designated time
							listCounter++;
						}
						else
						{
							ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
								 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
								 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
								 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
								 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0)); //create a time span in which a daily appointment happens
																						//on the specified day of the starting time of the input
							ApptTime2 = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));//create a time span in which a daily appointment happens
																					//on the specified day of the ending time of the input
							
							if (d.Overlap(ApptTime)|| d.Overlap(ApptTime2)){ //check if the daily appointment is within the timespan
								matchApptList[listCounter] = extractedOne;
								listCounter++;
							}
						}
						break;
				case 2:
						int specifiedStartWeekday = hkust.cse.calendar.gui.Utility.getweekday(d.StartTime());
						int specifiedEndWeekday = hkust.cse.calendar.gui.Utility.getweekday(d.EndTime());
						if (((specifiedStartWeekday > extractedOne.getWeekday())&& (specifiedEndWeekday > extractedOne.getWeekday()))|| //check x..A..B
							((specifiedStartWeekday < extractedOne.getWeekday())&& (specifiedEndWeekday < extractedOne.getWeekday()))|| //check A..B..x
							((specifiedStartWeekday > extractedOne.getWeekday())&& (specifiedEndWeekday < extractedOne.getWeekday())))  //check B..x..A
							break; //if the weekday is not in range, break
						else
							
							if ( specifiedStartWeekday== extractedOne.getWeekday()){ 
								ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
									 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
									 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
							
								if (d.Overlap(ApptTime)){ //check if the weekly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
							}
						else if (specifiedEndWeekday == extractedOne.getWeekday()){
								ApptTime = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
								if (d.Overlap(ApptTime)){ //check if the weekly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
							}
						else
						{ //the weekday is within the start time and end time
							matchApptList[listCounter] = extractedOne;
							listCounter++;
						}
						
					
						break;
				case 3:
						if (d.StartTime().getDate() == extractedOne.TimeSpan().StartTime().getDate()){
							ApptTime = new TimeSpan(new Timestamp(d.StartTime().getYear(), d.StartTime().getMonth(),
									 d.StartTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.StartTime().getYear(), 
									 d.StartTime().getMonth(), d.StartTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
							if (d.Overlap(ApptTime)){ //check if the monthly appointment is within the timespan
								matchApptList[listCounter] = extractedOne;
								listCounter++;
							}
						}
						else 
							if (d.EndTime().getDate() == extractedOne.TimeSpan().EndTime().getDate()){
								ApptTime = new TimeSpan(new Timestamp(d.EndTime().getYear(), d.EndTime().getMonth(),
									 d.EndTime().getDate(), extractedOne.TimeSpan().StartTime().getHours(), 
									 extractedOne.TimeSpan().StartTime().getMinutes(), 0, 0), new Timestamp(d.EndTime().getYear(), 
									 d.EndTime().getMonth(), d.EndTime().getDate(), extractedOne.TimeSpan().EndTime().getHours(),
									 extractedOne.TimeSpan().EndTime().getMinutes(), 0, 0));
								
								if (d.Overlap(ApptTime)){ //check if the monthly appointment is within the timespan
									matchApptList[listCounter] = extractedOne;
									listCounter++;				
								}
							}
							else
								if (((d.EndTime().getDate() > extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()< d.EndTime().getDate()))|| //check A.x.B
										((d.EndTime().getDate() < extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() < extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()> d.EndTime().getDate()) )|| //check B.A.x
										((d.EndTime().getDate() > extractedOne.TimeSpan().EndTime().getDate())&& (d.StartTime().getDate() > extractedOne.TimeSpan().StartTime().getDate())&&(d.StartTime().getDate()> d.EndTime().getDate()))){ //check x.B.A
									matchApptList[listCounter] = extractedOne;
									listCounter++;
								}
						
							
						break;
				}
			}
		}
		Appt[] returnList = new Appt[listCounter]; //copy all the appointments back to an array with appropriate size
		for (int j = 0; j< listCounter; j++)
		{
			returnList[j] = matchApptList[j];
		}
		if (listCounter == 0)
		{
			return null;
		}
		else
			return returnList;
	}
	
	@Override
	public Appt RetrieveJoinAppts(int joinApptID) { 
		// TODO Auto-generated method stub
		for (int i = 0; i< numOfAppt; i++){
			if (mAppts.get(i).getJoinID()== joinApptID)
			{
				return mAppts.get(i);
			}
		}
		return null;
		
	}

	@Override
	public void UpdateAppt(Appt appt) { //by Hin, useless currently
		// TODO Auto-generated method stub
		for (int i = InitialID ;i<(InitialID+ numOfAppt); i++){
			Appt extractedOne = mAppts.get(i);
			if (appt.getID()== extractedOne.getID())
			{
				mAppts.put(appt.getID(), appt);
				break;
			}
		}
	}

	@Override
	public void RemoveAppt(Appt appt) { //by Hin, remove an appt from the hashmap
		// TODO Auto-generated method stub
		
		if (appt != null){
			Map<Integer, Appt> temp = new HashMap<Integer, Appt>();
			int counterOfSave = InitialID;
			int counterJoinOfSave = InitialID;
			for (int i = InitialID ;i<(InitialID+ numOfAppt); i++) //copy all appointments except the one to be deleted to a temp hashmap
			{
				Appt extractedOne = mAppts.get(i);
				if (extractedOne.getID() == appt.getID()){ //do not copy the appt if the target ID matches.
					if (extractedOne.isJoint()){
						joinID--;
					}
					continue;
				}
				extractedOne.setID(counterOfSave);
				if (extractedOne.isJoint()){
					extractedOne.setJoinID(counterJoinOfSave);
					counterJoinOfSave++;
				}
				temp.put(counterOfSave, extractedOne);
				counterOfSave++;
			}
			mAppts.clear(); //clear the system hashmap
			numOfAppt--;
			for (int i = InitialID ;i<(InitialID+ numOfAppt); i++) //copy all appointments back to the hashmap from temp
			{
				Appt extractedOne = temp.get(i);
				mAppts.put(i, extractedOne);
			}
			mAssignedApptID--;
			
			saveAction();
		}
		

	}
	
	public LinkedList<String> RemoveApptAndNotifyInitializer(User user){ //remove all appts of a particular user and return a list of initalizer for notification
		LinkedList<String> listInit = new LinkedList<String>();
		UserpendingToDel.add(user.getUsername());
		int ApptsSize = mAppts.size();
		for (int i = 0 ; i<ApptsSize; i++){
			if (mAppts.get(i).getUser().equals(user.getUsername()))
			{
				Appt temp = mAppts.get(i);
				RemoveAppt(temp);
				ApptsSize = mAppts.size();
				i--;
			}
			else
				if (mAppts.get(i).getWaitingList().contains(user.getUsername())){
				mAppts.get(i).getWaitingList().remove(user.getUsername());
				if( !listInit.contains(mAppts.get(i).getUser()))
					listInit.add(mAppts.get(i).getUser());
			}
				
			
			else if (mAppts.get(i).getRejectList().contains(user.getUsername())){
				mAppts.get(i).getRejectList().remove(user.getUsername());
				
			}
			else if (mAppts.get(i).getAttendList().contains(user.getUsername())){
				mAppts.get(i).getAttendList().remove(user.getUsername());
				if( !listInit.contains(mAppts.get(i).getUser()))
					listInit.add(mAppts.get(i).getUser());
			}
			if (mAppts.get(i).getRemained()!= null)
				if (mAppts.get(i).getRemained().length>0){
					User[] remained = new User[mAppts.get(i).getRemained().length];
					boolean existInRemained = false;
					int counter1= 0;
					for (int j = 0; j < mAppts.get(i).getRemained().length;j++){
						if (!(mAppts.get(i).getRemained()[j].getUsername().equals(user.getUsername()))){
							remained[counter1] = mAppts.get(i).getRemained()[j];
							counter1++;
						}
						else
							existInRemained = true;
					}
					if (existInRemained){
						mAppts.get(i).setRemained(Arrays.copyOf(remained, counter1));
					}
				}
			if (mAppts.get(i).getInvited()!= null)
				if (mAppts.get(i).getInvited().length>0){
					User[] remained = new User[mAppts.get(i).getInvited().length];
					boolean existInRemained = false;
					int counter1= 0;
					for (int j = 0; j < mAppts.get(i).getInvited().length;j++){
						if (!(mAppts.get(i).getInvited()[j].getUsername().equals(user.getUsername()))){
							remained[counter1] = mAppts.get(i).getInvited()[j];
							counter1++;
						}
						else
							existInRemained = true;
					}
					if (existInRemained){
						mAppts.get(i).setInvited(Arrays.copyOf(remained, counter1));
					}
				}
			
		}
		
		
		if (listInit.size()==0)
			return null;
		else
		{
			saveAction();
			return listInit;
		}
	}
	
	public LinkedList<String> RemoveApptAndNotifyInitializer(Location _location){
		LinkedList<String> listInit = new LinkedList<String>();
		for (int i = 0; i < mAppts.size(); i++){
			if (mAppts.get(i).getLocation().getName().equals(_location.getName())){
				mAppts.get(i).setLocation(_locations[0]);
				listInit.add(mAppts.get(i).getUser());
			}
		}
		
		if (listInit.size() ==0)
			return null;
		else{
			
			saveAction();
			return listInit;
		}
	}

	public void saveAction(){
		SaveApptToXml();
	}
	
	public void self_check_of_appt(){
		for (int i = 0; i< numOfAppt; i++){
			if (mAppts.get(i).isJoint()){
				if (mAppts.get(i).getWaitingList().size()==0 && mAppts.get(i).getRejectList().size() ==0){
					mAppts.get(i).setState(true);
				}
				else
					mAppts.get(i).setState(false);
			}
		}
	}
	
	public Appt[] RetrieveApptInWait(User user){
		int counter = 0;
		Appt[] returnList = new Appt[joinID];
		for (int i = 0; i<numOfAppt; i++){
//			System.out.println(i);
//			System.out.println(user.getName());
//			System.out.println(mAppts.get(i).getWaitingList().getFirst());
//			System.out.println(mAppts.get(i).getWaitingList().contains(user.getName()));
			if (mAppts.get(i).getWaitingList().contains(user.getUsername())){
				returnList[counter] = mAppts.get(i);
				counter++;
			}
			
		}
		if (counter ==0)
			return null;
		else
			return Arrays.copyOf(returnList, counter);
	}
	
	private void reminderReset(){
		for (int i = 0 ; i< numOfAppt;i++){
			if (mAppts.get(i).getReminderNeed() && !mAppts.get(i).getReminderStatus()){
				mAppts.get(i).setReminderStatus(true);
			}
		}
	}
	
	@Override
	public User getDefaultUser() {
		// TODO Auto-generated method stub
		return defaultUser;
	}

	@Override
	public void LoadApptFromXml() {
		// TODO Auto-generated method stub
		XStream xstream = new XStream();
		xstream.alias("Appointment", Appt.class);

		try {
			FileInputStream fis = new FileInputStream("Appts.xml");
			xstream.fromXML(fis, oneInstance);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		self_check_of_appt();
		reminderReset();
		

	}
	
	public void refreshList(){
		UserStorageController usercon = UserStorageController.getInstance();
		User[] allU=usercon.RetrieveAllUsers();
		for (int i = 0 ; i< UserpendingToDel.size(); i++){
			boolean here = false;
			for (int j = 0 ;j <allU.length;j++){
				if (allU[j].getUsername().equals(UserpendingToDel.get(i))){
					here = true;
					break;
				}		
			}
			if (!here){
				
				UserpendingToDel.remove(UserpendingToDel.get(i));
				i--;
			}
		}
	}
	
	public LinkedList<String> getUserpending(){
		return UserpendingToDel;
	}
	
//	public void getWeather(){
//		this.temperature = hkust.cse.calendar.gui.CalGrid.temperature;
//		this.humidity = hkust.cse.calendar.gui.CalGrid.humidity;
//	}
	
	private void SaveApptToXml(){
		XStream xstream = new XStream();
		xstream.alias("Appointment", Appt.class);
		//String xml = xstream.toXML(this);
		try {
			FileOutputStream fs = new FileOutputStream("Appts.xml");
			xstream.toXML(this, fs);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<Integer,Double> getTemperature(){
		return temperature;
	}
	
	public Map<Integer,Double> getHumidity(){
		return humidity;
	}
	
	public Map<Integer,String> getSummary(){
		return summary;
	}
	
//	public boolean occupyLocation(String locationName){
//		for (int i = 1 ; i< _locations.length ; i++){ //starts from 1 as _locations[0] is null
//			if (_locations[i].equals(locationName)){
//				if (_locations[i].checkOccupy()==false){
//					_locations[i].setOccupy(true);
//					return true;
//				}
//				break;
//				
//			}
//		}
//		
//		return false;
//	}

}
