package hkust.cse.calendar.unit;

import hkust.cse.calendar.gui.CalGrid;
import hkust.cse.calendar.gui.ReminderDialog;

import java.sql.Timestamp;

public class Reminder implements Runnable { //to create a thread for the execution of reminder
	private Appt[] todayAppt;
	private Thread t1;
	private CalGrid parent;
	private TimeMachine timer;
	
	
	public Reminder(CalGrid parents, TimeMachine timer){ //constructor, also to create a thread and execute it
		
		if (t1 ==null)
			t1 = new Thread(this);
		
		parent = parents;
		this.timer = timer;
		
		t1.start();

	}
	
	
	
	
	private boolean checkTimeArrival_InADay(Timestamp currTime, Appt appointment){//given the appointment and current time, check if a reminder should be issued
		Timestamp apptstart = appointment.TimeSpan().StartTime(); 
		Timestamp remindertime = appointment.getReminder();
		int appt_timeInInteger = apptstart.getHours()*60 + apptstart.getMinutes(); //calculate the time of start of the appointment
		int curr_timeInInteger = currTime.getHours()*60 + currTime.getMinutes(); //calculate the current time
		int remindertimeInInteger = remindertime.getHours()*60 + remindertime.getMinutes(); //calculate the time of reminder
		//System.out.println(curr_timeInInteger+" "+remindertimeInInteger+" "+appt_timeInInteger); //for testing
		if ((curr_timeInInteger >= remindertimeInInteger)&&(remindertimeInInteger <= appt_timeInInteger) && (appt_timeInInteger>= curr_timeInInteger)){
			//System.out.println(remindertimeInInteger+" "+appt_timeInInteger);
			ReminderDialog reminder = new ReminderDialog(appointment); //if the reminder has been issued, return false
			return false;
		}
		return true;
	}




	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true)
		{
			try {
				Thread.currentThread().sleep(10000); //check if the reminder is needed every 5 second
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// for handling the remainders happen on the same day as the scheduled events
			Timestamp todayStart = new Timestamp(timer.outputYear(), timer.outputMonth()-1, timer.outputDay(), 0,0,0,0);
			Timestamp todayEnd = new Timestamp(timer.outputYear(), timer.outputMonth()-1, timer.outputDay(), 23, 59, 59 ,0);
			
			//retrieve all the appointments at a particular day
			todayAppt = parent.controller.RetrieveAppts(parent.controller.getDefaultUser(), new TimeSpan(todayStart, todayEnd));
							
			Timestamp currentTime = new Timestamp(timer.outputYear(), timer.outputMonth()-1, timer.outputDay(), 0, 0, 0,0);
			
			
			
			if (todayAppt != null){
				//System.out.println(parent.currentD +" "+ todayAppt.length); //for testing
				
				
				for (int i = 0; i< todayAppt.length; i++) //go through every appointment being passed to the function
				{
					if(todayAppt[i].getState()){
						if (todayAppt[i].getReminderNeed()){
							if (todayAppt[i].getLastReminder() != null){
								if (todayAppt[i].getLastReminder().getTime() != currentTime.getTime())
								{
									todayAppt[i].setReminderStatus(true); //allow the reminders to be issued in the future if there is no reminder on the same day (for frequent events)
								}
							
							}
							if (todayAppt[i].getReminderStatus())
							{
								boolean failureToRemind = checkTimeArrival_InADay(timer.getTime(),todayAppt[i]);
								todayAppt[i].setReminderStatus(failureToRemind);
								if (!failureToRemind)
								{								
									todayAppt[i].setLastReminder(currentTime); //store the time when the remainder was issued
								}
							}
						}
					}
				}
				
			}
		}
		
		
	}
}
