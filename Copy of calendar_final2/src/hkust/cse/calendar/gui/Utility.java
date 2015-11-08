package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeMachine;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;


public class Utility {

	public static int getNumber(String s) {
		if (s == null)
			return -1;
		if (s.trim().indexOf(" ") != -1) {
			JOptionPane.showMessageDialog(null,
					"Can't Contain Whitespace In Number !");
			return -1;
		}
		int result = 0;
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException n) {
			return -1;
		}
		return result;
	}

	public static Appt createDefaultAppt(int currentY, int currentM,
			int currentD, User me) {
		Appt newAppt = new Appt();
		newAppt.setID(0);
		Timestamp start = new Timestamp(0);
		start.setYear(currentY);
		start.setMonth(currentM - 1);
		start.setDate(currentD);
		start.setHours(9);
		start.setMinutes(0);

		Timestamp end = new Timestamp(0);
		end.setYear(currentY);
		end.setMonth(currentM - 1);
		end.setDate(currentD);
		end.setHours(9);
		end.setMinutes(30);

		newAppt.setTimeSpan(new TimeSpan(start, end));
		User[] temp = new User[1];
		temp[0] = me;
		// newAppt.setParticipants(temp);

		newAppt.setTitle("Untitled");
		newAppt.setInfo("Input description of this appointment");
		
		return newAppt;
	}

	public static Appt createDefaultAppt(int currentY, int currentM,
			int currentD, User me, int startTime) {
		Appt newAppt = new Appt();
		newAppt.setID(0);
		Timestamp start = new Timestamp(0);
		start.setYear(currentY);
		start.setMonth(currentM - 1);
		start.setDate(currentD);
		start.setHours(startTime / 60);
		start.setMinutes(startTime % 60);

		int dur = startTime + 60;
		Timestamp end = new Timestamp(0);
		end.setYear(currentY);
		end.setMonth(currentM - 1);
		end.setDate(currentD);
		end.setHours(dur / 60);
		end.setMinutes(dur % 60);

		
		
		newAppt.setTimeSpan(new TimeSpan(start, end));
		User[] temp = new User[1];
		temp[0] = me;

		newAppt.setTitle("Untitled");
		newAppt.setInfo("Input description of this appointment");
		return newAppt;
	}
	
	public static int timeDifferenceInMinute( Timestamp start, Timestamp end){
		return (end.getHours() - start.getHours()) * 60 + (end.getMinutes() - start.getMinutes());		
	}
	
	//return the day of week
	public static int getweekday(Timestamp e){ //valid input in Timestamp should be current year-1900, month-1
		java.util.GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.setTime(e);
		return (cal.get(java.util.Calendar.DAY_OF_WEEK)-1);
	}
		
		
	public static boolean isPastEvent(TimeSpan apptPeriod) { //check if the date has passed, by Hin
		 
		Timestamp start = apptPeriod.StartTime();

	    Timestamp today = new Timestamp(0);
	    today.setYear(hkust.cse.calendar.unit.TimeMachine.outputYear() - 1900); //get time from the TimeMachine
	    today.setMonth(hkust.cse.calendar.unit.TimeMachine.outputMonth() - 1);
	    today.setDate(hkust.cse.calendar.unit.TimeMachine.outputDay());
	    today.setHours(hkust.cse.calendar.unit.TimeMachine.outputHour());
	    today.setMinutes(hkust.cse.calendar.unit.TimeMachine.outputMinute());
	    
	    if( today.getTime() < start.getTime())
	    	return false;
	    else
	    	return true;
	    
	    
//		Calendar cal = Calendar.getInstance();
//	    int day = cal.get(Calendar.DATE);
//	    int month = cal.get(Calendar.MONTH) + 1;
//	    int year = cal.get(Calendar.YEAR);
//	    int hour = cal.get(Calendar.HOUR_OF_DAY);
//	    int minute = cal.get(Calendar.MINUTE);	
//		int day = hkust.cse.calendar.unit.TimeMachine.outputDay();
//		int month = hkust.cse.calendar.unit.TimeMachine.outputMonth();
//		int year = hkust.cse.calendar.unit.TimeMachine.outputYear() - 1900;
//		int hour = hkust.cse.calendar.unit.TimeMachine.outputHour();
//		int minute = hkust.cse.calendar.unit.TimeMachine.outputMinute();
		
	    

//	    if ( start.getYear() < year)
//	    {
//	    	System.out.println( start.getYear() + " " + year);
//	    	return false;
//	    }
//	    if (year == start.getYear() )
//	    	if ((start.getMonth()+1) < month )
//	    	{
//	    		System.out.println(start.getMonth()+ " "+month);
//	    		return false;
//	    	}
//	    if (year == start.getYear() && (start.getMonth()+1) == month)
//	    	if ( start.getDate() < day)
//	    	{
//	    		
//	    		return false;
//	    	}
//	    if (year == start.getYear() && (start.getMonth()+1) == month && start.getDate() == day)
//	    	if (start.getHours() < hour)
//	    	{
//	    		
//	    		return false;
//	    	}
//	    		
//	    if (year == start.getYear() && (start.getMonth()+1) == month && start.getDate() == day && start.getHours() == hour)
//	    	if (start.getMinutes() < minute)
//	    	{	    		
//	    		return false;
//	    	}
//	    
//	    return true;    		
	}

}
