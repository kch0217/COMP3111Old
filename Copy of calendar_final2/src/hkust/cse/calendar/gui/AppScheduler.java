package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class AppScheduler extends JDialog implements ActionListener,
		ComponentListener {
	
	private JRadioButton[] reminder;
	private JRadioButton[] publicity;
	private JLabel reminderHL;
	private JTextField reminderH;
	private JLabel reminderML;
	private JTextField reminderM;
	private JComboBox<Location>locField;
	private JRadioButton []freq;
	private DefaultComboBoxModel combomodel;
	private JLabel gp;
	private Boolean[] option={true,false};
	private JComboBox<Boolean> isGroup;
	private ApptStorageControllerImpl con;
	//Intelligence
	private JLabel duration;
	private JComboBox<Integer> dur;
	private Integer[] durSelection;
	private JLabel choicelabel;
	private JComboBox<String> choice;
	DefaultComboBoxModel comBomodel2;
	DefaultComboBoxModel comBomodel3;
	HashMap<Integer ,schePack> myList;
	private boolean usedDuration = false;
	private JLabel weathercondition;
	private JTextField weather;
	//
	//
	private JLabel yearL;
	private JTextField yearF;
	private JLabel monthL;
	private JTextField monthF;
	private JLabel dayL;
	private JTextField dayF;
	private JLabel sTimeHL;
	private JTextField sTimeH;
	private JLabel sTimeML;
	private JTextField sTimeM;
	private JLabel eTimeHL;
	private JTextField eTimeH;
	private JLabel eTimeML;
	private JTextField eTimeM;

	private DefaultListModel model;
	private JTextField titleField;

	private JButton saveBut;
	private JButton CancelBut;
	private JButton inviteBut;
	private JButton rejectBut;
	
	private Appt NewAppt;
	private Appt OldAppt;
	private CalGrid parent;
	private boolean isNew = true;
	private boolean isChanged = true;
	private boolean isJoint = false;

	private JTextArea detailArea;

	private JSplitPane pDes;
	JPanel detailPanel;
	
	private final int FREQUENCY_LENGTH = 4;
	public final static int ONCE = 0;
	public final static int DAILY = 1;
	public final static int WEEKLY = 2;
	public final static int MONTHLY = 3;
	
//	private JTextField attendField;
//	private JTextField rejectField;
//	private JTextField waitingField;
	private int selectedApptId = -1;
	
	private String myState;//added by Hin to differentiate whether a new appointment is made or modification is made
	
	private Appt myAppt;
	
	//for intelligence
	private class schePack{
		public String TimeString;
		public TimeSpan Timespan;
	}
	//private JButton set;
	String[] mytempString = {"No available timeslots!"};
//	private JComboBox<String> Schedulable;
	private String[] ListOfSchedulable;
//	private JLabel durationText;
//	private JTextField durationField;
	

	private void commonConstructor(String title, CalGrid cal) {
		parent = cal;
		con=cal.controller;
		this.setAlwaysOnTop(false);
		setTitle(title);
		setModal(false);

		Container contentPane;
		contentPane = getContentPane();
		
		JPanel pDate = new JPanel();
		Border dateBorder = new TitledBorder(null, "DATE");
		pDate.setBorder(dateBorder);

		yearL = new JLabel("YEAR: ");
		pDate.add(yearL);
		yearF = new JTextField(6);
		pDate.add(yearF);
		monthL = new JLabel("MONTH: ");
		pDate.add(monthL);
		monthF = new JTextField(4);
		pDate.add(monthF);
		dayL = new JLabel("DAY: ");
		pDate.add(dayL);
		dayF = new JTextField(4);
		pDate.add(dayF);
		// freq reminder test gp duration
		freq= new JRadioButton[4];
		JPanel frequency=new JPanel();
		freq[0]=new JRadioButton("One-time");		//add the buttons of frequency
		freq[0].addActionListener(this);
		freq[1]=new JRadioButton("Daily");
		freq[1].addActionListener(this);
		freq[2]=new JRadioButton("Weekly");
		freq[2].addActionListener(this);
		freq[3]=new JRadioButton("Monthly");
		freq[3].addActionListener(this);
		JLabel test=new JLabel("Frequency:");

		ButtonGroup bg=new ButtonGroup();
		bg.add(freq[0]);
		bg.add(freq[1]);
		bg.add(freq[2]);
		bg.add(freq[3]);
		frequency.add(test);
		frequency.add(freq[0]);
		frequency.add(freq[1]);
		frequency.add(freq[2]);
		frequency.add(freq[3]);
		
		publicity = new JRadioButton[2];
		JPanel publicPanel = new JPanel();
		publicity[0] = new JRadioButton("Private");
		publicity[0].addActionListener(this);
		publicity[1] = new JRadioButton("Public");
		publicity[1].addActionListener(this);
		JLabel publicL = new JLabel("Event Type: ");
		ButtonGroup publicBG = new ButtonGroup();
		publicBG.add(publicity[0]);
		publicBG.add(publicity[1]);
		publicPanel.add(publicL);
		publicPanel.add(publicity[0]);
		publicPanel.add(publicity[1]);
		
		reminder = new JRadioButton[2];
		JPanel remSelect = new JPanel();
		reminder[0] = new JRadioButton("No");
		reminder[0].addActionListener(this);
		reminder[1] = new JRadioButton("Yes");
		reminder[1].addActionListener(this);
		JLabel remind=new JLabel("Reminder:");
		ButtonGroup remindBG = new ButtonGroup();
		remindBG.add(reminder[0]);
		remindBG.add(reminder[1]);
		remSelect.add(remind);
		remSelect.add(reminder[0]);
		remSelect.add(reminder[1]);
		
		JPanel remTime = new JPanel();
		reminderH = new JTextField(4);
		remTime.add(reminderH);
		reminderHL = new JLabel("Hour");
		remTime.add(reminderHL);
		reminderM = new JTextField(4);
		remTime.add(reminderM);
		reminderML = new JLabel("Minute");
		remTime.add(reminderML);
		remTime.add( new JLabel(" before the appointment Start"));
		
		JPanel rem = new JPanel();
		rem.setLayout(new BorderLayout());
		rem.add(remSelect, BorderLayout.CENTER);
		rem.add(remTime, BorderLayout.SOUTH);
		//duration
		JPanel dura=new JPanel();
		Border duraBorder = new TitledBorder(null, "SELECT TIMESLOTS");
		dura.setBorder(duraBorder);
		//set = new JButton("Confirm");
		dura.setLayout(new BorderLayout());
		JPanel durationlayer1=new JPanel();
		JPanel choicelayer=new JPanel();
		duration=new JLabel("Duration(in min):");
		durationCalc();
		dur=new JComboBox<Integer>(durSelection);
		dur.addActionListener(this);
		choicelabel=new JLabel("Available Timeslots:");
		
		choice=new JComboBox<String>();
		DefaultComboBoxModel comBomodel = new DefaultComboBoxModel(mytempString );
		choice.setModel(comBomodel);
		choice.addActionListener(this);
		
		durationlayer1.add(duration);
		durationlayer1.add(dur);
		//durationlayer1.add(set);
		//set.addActionListener(this);
		choicelayer.setLayout(new BorderLayout());
		choicelayer.add(choicelabel, BorderLayout.NORTH);
		choicelayer.add(choice, BorderLayout.CENTER);
		dura.add(durationlayer1, BorderLayout.NORTH);
		rem.add(choicelayer,BorderLayout.NORTH);
		
		//durationdone
		
		//group
		JPanel group=new JPanel();
		gp=new JLabel("group event?");
		
		isGroup= new JComboBox<Boolean>(option);
		isGroup.setEnabled(false);
		group.add(gp);
		group.add(isGroup);
		if (myState.equals("New")){
			if (myAppt.getWaitingList().size() > 0 || myAppt.getRejectList().size() > 0 || myAppt.getAttendList().size()>0)
			{
				//option[1] = true; //there is a problem in the setting
				isGroup.setSelectedItem(option[0]);
				
			}
			else{
				//option[0] = false;
				isGroup.setSelectedItem(option[1]);
			}
			
		}
		//weather
		JPanel weatherpan=new JPanel();
		weatherpan.setLayout(new BorderLayout());
		weathercondition=new JLabel("weather:");
		weather=new JTextField(10);
		weather.setText("no weather info is present");
		weather.setEditable(false);
		JScrollPane weatherscr=new JScrollPane(weather);
		weatherscr.setViewportView(weather);
		weatherpan.add(weathercondition, BorderLayout.NORTH);
		weatherpan.add(weatherscr,BorderLayout.CENTER);
		//separation line		
		JPanel psTime = new JPanel();
		Border stimeBorder = new TitledBorder(null, "START TIME");
		psTime.setBorder(stimeBorder);
		sTimeHL = new JLabel("Hour");
		psTime.add(sTimeHL);
		sTimeH = new JTextField(4);
		psTime.add(sTimeH);
		sTimeML = new JLabel("Minute");
		psTime.add(sTimeML);
		sTimeM = new JTextField(4);
		psTime.add(sTimeM);

		JPanel peTime = new JPanel();
		Border etimeBorder = new TitledBorder(null, "END TIME");
		peTime.setBorder(etimeBorder);
		eTimeHL = new JLabel("Hour");
		peTime.add(eTimeHL);
		eTimeH = new JTextField(4);
		peTime.add(eTimeH);
		eTimeML = new JLabel("Minute");
		peTime.add(eTimeML);
		eTimeM = new JTextField(4);
		peTime.add(eTimeM);

		JPanel pTime = new JPanel();
		pTime.setLayout(new BorderLayout());
		pTime.add("West", psTime);
		pTime.add("East", peTime);
		pTime.add(dura, BorderLayout.CENTER);
		// new pane containing time frequency and reminder and gp
		JPanel freqgp= new JPanel();
		freqgp.setLayout(new BorderLayout());
		freqgp.add(frequency, BorderLayout.NORTH);
		freqgp.add(group, BorderLayout.WEST);
		//weather
		freqgp.add(weatherpan, BorderLayout.CENTER);
		
		freqgp.add(publicPanel, BorderLayout.SOUTH);
		//freqgp.add(group, BorderLayout.SOUTH);
		JPanel timefreqloc= new JPanel();
		timefreqloc.setLayout(new BorderLayout());
		timefreqloc.add(pTime, BorderLayout.NORTH);
		timefreqloc.add(freqgp, BorderLayout.SOUTH);
		timefreqloc.add(rem, BorderLayout.CENTER);
		//
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.setBorder(new BevelBorder(BevelBorder.RAISED));
		top.add(pDate, BorderLayout.NORTH);
		top.add(timefreqloc, BorderLayout.CENTER);

		contentPane.add("North", top);

		JPanel titleAndTextPanel = new JPanel();
		JLabel titleL = new JLabel("TITLE");
		titleField = new JTextField(15);
		titleAndTextPanel.add(titleL);
		titleAndTextPanel.add(titleField);

		//
		Location[] locations=cal.controller.getLocationList();
		if(locations==null){
			locations=new Location[0];
		}
		JLabel locationL= new JLabel("LOCATION");		// list of locations
		combomodel=new DefaultComboBoxModel<Location>(locations);
		locField= new JComboBox<Location>(combomodel);
		//locField = new JComboBox<Location>(locations);
		
		titleAndTextPanel.add(locationL);
		titleAndTextPanel.add(locField);
		//
		
		//Test - Intelligence
		
		
//		durationText = new JLabel("Duration: ");
//		durationField = new JTextField(2);
//		durationField.setSize(50, 50);
		//titleAndTextPanel.add(set);
//		titleAndTextPanel.add(durationText);
//		titleAndTextPanel.add(durationField);
		
		
//		Schedulable = new JComboBox<String>();
//		Schedulable.setModel( comBomodel );
//		titleAndTextPanel.add(Schedulable);

		
		
		//Test
				
		detailPanel = new JPanel();
		detailPanel.setLayout(new BorderLayout());
		Border detailBorder = new TitledBorder(null, "Appointment Description");
		detailPanel.setBorder(detailBorder);
		detailArea = new JTextArea(10, 10);

		detailArea.setEditable(true);
		JScrollPane detailScroll = new JScrollPane(detailArea);
		detailPanel.add(detailScroll);

		pDes = new JSplitPane(JSplitPane.VERTICAL_SPLIT, titleAndTextPanel,
				detailPanel);

		top.add(pDes, BorderLayout.SOUTH);

		if (NewAppt != null) {
			detailArea.setText(NewAppt.getInfo());

		}
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));

//		inviteBut = new JButton("Invite");
//		inviteBut.addActionListener(this);
//		panel2.add(inviteBut);
		
		saveBut = new JButton("Save");
		saveBut.addActionListener(this);
		panel2.add(saveBut);

		rejectBut = new JButton("Reject");
		rejectBut.addActionListener(this);
		panel2.add(rejectBut);
		rejectBut.show(false);

		CancelBut = new JButton("Cancel");
		CancelBut.addActionListener(this);
		panel2.add(CancelBut);

		contentPane.add("South", panel2);
		NewAppt = new Appt();
		NewAppt.setID(-1); //not yet valid

		if (this.getTitle().equals("Join Appointment Content Change") || this.getTitle().equals("Join Appointment Invitation")){
			inviteBut.show(false);
			rejectBut.show(true);
			CancelBut.setText("Consider Later");
			saveBut.setText("Accept");
		}
		if (this.getTitle().equals("Someone has responded to your Joint Appointment invitation") ){
			inviteBut.show(false);
			rejectBut.show(false);
			CancelBut.show(false);
			saveBut.setText("confirmed");
		}
		if (this.getTitle().equals("Join Appointment Invitation") || this.getTitle().equals("Someone has responded to your Joint Appointment invitation") || this.getTitle().equals("Join Appointment Content Change")){
			allDisableEdit();
		}
		
		pack();

	}
	
	AppScheduler(String title, CalGrid cal, int selectedApptId) {
		myState = title; //added by Hin
		this.selectedApptId = selectedApptId;
		commonConstructor(title, cal);
	}

	AppScheduler(String title, CalGrid cal, Appt currAppt) {
		myAppt = currAppt;
		myState = title;
		
		commonConstructor(title, cal);
	}
	
	public void actionPerformed(ActionEvent e) {

		// distinguish which button is clicked and continue with require function
		if (e.getSource() == reminder[0]){
			reminderH.setEnabled(false);
			reminderM.setEnabled(false);
		}else if( e.getSource() == reminder[1]){
			reminderH.setEnabled(true);
			reminderM.setEnabled(true);
		}
		
		if (e.getSource() == CancelBut) {

			setVisible(false);
			dispose();
		} else if (e.getSource() == saveBut) {
			//System.out.println( myState);
			if( myState.equals("New")){
				saveButtonResponse();
				
			}
			else if( myState.equals("Modify")){
				//parent.controller.ManageAppt(OldAppt,1);
				saveButtonResponse();
			}
			if (this.isVisible())
				return;
			
		} else if (e.getSource() == rejectBut){
			if (JOptionPane.showConfirmDialog(this, "Reject this joint appointment?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0){
				NewAppt.addReject(getCurrentUser());
				NewAppt.getAttendList().remove(getCurrentUser());
				NewAppt.getWaitingList().remove(getCurrentUser());
				this.setVisible(false);
				dispose();
			}
		}
		
		if (e.getSource() == freq[0]){
			if (usedDuration)
				JOptionPane.showMessageDialog(null, "Please select the duration and timeslots again!");
			
//			durationFrequency(0);
		}
		if (e.getSource() == freq[1]){
			if (usedDuration)
				JOptionPane.showMessageDialog(null, "Please select the duration and timeslots again!");
//			durationFrequency(1);
		}
		if (e.getSource() == freq[2]){
			if (usedDuration)
				JOptionPane.showMessageDialog(null, "Please select the duration and timeslots again!");
//			durationFrequency(2);
		}
		if (e.getSource() == freq[3]){
			if (usedDuration)
				JOptionPane.showMessageDialog(null, "Please select the duration and timeslots again!");
//			durationFrequency(3);
		}
		if (e.getSource() == dur){ //for intelligence
			durPart(e);
//			JComboBox combo = (JComboBox)e.getSource();
//            Integer currentChoice = (Integer)combo.getSelectedItem();
//			int duration = currentChoice;
//			int[] Appdate = getValidDate();
//			Timestamp currTime = new Timestamp(Appdate[0], Appdate[1]-1, Appdate[2],0,0,0,0);
//			LinkedList<String> allUserList = myAppt.getPossiblePeople();
//			allUserList.add(parent.controller.getDefaultUser().getUsername());
//			//System.out.println(allUserList.size());
//			LinkedList<User> allUserList2 = new LinkedList<User>();
//			UserStorageController userCon = UserStorageController.getInstance();
//			for (int i = 0; i<allUserList.size();i++){
//				allUserList2.add(userCon.RetrieveUserByName(allUserList.get(i)));
//			}
//			//System.out.println(allUserList2.size());
//			
//			User[] allUser = allUserList2.toArray(new User[allUserList.size()]);
//			myList = schedulableTime(duration, currTime, allUser);
//			if (myList != null){
//				ListOfSchedulable = new String[myList.size()];
//	
//				for ( int i = 0; i<myList.size(); i++ ) {
//					ListOfSchedulable[i] = myList.get(i).TimeString;
//				}
//				comBomodel2 = new DefaultComboBoxModel(ListOfSchedulable );
//				
//				choice.setModel(comBomodel2);
//				
//        		sTimeH.setText(Integer.toString(myList.get(0).Timespan.StartTime().getHours()));
//        		sTimeM.setText(Integer.toString(myList.get(0).Timespan.StartTime().getMinutes()));
//        		eTimeH.setText(Integer.toString(myList.get(0).Timespan.EndTime().getHours()));
//        		eTimeM.setText(Integer.toString(myList.get(0).Timespan.EndTime().getMinutes()));
//			}
//			else{
//				comBomodel3 = new DefaultComboBoxModel(mytempString);
//				choice.setModel(comBomodel3);
//			}
			
		}
		if (e.getSource() == choice){
			choicedurPart(e);
//			JComboBox combo = (JComboBox)e.getSource();
//            String currentChoice = (String)combo.getSelectedItem();
//            schePack tempPack = null;
//            if (myList!=null){
//            	for (int i = 0 ; i<myList.size();i++){
//            		tempPack = myList.get(i);
//            		if (tempPack.TimeString.equals(currentChoice))
//            		{
//            			break;
//            		}
//            	}
//            	if (tempPack!=null){
//            		TimeSpan mySpan = tempPack.Timespan;
//            		sTimeH.setText(Integer.toString(mySpan.StartTime().getHours()));
//            		sTimeM.setText(Integer.toString(mySpan.StartTime().getMinutes()));
//            		eTimeH.setText(Integer.toString(mySpan.EndTime().getHours()));
//            		eTimeM.setText(Integer.toString(mySpan.EndTime().getMinutes()));
//            		
//            	}
//            }
            
		}
		
		parent.UpdateCal(); 
		//parent.getAppList().clear();
		//parent.getAppList().setTodayAppt(parent.GetTodayAppt());		
		parent.repaint();
	}
	
//	private void durationFrequency(int freq){
//		
//	}
	//testing
	private boolean isValid(TimeSpan t, User user,ApptStorageControllerImpl controller,Appt app){
		Appt[] case1=controller.RetrieveApptForChecking(t,user);
		Appt[] case2=controller.RetrieveFrequencyAppts(user, t, app.getFrequency());
		if(case1==null &&case2==null)
			return true;
		return false;
	}
	
	//testing
	
	private void durPart(ActionEvent e){ //action listener for duration comboBox
		JComboBox combo = (JComboBox)e.getSource();
        Integer currentChoice = (Integer)combo.getSelectedItem();
        if (currentChoice >0){
			int duration = currentChoice;
			int freqAppt = 0;
			
			for( int i = 0; i < FREQUENCY_LENGTH; i++){					// set the frequency
				if( freq[i].isSelected())
					freqAppt = i;
			}
	
			int[] Appdate = getValidDate();
			Timestamp currTime = new Timestamp(Appdate[0], Appdate[1]-1, Appdate[2],0,0,0,0);
			LinkedList<String> allUserList = myAppt.getPossiblePeople();
			allUserList.add(parent.controller.getDefaultUser().getUsername());
			//System.out.println(allUserList.size());
			LinkedList<User> allUserList2 = new LinkedList<User>();
			UserStorageController userCon = UserStorageController.getInstance();
			for (int i = 0; i<allUserList.size();i++){
				allUserList2.add(userCon.RetrieveUserByName(allUserList.get(i)));
			}
			//System.out.println(allUserList2.size());
			
			User[] allUser = allUserList2.toArray(new User[allUserList.size()]);
			myList = schedulableTime(duration, currTime, allUser, freqAppt);
			if (myList != null){
				ListOfSchedulable = new String[myList.size()];
	
				for ( int i = 0; i<myList.size(); i++ ) {
					ListOfSchedulable[i] = myList.get(i).TimeString;
				}
				comBomodel2 = new DefaultComboBoxModel(ListOfSchedulable );
				
				choice.setModel(comBomodel2);
				
	    		sTimeH.setText(Integer.toString(myList.get(0).Timespan.StartTime().getHours()));
	    		sTimeM.setText(Integer.toString(myList.get(0).Timespan.StartTime().getMinutes()));
	    		eTimeH.setText(Integer.toString(myList.get(0).Timespan.EndTime().getHours()));
	    		eTimeM.setText(Integer.toString(myList.get(0).Timespan.EndTime().getMinutes()));
			}
			else{
				comBomodel3 = new DefaultComboBoxModel(mytempString);
				choice.setModel(comBomodel3);
			}
	        
			usedDuration = true;
        }
	}
	
	private void choicedurPart(ActionEvent e){
		JComboBox combo = (JComboBox)e.getSource();
        String currentChoice = (String)combo.getSelectedItem();
        schePack tempPack = null;
        if (myList!=null){
        	for (int i = 0 ; i<myList.size();i++){
        		tempPack = myList.get(i);
        		if (tempPack.TimeString.equals(currentChoice))
        		{
        			break;
        		}
        	}
        	if (tempPack!=null){
        		TimeSpan mySpan = tempPack.Timespan;
        		sTimeH.setText(Integer.toString(mySpan.StartTime().getHours()));
        		sTimeM.setText(Integer.toString(mySpan.StartTime().getMinutes()));
        		eTimeH.setText(Integer.toString(mySpan.EndTime().getHours()));
        		eTimeM.setText(Integer.toString(mySpan.EndTime().getMinutes()));
        		
        	}
        }
        
	}

	private JPanel createPartOperaPane() {
		JPanel POperaPane = new JPanel();
		JPanel browsePane = new JPanel();
		JPanel controPane = new JPanel();

		POperaPane.setLayout(new BorderLayout());
		TitledBorder titledBorder1 = new TitledBorder(BorderFactory
				.createEtchedBorder(Color.white, new Color(178, 178, 178)),
				"Add Participant:");
		browsePane.setBorder(titledBorder1);

		POperaPane.add(controPane, BorderLayout.SOUTH);
		POperaPane.add(browsePane, BorderLayout.CENTER);
		POperaPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
		return POperaPane;

	}

	private int[] getValidDate() {

		int[] date = new int[3];
		date[0] = Utility.getNumber(yearF.getText());
		date[1] = Utility.getNumber(monthF.getText());
		if (date[0] < 1980 || date[0] > 2100) {
			JOptionPane.showMessageDialog(this, "Please input proper year",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (date[1] <= 0 || date[1] > 12) {
			JOptionPane.showMessageDialog(this, "Please input proper month",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		date[2] = Utility.getNumber(dayF.getText());
		int monthDay = CalGrid.monthDays[date[1] - 1];
		if (date[1] == 2) {
			GregorianCalendar c = new GregorianCalendar();
			if (c.isLeapYear(date[0]))
				monthDay = 29;
		}
		if (date[2] <= 0 || date[2] > monthDay) {
			JOptionPane.showMessageDialog(this,
			"Please input proper month day", "Input Error",
			JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return date;
	}

	private int getTime(JTextField h, JTextField min) {

		int hour = Utility.getNumber(h.getText());
		if (hour == -1)
			return -1;
		int minute = Utility.getNumber(min.getText());
		if (minute == -1)
			return -1;

		return (hour * 60 + minute);

	}

	private int[] getValidTimeInterval() {

		int[] result = new int[2];
		result[0] = getTime(sTimeH, sTimeM);
		result[1] = getTime(eTimeH, eTimeM);
		if ((result[0] % 15) != 0 || (result[1] % 15) != 0) {
			JOptionPane.showMessageDialog(this,
					"Minute Must be 0, 15, 30, or 45 !", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		if (!sTimeM.getText().equals("0") && !sTimeM.getText().equals("15") && !sTimeM.getText().equals("30") && !sTimeM.getText().equals("45") 
			|| !eTimeM.getText().equals("0") && !eTimeM.getText().equals("15") && !eTimeM.getText().equals("30") && !eTimeM.getText().equals("45")){
			JOptionPane.showMessageDialog(this,
					"Minute Must be 0, 15, 30, or 45 !", "Input Error",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}
		
		if (result[1] == -1 || result[0] == -1) {
			JOptionPane.showMessageDialog(this, "Please check time",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if (result[1] <= result[0]) {
			JOptionPane.showMessageDialog(this,
					"End time should be bigger than \nstart time",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		if ((result[0] < AppList.OFFSET * 60)
				|| (result[1] > (AppList.OFFSET * 60 + AppList.ROWNUM * 2 * 15))) {
			JOptionPane.showMessageDialog(this, "Out of Appointment Range !",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return result;
	}
	
//  Depreciated
//	private boolean isPastEvent(TimeSpan apptPeriod) { //check if the date has passed, by Hin, Kelvin //Has been relocated to Utility.java
//		 
//		Timestamp start = apptPeriod.StartTime();		// get the start time of the TimeSpan
//		
//		Calendar cal = Calendar.getInstance();
//	    int day = cal.get(Calendar.DATE);
//	    int month = cal.get(Calendar.MONTH) + 1;
//	    int year = cal.get(Calendar.YEAR);
//	    int hour = cal.get(Calendar.HOUR_OF_DAY);
//	    int minute = cal.get(Calendar.MINUTE);
//		int day = parent.timeCounter.outputDay();
//		int month = parent.timeCounter.outputMonth();
//		int year = parent.timeCounter.outputYear() - 1900;
//		int hour = parent.timeCounter.outputHour();
//		int minute = parent.timeCounter.outputMinute();
//
//	    Timestamp today = new Timestamp(0);				// Create a Timestamp of today
//	    today.setYear(parent.timeCounter.outputYear() - 1900);
//	    today.setMonth(parent.timeCounter.outputMonth() - 1);
//	    today.setDate(parent.timeCounter.outputDay());
//	    today.setHours(parent.timeCounter.outputHour());
//	    today.setMinutes(parent.timeCounter.outputMinute());
//	    
//	    if( today.getTime() < start.getTime())			// check if the start time is past or not
//	    	return true;
//	    else
//	    	return false;
//		
//	    System.out.println(year+" "+month+" "+day+" "+hour+" "+minute);
//
//	    if ( start.getYear() < year)
//	    {
//	    	//System.out.println( start.getYear() + " " + year);
//	    	return false;
//	    }
//	    if (year == start.getYear() )
//	    	if ((start.getMonth()+1) < month )
//	    	{
//	    		//System.out.println(start.getMonth()+ " "+month);
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
//	    	 
//	}
	
	private boolean isConflictEvent( TimeSpan apptPeriod, int frequency){	// By Kelvin
																			// Check if there is an event conflict with the input event
		if( apptPeriod == null){
			return false;
		}
		
		Timestamp start = apptPeriod.StartTime();							// Get the start and end of the Event
		Timestamp end = apptPeriod.EndTime();
		int newApptDuration = ( end.getHours() - start.getHours())*60 + ( end.getMinutes() - start.getMinutes());	// Get the length of the event
		
		if( frequency == ONCE){												// If frequency is Once Only
			Appt[] apptAry = parent.GetThisDayAppt(start);					// only check the event happened on event day
			if( apptAry == null)
				return false;

			//System.out.println("Number of appointments: "+ apptAry.length);
			for( int i = 0; i < apptAry.length; i++){						// For all event in the array
				//System.out.println("The current iteration i:"+ i);
				//System.out.println(apptAry[i].getID());
				Timestamp apptStart = apptAry[i].TimeSpan().StartTime();	// Get the length of the event
				Timestamp apptEnd = apptAry[i].TimeSpan().EndTime();
				//System.out.println(apptEnd.getHours()+" "+apptStart.getHours()+" "+apptEnd.getMinutes()+" "+apptStart.getMinutes());

				int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 + ( apptEnd.getMinutes() - apptStart.getMinutes());
				int durationBetweenStarts =  ( apptStart.getHours() - start.getHours())*60 + ( apptStart.getMinutes() - start.getMinutes());

				if ( durationBetweenStarts >= 0){							// check if the event is conflict or not
					if( durationBetweenStarts < newApptDuration)
						return true;
				}
				else{
					if( ( durationBetweenStarts*-1) < thisApptDuration)
						return true;
				}
			}		
		}
		else{																// If other Frequency
			Appt[] apptAry = parent.GetAllAppt();							// get all the Appt to check
			if( apptAry == null)
				return false;
			if( frequency == DAILY){	// Frequency Daily: Check all the event
				for( int i = 0; i < apptAry.length; i++){
					
					//System.out.println("The current iteration i:"+ i);
					//System.out.println(apptAry[i].getID());
					Timestamp apptStart = apptAry[i].TimeSpan().StartTime();
					Timestamp apptEnd = apptAry[i].TimeSpan().EndTime();
					//System.out.println(apptEnd.getHours()+" "+apptStart.getHours()+" "+apptEnd.getMinutes()+" "+apptStart.getMinutes());

					int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 + ( apptEnd.getMinutes() - apptStart.getMinutes());
					int durationBetweenStarts =  ( apptStart.getHours() - start.getHours())*60 + ( apptStart.getMinutes() - start.getMinutes());

					if ( durationBetweenStarts >= 0){
						if( durationBetweenStarts < newApptDuration)
							return true;
					}
					else{
						if( ( durationBetweenStarts*-1) < thisApptDuration)
							return true;
					}
				}		
			}
			else if( frequency == WEEKLY){	// Frequency Weekly: check the event happened on the same weekday, Frequency is Daily or Monthly
				for( int i = 0; i < apptAry.length; i++){
					
					//System.out.println("The current iteration i:"+ i);
					//System.out.println(apptAry[i].getID());
					Timestamp apptStart = apptAry[i].TimeSpan().StartTime();
					if( apptStart.getDay() == start.getDay() || apptAry[i].getFrequency() == DAILY || apptAry[i].getFrequency() == MONTHLY){	
						Timestamp apptEnd = apptAry[i].TimeSpan().EndTime();																	
						//System.out.println(apptEnd.getHours()+" "+apptStart.getHours()+" "+apptEnd.getMinutes()+" "+apptStart.getMinutes());

						int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 + ( apptEnd.getMinutes() - apptStart.getMinutes());
						int durationBetweenStarts =  ( apptStart.getHours() - start.getHours())*60 + ( apptStart.getMinutes() - start.getMinutes());

						if ( durationBetweenStarts >= 0){
							if( durationBetweenStarts < newApptDuration)
								return true;
						}
						else{
							if( ( durationBetweenStarts*-1) < thisApptDuration)
								return true;
						}
					}					
				}		
			}
			else{	// Frequency Monthly:  check the event happened on the same month day, Frequency is Daily or Weekly
				for( int i = 0; i < apptAry.length; i++){
					//System.out.println("The current iteration i:"+ i);
					//System.out.println(apptAry[i].getID());
				
					Timestamp apptStart = apptAry[i].TimeSpan().StartTime();
					if( apptStart.getDate() == start.getDate() || apptAry[i].getFrequency() == DAILY || apptAry[i].getFrequency() == WEEKLY){
						Timestamp apptEnd = apptAry[i].TimeSpan().EndTime();
						//System.out.println(apptEnd.getHours()+" "+apptStart.getHours()+" "+apptEnd.getMinutes()+" "+apptStart.getMinutes());

						int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 + ( apptEnd.getMinutes() - apptStart.getMinutes());
						int durationBetweenStarts =  ( apptStart.getHours() - start.getHours())*60 + ( apptStart.getMinutes() - start.getMinutes());

						if ( durationBetweenStarts >= 0){
							if( durationBetweenStarts < newApptDuration)
								return true;
						}
						else{
							if( ( durationBetweenStarts*-1) < thisApptDuration)
								return true;
						}
					}			
				}		
			}
		}
		return false;
	}
	
	
	
	private void saveButtonResponse() { //by Hin
		// Fix Me!
		// Save the appointment to the hard disk
		
		
		
		int[] Appdate = getValidDate(); //get the input of date and time
		int[] Apptime = getValidTimeInterval();
		
		if (Appdate == null || Apptime == null ) //for validation
		{
			return;
		}
		
		Timestamp startApp = CreateTimeStamp(Appdate, Apptime[0]);	// set the TimeSpan for the of the event
		Timestamp endApp =  CreateTimeStamp(Appdate, Apptime[1]);
		TimeSpan startend = new TimeSpan(startApp, endApp);
		Timestamp reminderApp = null;	// preset the reminder Time as null for no reminder Time
		//System.out.println(endApp.getHours());
		
		int freqAppt = 0;
		
		for( int i = 0; i < FREQUENCY_LENGTH; i++){					// set the frequency
			if( freq[i].isSelected())
				freqAppt = i;
		}
		

		
		if  (myState.equals("Modify")){				  	// Delete the old appt for modify the recent appt if old appt is after current time
			
			if ( hkust.cse.calendar.gui.Utility.isPastEvent( OldAppt.TimeSpan()))	// Check Past Event
			{
				JOptionPane.showMessageDialog(this, "Events in the past cannot be scheduled or modified",
						"Input Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			parent.controller.ManageAppt(OldAppt,1);
			
		
		}
		
		myAppt.setTimeSpan(startend);
		java.util.GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
		cal.setTime(startApp);
		myAppt.setWeekday(cal.get(java.util.Calendar.DAY_OF_WEEK)-1); //set the weekday
		myAppt.setFrequency( freqAppt);
		//System.out.println(myAppt.TimeSpan().StartTime().getDate());
		
		String selecteditem=locField.getSelectedItem().toString();
		int capacityofloc=0;
		Location[] allloc=parent.controller.getLocationList();
		for(int b=0;b<allloc.length;b++){
			if(allloc[b].getName().equals(selecteditem)){
				capacityofloc=allloc[b].getCapacity();
			}
		}
		myAppt.setLocation( new Location(locField.getSelectedItem().toString(),capacityofloc));
		//System.out.println(myAppt.TimeSpan().StartTime().getDate());
		//
		boolean workable=isValid(myAppt.TimeSpan(),UserStorageController.getInstance().RetrieveUserByName(getCurrentUser()),parent.controller,myAppt);
		//System.out.println(myAppt.TimeSpan().StartTime().getDate());
		if(!workable){
			JOptionPane.showMessageDialog(null, "the timeslot is conflicted with possible events");
			return;
		}
		
		if ( hkust.cse.calendar.gui.Utility.isPastEvent( startend))	// Check Past Event
		{
			JOptionPane.showMessageDialog(this, "Events in the past cannot be scheduled or modified",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
				parent.controller.ManageAppt(OldAppt,3);
			return;
		}
		
		if (isConflictEvent( startend, freqAppt)){					// Check Conflict
			JOptionPane.showMessageDialog(this, "Event is Conflicted with another scheduled event",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
				parent.controller.ManageAppt(OldAppt,3);
			return;
		}
		int groupeventsize=myAppt.getWaitingList().size()+1+myAppt.getAttendList().size();
		//System.out.println(groupeventsize +" "+ myAppt.getLocation().getName()+" "+myAppt.getLocation().getCapacity());
		
		if(groupeventsize>myAppt.getLocation().getCapacity()){
			JOptionPane.showMessageDialog(null, "number of participants exceeds capacity of location");
			return;
		}
		for (int i = 0; i < myAppt.getWaitingList().size(); i++){ // check all users to see if they have conflict
			UserStorageController con = UserStorageController.getInstance();
			if (hkust.cse.calendar.gui.InviteDialog.checkConflict(parent.controller, con.RetrieveUserByName(myAppt.getWaitingList().get(i)), myAppt)){
				JOptionPane.showMessageDialog(this, "Event is Conflicted with "+ myAppt.getWaitingList().get(i)+"'s scheduled event",
						"Input Error", JOptionPane.ERROR_MESSAGE);
				if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
					parent.controller.ManageAppt(OldAppt,3);
				return;
			}
		}
		if (!myAppt.getLocation().getName().equals(" ")){
			Appt[] conflictingLocation = parent.controller.RetrieveApptsbyLocationTime(startend, myAppt.getLocation());
			if (conflictingLocation!=null){
				JOptionPane.showMessageDialog(this, "Event is Conflicted with another event in terms of location",
						"Input Error", JOptionPane.ERROR_MESSAGE);
				if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
					parent.controller.ManageAppt(OldAppt,3);
				return;
			}
		}
		if (titleField.getText().trim().isEmpty()){					// Check if the Title is empty or not
			JOptionPane.showMessageDialog(this, "Title must be provided!",
					"Input Error", JOptionPane.ERROR_MESSAGE);
			if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
				parent.controller.ManageAppt(OldAppt,3);
			return;
		}
		if ( reminder[1].isSelected()){								// Check has reminder 
			int remindH = Integer.parseInt( reminderH.getText());
			int remindM = Integer.parseInt( reminderM.getText());
			if((remindH < 0 || remindM < 0) || (remindH == 0 && remindM == 0)){			// Check invalid input
				JOptionPane.showMessageDialog(this, "Reminder Time must be positive!",
					"Input Error", JOptionPane.ERROR_MESSAGE);
				if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
					parent.controller.ManageAppt(OldAppt,3);
				return;
			}
			if( getTime( reminderH, reminderM) > getTime( sTimeH, sTimeM)){				// Check the reminder is within today or not 
				JOptionPane.showMessageDialog(this, "Reminder Must be within the Day of the event!",
						"Input Error", JOptionPane.ERROR_MESSAGE);
				if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
					parent.controller.ManageAppt(OldAppt,3);
				return;
			}			
			else {																		
				long time = (remindH * 60 + remindM) * 60 * 1000;
				reminderApp = new Timestamp(startApp.getTime() - time);
				if( reminderApp.getTime() < hkust.cse.calendar.unit.TimeMachine.getTime().getTime()){ // Check the reminder is past or not 
					JOptionPane.showMessageDialog(this, "Reminder Must before the current time!",
							"Input Error", JOptionPane.ERROR_MESSAGE);
					if  (myState.equals("Modify"))				  	// add the old appt back if it is modify
						parent.controller.ManageAppt(OldAppt,3);
					return;
				}
//				System.out.println(reminderApp.getYear());
//				System.out.println(reminderApp.getMonth());
//				System.out.println(reminderApp.getDate());
//				System.out.println(reminderApp.getHours());
//				System.out.println(reminderApp.getMinutes());
			}
		}
		
		// If all passes
		// Create a new Appointment to save all the information and save
		
		
		
		//myAppt.setTimeSpan(startend);
		
//		java.util.GregorianCalendar cal = (GregorianCalendar) Calendar.getInstance();
//		cal.setTime(startApp);
		//myAppt.setWeekday(cal.get(java.util.Calendar.DAY_OF_WEEK)-1); //set the weekday
		
		myAppt.setTitle(titleField.getText());
		
//		myAppt.setTimeSpan(startend);
//		java.util.GregorianCalendar cal2 = (GregorianCalendar) Calendar.getInstance();
//		cal2.setTime(startApp);
//		myAppt.setWeekday(cal.get(java.util.Calendar.DAY_OF_WEEK)-1); //set the weekday
//		myAppt.setFrequency( freqAppt);
//		myAppt.setLocation( new Location(locField.getSelectedItem().toString()));

		String appDetails = detailArea.getText();
		
		myAppt.setInfo(appDetails);
		
		if ( reminder[1].isSelected()){ //check whether reminder is required
			myAppt.setReminder(reminderApp);
			myAppt.setReminderStatus(true);
			myAppt.setReminderNeed(true);
		
		}
		else
		{
			myAppt.setReminderStatus(false);
			//appointment.setReminder(reminderApp);
			myAppt.setReminderNeed(false);
		}
		if( publicity[1].isSelected()){
			myAppt.setPublicity(true);
		}
		else{
			myAppt.setPublicity(false);
		}
		//if ((boolean)isGroup.getItemAt(isGroup.getSelectedIndex()))
		if (myAppt.getWaitingList().size() > 0 || myAppt.getRejectList().size() > 0 || myAppt.getAttendList().size()>0)
		{
			//System.out.println((boolean)isGroup.getItemAt(isGroup.getSelectedIndex()));
			myAppt.setJoint(true);
		}
		else
			myAppt.setJoint(false);
		
		//myAppt.setFrequency( freqAppt);
		if (freqAppt > 0){
			//checkfreqAppt(appointment);
		}
		//System.out.println(myAppt.TimeSpan().StartTime().getDate());
		parent.controller.ManageAppt(myAppt, 3); 	// 3 means create a new appointment
		this.setVisible(false);							// close the window
		

		
	}

	private Timestamp CreateTimeStamp(int[] date, int time) {
		Timestamp stamp = new Timestamp(0);
		stamp.setYear(date[0]);
		stamp.setMonth(date[1] - 1);
		stamp.setDate(date[2]);
		stamp.setHours(time / 60);
		stamp.setMinutes(time % 60);
		return stamp;
	}

	public void updateSetApp(Appt appt, String State) {
		// Fix Me!
		if( appt != null){	// If the appt is not empty Set all the information in the appt to the window
			OldAppt = appt;
			if (State.equals("Modify"))
				myAppt = (Appt) OldAppt.clone();
			
			//System.out.println(myAppt.getPossiblePeople().size());
			yearF.setText( Integer.toString( appt.TimeSpan().StartTime().getYear() + 1900));
			monthF.setText( Integer.toString( appt.TimeSpan().StartTime().getMonth() + 1));
			dayF.setText( Integer.toString( appt.TimeSpan().StartTime().getDate()));
			sTimeH.setText( Integer.toString( appt.TimeSpan().StartTime().getHours()));
			sTimeM.setText( Integer.toString( appt.TimeSpan().StartTime().getMinutes()));
			eTimeH.setText( Integer.toString( appt.TimeSpan().EndTime().getHours()));
			eTimeM.setText( Integer.toString( appt.TimeSpan().EndTime().getMinutes()));
			titleField.setText( appt.getTitle());
			detailArea.setText( appt.getInfo());
			freq[ appt.getFrequency()].setSelected(true);
			//weather
			String date=yearF.getText()+monthF.getText();
			if(Integer.parseInt(dayF.getText())/10==0){
				date=date+"0"+Integer.parseInt(dayF.getText());
			}
			else{
				date+=Integer.parseInt(dayF.getText());
			}
			int key=Integer.parseInt(date);
			if(parent.controller.getTemperature().containsKey(key)){
				Font font = new Font("Courier", Font.BOLD,10);
				weather.setFont(font);
				weather.setText(parent.controller.getTemperature().get(key)+"degree C/ "+parent.controller.getHumidity().get(key)*100+"% "
						+parent.controller.getSummary().get(key));
			}
			if( appt.getPublicity() == true){
				publicity[1].setSelected(true);
			}
			else{
				publicity[0].setSelected(true);
			}			
			
			if( appt.getReminderNeed() == true){
				reminder[1].setSelected(true);
				int tempH = appt.TimeSpan().StartTime().getHours() - appt.getReminder().getHours();
				int tempM = appt.TimeSpan().StartTime().getMinutes() - appt.getReminder().getMinutes();
				if( tempM < 0){
					tempH--;
					tempM += 60;
				}
				reminderH.setText( Integer.toString( tempH));
				reminderM.setText( Integer.toString( tempM));
			}
			else{
				reminder[0].setSelected(true);
				reminderH.setText( Integer.toString( 0));
				reminderM.setText( Integer.toString( 0));
				reminderH.setEnabled(false);
				reminderM.setEnabled(false);
			}
			if (State.equals("Modify"))
				if (appt.getWaitingList().size() > 0 || appt.getRejectList().size() > 0 || appt.getAttendList().size()>0)
				{
					isGroup.setSelectedItem(option[0]);
				}
				else
					isGroup.setSelectedItem(option[1]);
			//Location k=(Location)locField.getSelectedItem();
			//int index=combomodel.getIndexOf(appt.getLocation());
			//System.out.println(index);
			int index1=-1;
			if(appt.getLocation()!=null){
				for(int i=0;i<locField.getItemCount();i++){
					if(appt.getLocation().getName().equals(locField.getItemAt(i).getName())){
						index1=i;
					}
				}
				locField.setSelectedIndex(index1);
			}

			//locField.setSelectedIndex(index);

		}
	}

	public void componentHidden(ComponentEvent e) {

	}

	public void componentMoved(ComponentEvent e) {

	}

	public void componentResized(ComponentEvent e) {

		Dimension dm = pDes.getSize();
		double width = dm.width * 0.93;
		double height = dm.getHeight() * 0.6;
		detailPanel.setSize((int) width, (int) height);

	}

	public void componentShown(ComponentEvent e) {

	}
	
	public String getCurrentUser()		// get the id of the current user
	{
		return this.parent.mCurrUser.toString();
	}
	
	private void allDisableEdit(){
		yearF.setEditable(false);
		monthF.setEditable(false);
		dayF.setEditable(false);
		sTimeH.setEditable(false);
		sTimeM.setEditable(false);
		eTimeH.setEditable(false);
		eTimeM.setEditable(false);
		titleField.setEditable(false);
		detailArea.setEditable(false);
	}
	
	private void durationCalc(){//calculate duration
		durSelection = new Integer[41];
		for (int i = 0; i<= 40; i++)
		{
			durSelection[i] = 15*i;
		}
	}
	
	private HashMap<Integer, schePack> schedulableTime(int duration, Timestamp timeOfSchedule, User[] users, int freqAppt){ //assume timeOfSchedule has year, month, date, duration is in 15 min interval >0
		int currUnit = 0; //in min, starts from 8:00, start value: 0, max value: 10*60-15, total 40 units
		//System.out.println(hkust.cse.calendar.unit.TimeMachine.outputYear()+" "+timeOfSchedule.getYear()+" "+hkust.cse.calendar.unit.TimeMachine.outputMonth()+ timeOfSchedule.getMonth());
		if (timeOfSchedule.getYear() == hkust.cse.calendar.unit.TimeMachine.outputYear() && timeOfSchedule.getMonth()== hkust.cse.calendar.unit.TimeMachine.outputMonth()-1 && timeOfSchedule.getDate() == hkust.cse.calendar.unit.TimeMachine.outputDay())
		{
			timeOfSchedule.setHours(hkust.cse.calendar.unit.TimeMachine.outputHour());
			timeOfSchedule.setMinutes(hkust.cse.calendar.unit.TimeMachine.outputMinute());
			
			if (!(timeOfSchedule.getHours()<8)){
				if (timeOfSchedule.getHours()==8 && timeOfSchedule.getMinutes() ==0)
					currUnit = 0;
				else
					{
						currUnit = (int) Math.ceil(((timeOfSchedule.getHours()-8)*60 + timeOfSchedule.getMinutes())/15.0);
					}
			}
			//System.out.println(timeOfSchedule.getHours()+" "+timeOfSchedule.getMinutes()+" "+currUnit);
		}
		else
			currUnit = 0;
		if (duration>10*60)
			return null;
		HashMap<Integer, schePack> mytime = new HashMap<Integer, schePack>();
		int StringCounter = 0;
		//System.out.println(users.length);
		
		for (  ; currUnit <= (40-duration/15); currUnit++){
			Timestamp start = new Timestamp(timeOfSchedule.getYear(), timeOfSchedule.getMonth(), timeOfSchedule.getDate(),8+currUnit/4 ,(currUnit*15)%60 ,0,0);
			Timestamp end = new Timestamp(timeOfSchedule.getYear(), timeOfSchedule.getMonth(), timeOfSchedule.getDate(),8+currUnit/4 ,(currUnit*15)%60+duration ,0,0);
			boolean conflict = false;
			boolean modifyExist = false;
			for (int j = 0; j< users.length; j++){
				Appt[] tempList = parent.controller.RetrieveApptForChecking(new TimeSpan(start, end), users[j]);
				Appt[] tempList2 = parent.controller.RetrieveFrequencyAppts(users[j], new TimeSpan(start, end), freqAppt);
				if (tempList!=null ||tempList2!=null){ //the timeslot of the modified appointment should be free
					if (myState.equals("Modify")&& tempList.length ==1 &&tempList2.length ==0){
						if (tempList[0].getID()==myAppt.getID()){
							conflict = false;
							modifyExist = true;
						}
					}
					else
						if (myState.equals("Modify")&& tempList.length ==0 &&tempList2.length ==1){
							if (tempList2[0].getID()==myAppt.getID()){
								conflict = false;
								modifyExist = true;
							}
						}
						else
							if (myState.equals("Modify")&& tempList.length ==1 &&tempList2.length ==1){
								if (tempList2[0].getID()==myAppt.getID() && tempList[0].getID()==myAppt.getID()){
									conflict = false;
									modifyExist = true;
								}
								else
								{
									conflict = true;
									break;
									
								}
					
							}
					if (!modifyExist){
						conflict = true;
						break;
					}
				}					
			}
			if (!conflict){
				
				String input = new String();
				String startHour = Integer.toString(start.getHours());
				String startMinute = Integer.toString(start.getMinutes());
				String endHour = Integer.toString(end.getHours());
				String endMinute = Integer.toString(end.getMinutes());
				if (start.getHours()<10)					
					startHour = "0"+startHour;
				if (start.getMinutes()<10)
					startMinute = "0"+startMinute;
				if (end.getHours()<10)
					endHour = "0"+endHour;
				if (end.getMinutes()<10)
					endMinute = "0"+endMinute;

				input = startHour + ":" + startMinute + " - " + endHour + ":" + endMinute;
				schePack timePack = new schePack();
				timePack.TimeString = input;
				timePack.Timespan = new TimeSpan(start, end);
				mytime.put(StringCounter, timePack);

				StringCounter++;
			}
		}
		if (StringCounter==0)
			return null;
		else
			return mytime;
	}
	
}
