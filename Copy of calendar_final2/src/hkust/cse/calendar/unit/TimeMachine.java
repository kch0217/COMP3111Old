package hkust.cse.calendar.unit;

import hkust.cse.calendar.gui.CalGrid;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class TimeMachine implements Runnable{ //weekday starts from Sunday, 0

	private static TimeMachine uniqueInstance = new TimeMachine();
	private Thread t1 = null;
	private Thread t2 = null;
	private boolean timerfirst = true;
	private boolean haltTimer = false;
	private boolean computerClock = false;
	private static Timestamp currentTime;
	
	public static final String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug","Sept", "Oct", "Nov","Dec"};
	public static final String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
	
	private static int day ;
	private static int month ;
	private static int year;
	private static int hour;
	private static int minute;
	private static int second;
	private static int weekday;
	
	

    private static CalGrid parent = null;
	
	@Override
	public void run() { //thread content
		// TODO Auto-generated method stub
		if (timerfirst){ //lock by the first thread executing it
			timerfirst = false;
			while (true){ 
				while (!computerClock){ //switch between program clock and computer clock

					try {
						Thread.currentThread().sleep(1000); //sleep every second
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
//					second++; //increase the timer by a second
//					if (second == 60) //increase the minute and hour and day
//					{
//						second = 0;
//						minute++;
//					}
//					if (minute ==60){
//						minute = 0;
//						hour++;
//					}
//					if (hour ==24){
//						hour = 0;
//						day++;
//						weekday = (weekday+1) % weekdays.length;
//						parent.UpdateCal(); //redraw the view
//					}
//					if ((day == 29) && (month == 2)){ //2 means February, check leap year
//						if (year % 4 != 0 || (year % 100)== 0 && (year % 400) != 0){ // e.g. 1700 is not a leap year
//							day = 1;
//							month++;
//						}
//		
//						
//					}
//					if ((day == 30) && (month ==2)){ //handling of leap year
//						day = 1;
//						month++;
//					}
//					if (((month == 1) || (month == 3)|| (month == 5)|| (month == 7)|| (month == 8) ||(month == 10) || (month == 12)) && (day== 32)){ //handling of months with 31 days
//						day = 1;
//						month++;
//					}
//					if (((month == 4) || (month == 6)|| (month == 9)|| (month == 11)) && (day== 31)){ //handling of months with 30 days
//						day =1;
//						month++;
//					}
//					if (month==13){ //switch to a new year
//						month =1;
//						year++;
//					}
					currentTime.setTime(currentTime.getTime() + 1000);
					year = currentTime.getYear() + 1900;
					month = currentTime.getMonth() +1;
					day = currentTime.getDate();
					hour = currentTime.getHours();
					minute = currentTime.getMinutes();
					second = currentTime.getSeconds();
					weekday = currentTime.getDay();
					
					//outputTheCurrentTime();
				}
				while (computerClock){
					try { //obtain the computer time every second
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Calendar cal = Calendar.getInstance();
				    day = cal.get(Calendar.DATE);
				    month = cal.get(Calendar.MONTH) + 1;
				    year = cal.get(Calendar.YEAR);
				    hour = cal.get(Calendar.HOUR_OF_DAY);
				    minute = cal.get(Calendar.MINUTE);
				    second = cal.get(Calendar.SECOND);
				    weekday = cal.get(Calendar.DAY_OF_WEEK)-1;
					
				}
			}
		}
		else
		{ //Thread 2 is responsible for providing an user interface for accessing the timer
			int option =-1;
		    while (option!=0){
		    	System.out.println("1) Output the current time");
		    	//System.out.println("2) Modify the time");
		    	System.out.println("2) Switch mode (Program clock <-> Computer Clock)");
		    	System.out.println("0) Quit");
		    	
		    	System.out.print("Please choose an option: ");
		    	Scanner scanner = new Scanner(System.in);
		    	option = Integer.parseInt(scanner.next());
		    	if (option ==1)
		    	{
		    		 outputTheCurrentTime();
		    		 
		    	}

//		    	if (option == 2){
//		    		System.out.println("Please input the year, month(in integer), day, hour, minute and second separated by a space: ");
//		    		year = Integer.parseInt(scanner.next());
//		    		month = Integer.parseInt(scanner.next());
//		    		day = Integer.parseInt(scanner.next());
//		    		hour = Integer.parseInt(scanner.next());
//		    		minute = Integer.parseInt(scanner.next());
//		    		second = Integer.parseInt(scanner.next());
//		    			
//
//		    		//Sun = 0, Mon = 1, Tue = 2, Wed = 3, Thurs = 4. Fri = 5, Sat = 6
//		    		java.util.GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance(); //set the weekday of the input date
//		    		cal.setTime(new Timestamp(year-1900, month-1, day, hour, minute, second, 0));
//
//		    		weekday = cal.get(java.util.Calendar.DAY_OF_WEEK)-1;
//
//
//		    		if (parent != null)
//		    			parent.UpdateCal(); //redraw the view
//		    		System.out.println();
//		    	}
		    	if (option ==3)
		    	{ //switch between program timer and computer clock
		    		if (!computerClock)
		    		{
		    			computerClock = true;
		    			System.out.println("Computer Clock is in effective.");
		    		}
		    		else{
		    			computerClock = false;
		    			System.out.println("Program Clock is in effective.");
		    		}
		    		System.out.println();

		    		

		    	}
		    }
		}
			
	}
	
	//private constructor to obtain the first instance of time from the computer and set up the thread, inaccessible for outsiders
	private TimeMachine( ){	// Modified Added parent reference to CalGrid By Kelvin
		Calendar cal = Calendar.getInstance();	
	    day = cal.get(Calendar.DATE);
	    month = cal.get(Calendar.MONTH) + 1;
	    year = cal.get(Calendar.YEAR);
	    hour = cal.get(Calendar.HOUR_OF_DAY);
	    minute = cal.get(Calendar.MINUTE);
	    second = cal.get(Calendar.SECOND);
	    weekday = cal.get(Calendar.DAY_OF_WEEK)-1;
	    currentTime = new Timestamp(year-1900, month-1, day, hour, minute, second, 0);
	    
	    
	    if (t1 ==null)
	    	t1 = new Thread(this);
	    
	    if (t2 == null)
	    	t2 = new Thread(this);
	    
	    t1.start(); //start a new thread to increment the clock, not for inherit
	    t2.start();
		
	}
	
	public static TimeMachine getInstance(CalGrid myparent){
		if (myparent!=null)
			parent = myparent;
		return uniqueInstance;
	}
	

	// below are the getter of different sections of time
	public void outputTheCurrentTime(){
		System.out.println("Today Date: "+day+"/"+months[month-1]+"/"+year+" "+weekdays[weekday]+" "+hour+":"+minute+":"+second);
		System.out.println();
	}
	
	
	public static int outputHour(){
		return hour;
	}

	public static int outputMinute(){
		return minute;
	}
	public static int outputSecond(){
		return second;
	}
	public static int outputYear(){
		return year;
	}
	public static int outputMonth(){
		return month;
	}
	public static int outputDay(){
		return day;
	}
	public static int outputWeekday(){
		return weekday;
	}
	
	public static Timestamp getTime(){
		return new Timestamp(year - 1900, month-1, day, hour, minute, second,0);
	}
	
	public static void setTimeMac(int cyear, int cmonth, int cday, int chour, int cminute, int csecond){
		
		currentTime.setTime((new Timestamp(cyear - 1900, cmonth-1, cday, chour, cminute, csecond,0).getTime()));
		if (parent != null){
//			System.out.println("HI");
			parent.UpdateCal();
		}
		
	}
}
