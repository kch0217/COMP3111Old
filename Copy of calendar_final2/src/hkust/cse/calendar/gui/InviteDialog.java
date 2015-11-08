package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

class InviteDialog extends JFrame{
	/**
	 * 
	 */
	
	private CalGrid parent;
	private static final long serialVersionUID=1L;
	private UserStorageController userc=UserStorageController.getInstance();
	private JScrollPane left;
	private JScrollPane right;
	private JButton invite;
	private JButton remove;
	private JButton finish;
	private DefaultListModel<User> leftModel;
	private DefaultListModel<User> rightModel;
	
	//private User[] leftUser=userc.RetrieveAllUsers();
	//private User[] rightUser=new User[0];
	//private JList<User>lleft=new JList<User>(leftUser);
	//private JList<User>lright=new JList<User>(rightUser);
	//
	private User[] leftUser;
	private User[] rightUser;
	private JList<User> lleft;
	private JList<User> lright;
	private JPanel buttons;
	private boolean needDefault;
	
	private Appt appoint;
	private String currpros;
	
	public InviteDialog(CalGrid appGrid, final ApptStorageControllerImpl controller, Appt passAppt, boolean defaultValue, final int startTime, String process){
		
		setTitle("Invite Users");
		
		needDefault = defaultValue;
		parent = appGrid;
		currpros = process;
		this.setLayout(new BorderLayout());
		this.setLocationByPlatform(true);
		this.setSize(500,200);
		this.setLocationByPlatform(true);
		this.setVisible(true);
		
		invite= new JButton("invite");
		remove= new JButton("remove");
		finish= new JButton("finish");
		
		buttons= new JPanel();
		buttons.setLayout(new GridLayout(3,1,0,10));
		buttons.add(invite);
		buttons.add(remove);
		buttons.add(finish);
		
		//leftModel= new DefaultListModel<User>();
		//rightModel= new DefaultListModel<User>();
		
		//lleft=new JList<User>(leftModel);
		//lright=new JList<User>(rightModel);
//		if(controller==null){

//		}
//		int id=controller.getid();
//		System.out.print(id);
		//Appt appoint=controller.RetrieveById(controller.getid()-1);
		final Appt appoint = passAppt;
		
		UserStorageController usercon = hkust.cse.calendar.userstorage.UserStorageController.getInstance();
		
		
		
		User[] listUser = usercon.RetrieveAllUsers();
		if (listUser != null && listUser.length>0){
			if (appoint.getRemained()!= null&& appoint.getRemained().length>0){
				int counter1= 0;
				User[] remained = new User[listUser.length];
				for (int i = 0 ; i<listUser.length; i++){
					for (int j = 0; j < appoint.getRemained().length;j++){
						if ((appoint.getRemained()[j].getUsername().equals(listUser[i].getUsername()))){
							remained[counter1] = appoint.getRemained()[j];
							counter1++;
							break;
						}

					}

				}
				if (counter1 > 0)
					appoint.setRemained(Arrays.copyOf(remained, counter1));
				
//				System.out.println(appGrid.controller.getUserpending().size());
//				System.out.println(appoint.getRemained().length);
//				if (appGrid.controller.getUserpending().size()>0){
//					int counter2= 0;
//					User[] remained2 = new User[listUser.length];
//					for (int j = 0; j < appoint.getRemained().length;j++){
//						if (!appGrid.controller.getUserpending().contains(appoint.getRemained()[j].getUsername())){
//							remained2[counter2] = appoint.getRemained()[j];
//							counter2++;
//							break;
//						}
//					}
//					if (counter2 > 0)
//						appoint.setRemained(Arrays.copyOf(remained2, counter2));
//				}
			}
		}
		
		
	
		if (listUser != null && listUser.length>0){
			if (appoint.getInvited()!= null&& appoint.getInvited().length>0){
				int counter1= 0;
				User[] remained = new User[listUser.length];
				for (int i = 0 ; i<listUser.length; i++){
					for (int j = 0; j < appoint.getInvited().length;j++){
						if ((appoint.getInvited()[j].getUsername().equals(listUser[i].getUsername()))){
							remained[counter1] = appoint.getInvited()[j];
							counter1++;
							break;
						}

					}

				}
				if (counter1 > 0)
					appoint.setInvited(Arrays.copyOf(remained, counter1));
				
//				if (appGrid.controller.getUserpending().size()>0){
//					int counter2= 0;
//					User[] remained2 = new User[listUser.length];
//					for (int j = 0; j < appoint.getInvited().length;j++){
//						if (!appGrid.controller.getUserpending().contains(appoint.getInvited()[j].getUsername())){
//							remained2[counter2] = appoint.getInvited()[j];
//							counter2++;
//							break;
//						}
//					}
//					if (counter2 > 0)
//						appoint.setInvited(Arrays.copyOf(remained2, counter2));
//				}
			}
		}
		
		
		leftModel=new DefaultListModel<User>();
		if(appoint.getRemained().length!=0){
			for(int i=0;i<appoint.getRemained().length;i++){
				leftModel.addElement(appoint.getRemained()[i]);
			}
		}
		else{
			leftModel=new DefaultListModel<User>();
		}
		if(appoint.getInvited().length!=0){
			rightModel=new DefaultListModel<User>();
			for(int k=0;k<appoint.getInvited().length;k++){
				rightModel.addElement(appoint.getInvited()[k]);
			}
		}
		else{
			rightModel=new DefaultListModel<User>();
		}
		lleft=new JList<User>(leftModel);
		lright=new JList<User>(rightModel);
		//leftModel=(DefaultListModel<User>) lleft.getModel();
		//rightModel=(DefaultListModel<User>) lright.getModel();
		//
		left=new JScrollPane();		//to create the scrollpane
		left.setLayout(new ScrollPaneLayout());
		left.setViewportView(lleft);
		left.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		left.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		right=new JScrollPane();		//to create the scrollpane
		right.setLayout(new ScrollPaneLayout());
		right.setViewportView(lright);
		right.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		right.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		

		this.add(left, BorderLayout.WEST);
		this.add(buttons, BorderLayout.CENTER);
		this.add(right, BorderLayout.EAST);
		
		if(lright.getSelectedIndex()==-1){
			remove.setEnabled(false);
		}
		
		
		lleft.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if(e.getValueIsAdjusting()==false){
					if(lleft.getSelectedIndex()==-1){		//to ensure invite button is not enabled
						invite.setEnabled(false);		//when no items available
					}
					else{
						invite.setEnabled(true);
					}
				}
			}
			
		});
		
		lright.addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if(e.getValueIsAdjusting()==false){
					if(lright.getSelectedIndex()==-1){		//to ensure remove button is not enabled
						remove.setEnabled(false);		//when no items available
					}
					else{
						remove.setEnabled(true);
					}
				}
			}
			
		});
		
		invite.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(e.getSource().equals(invite)){
					int indexl=lleft.getSelectedIndex();
					int indexr=lright.getSelectedIndex();
					if(indexr==-1){
						indexr=0;
					}
					else{
						indexr++;
					}
//					boolean conflict=checkConflict(controller,leftModel.elementAt(indexl));
//					if(conflict!=true){
//						rightModel.add(indexr, leftModel.elementAt(indexl));
//						leftModel.remove(indexl);
//					}
//					else{
//						JOptionPane.showMessageDialog(null, "the user cannot be invited due to time conflict");
//					}

					if (leftModel.elementAt(indexl).getUsername().equals(parent.controller.getDefaultUser().getUsername())){

						JOptionPane.showMessageDialog(null, "You cannot invite yourself!");
					}
					else{
						rightModel.add(indexr, leftModel.elementAt(indexl));
						leftModel.remove(indexl);
					}
					
				}
				else{
					return;
				}
			}
			
		});
		
		remove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(e.getSource().equals(remove)){
					int indexl=lleft.getSelectedIndex();
					int indexr=lright.getSelectedIndex();
					if(indexl==-1){
						indexl=0;
					}
					else{
						indexl++;
					}
					leftModel.add(indexl, rightModel.getElementAt(indexr));
					rightModel.remove(indexr);
				}
			}
			
		});
		
		finish.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//Appt appt=controller.RetrieveById(controller.getid()-1);
				if(appoint.getDialog()==false){	//remaine=all invite=new User[0]
					//input the list of invitation to waiting
					LinkedList<String> temp=new LinkedList<String>();
					if(!rightModel.isEmpty()){
						for(int i=0;i<rightModel.size();i++){
							temp.add(rightModel.getElementAt(i).toString());							
						}
					}
					appoint.setWaitingList(temp);
					
					appoint.setDialog(true);
					
					//JOptionPane.showMessageDialog(null, "done");
					
				}
				else{
					//User[] rem=(User[])leftModel.toArray();
					User[] rem=new User[leftModel.size()];
					if(!leftModel.isEmpty()){
						for(int i=0;i<leftModel.getSize();i++){
							rem[i]=leftModel.elementAt(i);
						}
					}
					User[] apptremained=appoint.getRemained();
					ArrayList<User> removeinvite=new ArrayList<User>();
					ArrayList<User> newlyinvite=new ArrayList<User>();
					int counter1=0;
					int counter2=0;
		
					if(rem.length!=0 && apptremained.length!=0){	
						if(rem!=appoint.getRemained()){
							for(int i=0;i<rem.length;i++){
								counter1=0;
								for(int z=0;z<apptremained.length;z++){
									if(rem[i].toString().equals(apptremained[z].toString())==false){	//remove ele from lright
										counter1++;
									}
								}
								if(counter1==apptremained.length){	//t1 check rem not exist fts exist
									removeinvite.add(rem[i]);
								}
							}
							for(int a=0;a<apptremained.length;a++){		
								counter2=0;		//t2 check rem exist fts not exist
								for(int b=0;b<rem.length;b++){	//invite action
									if(apptremained[a].toString().equals(rem[b].toString())==false){
										counter2++;
									}
								}
								if(counter2==rem.length){
									newlyinvite.add(apptremained[a]);
								}
							}
						}
					}
					else if(rem.length==0 && apptremained.length!=0){
						int counter3=0;		//t3 some items to be invited
						User[] all=UserStorageController.getInstance().RetrieveAllUsers();
						for(int c=0;c<all.length;c++){
							counter3=0;
							for(int d=0;d<apptremained.length;d++){
								if(apptremained[d].toString().equals(all[c].toString())==false){
									counter3++;
								}
							}
							if(counter3==apptremained.length){
								newlyinvite.add(all[c]);
							}
						}
					}
					else if(rem.length!=0 &&apptremained.length==0){
						int counter4=0;
						User[] all=UserStorageController.getInstance().RetrieveAllUsers();
						for(int f=0;f<all.length;f++){
							counter4=0;
							for(int g=0;g<rem.length;g++){
								if(rem[g].toString().equals(all[f].toString())==false){
									counter4++;
								}
							}
							if(counter4==rem.length){
								removeinvite.add(all[f]);
							}
						}
					}
					//process the extra people to be invited/removed
					if(!removeinvite.isEmpty() ||!newlyinvite.isEmpty()){
						LinkedList<String>attend=appoint.getAttendList();
						LinkedList<String>reject=appoint.getRejectList();
						LinkedList<String>waiting=appoint.getWaitingList();
						int index=0;
						if(!removeinvite.isEmpty()){
							for(int y=0;y<removeinvite.size();y++){
								boolean attendhere=attend.contains(removeinvite.get(y).toString());
								boolean rejecthere=reject.contains(removeinvite.get(y).toString());
								boolean waitinghere=waiting.contains(removeinvite.get(y).toString());
								if(attendhere){
									attend.remove(removeinvite.get(y).toString());
								}
								if(rejecthere){
									reject.remove(removeinvite.get(y).toString());
								}
								if(waitinghere){
									waiting.remove(removeinvite.get(y).toString());
								}
							}
						}
						if(!newlyinvite.isEmpty()){
							for(int x=0;x<newlyinvite.size();x++){
								waiting.add(newlyinvite.get(x).toString());
							}
						}
						appoint.setAttendList(attend);
						appoint.setRejectList(reject);
						appoint.setWaitingList(waiting);
					}
					
					
				}
				User[] left=new User[leftModel.getSize()];
				User[] right=new User[rightModel.getSize()];
				if(!leftModel.isEmpty()){
					for(int t=0;t<leftModel.getSize();t++){
						left[t]=leftModel.elementAt(t);
					}
				}
				if(!rightModel.isEmpty()){
					for(int a=0;a<rightModel.getSize();a++){
						right[a]=rightModel.elementAt(a);
					}
				}
				appoint.setRemained(left);
				appoint.setInvited(right);
				InviteDialog.this.setVisible(false);
				
				if (currpros.equals("New")){
					AppScheduler a = new AppScheduler("New", parent, appoint);
					if (needDefault){
						a.updateSetApp(hkust.cse.calendar.gui.Utility.createDefaultAppt(
								parent.currentY, parent.currentM, parent.currentD,
								parent.mCurrUser, startTime), "New");
						a.setLocationRelativeTo(null);
						a.show();
					}
					else
					{
						
						a.updateSetApp(hkust.cse.calendar.gui.Utility
								.createDefaultAppt(parent.currentY, parent.currentM, parent.currentD,
										parent.mCurrUser), "New");
						a.setLocationRelativeTo(null);
						a.show();
						
					}
				}
				else if (currpros.equals("Modify")){
					AppScheduler setAppDial = new AppScheduler("Modify", parent, appoint.getID());
					
					setAppDial.updateSetApp(appoint, "Modify");
					
					setAppDial.show();
					setAppDial.setResizable(false);
				}
			}
			
		});
		if (appoint.getAttendList().size()>0){
			for (int i = 0; i< appoint.getAttendList().size();i++){
				JOptionPane.showMessageDialog(null, appoint.getAttendList().get(i)+ " has agreed to attend!");
			}
		}
		if (appoint.getRejectList().size()>0 ||appoint.getWaitingList().size()>0){
			if (appoint.getAttendList().size()>0){
				for (int i = 0; i< appoint.getAttendList().size();i++){
					JOptionPane.showMessageDialog(null, appoint.getAttendList().get(i)+ " has agreed to attend!");
				}
			}
			for (int i = 0; i< appoint.getRejectList().size();i++){
				JOptionPane.showMessageDialog(null, appoint.getRejectList().get(i)+ " has declined to attend!");
			}
		}

	}
	public static boolean checkConflict(ApptStorageControllerImpl controller,User user, Appt app){
		//Appt app=controller.RetrieveById(controller.getid()-1);	//groupevent
		int newApptDuration=( app.TimeSpan().EndTime().getHours() - app.TimeSpan().StartTime().getHours())*60 
				+ ( app.TimeSpan().EndTime().getMinutes() - app.TimeSpan().StartTime().getMinutes());
		
		Appt[] checkappt=getUserDayAppt(app.TimeSpan().StartTime(),user,controller);			//for once

		int freq=app.getFrequency();
		if(freq==AppScheduler.ONCE){
			if(checkappt==null){
				return false;
			}
			for(int i=0;i<checkappt.length;i++){
				Timestamp apptStart=checkappt[i].TimeSpan().StartTime();
				Timestamp apptEnd=checkappt[i].TimeSpan().EndTime();
				int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 
						+ ( apptEnd.getMinutes() - apptStart.getMinutes());
				int durationBetweenStarts =  ( apptStart.getHours() - app.TimeSpan().StartTime().getHours())*60 + 
						( apptStart.getMinutes() - app.TimeSpan().StartTime().getMinutes());
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
		else{
			checkappt=getAllUserAppt(user,controller);
			if(checkappt==null){
				return false;
			}
			if(freq==AppScheduler.DAILY){
				for(int i=0;i<checkappt.length;i++){
					Timestamp apptStart = checkappt[i].TimeSpan().StartTime();
					Timestamp apptEnd = checkappt[i].TimeSpan().EndTime();
					int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 +
							( apptEnd.getMinutes() - apptStart.getMinutes());
					int durationBetweenStarts =  ( apptStart.getHours() - app.TimeSpan().StartTime().getHours())*60 
							+ ( apptStart.getMinutes() - app.TimeSpan().StartTime().getMinutes());
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
			else if(freq==AppScheduler.WEEKLY){
				for(int i=0;i<checkappt.length;i++){
					Timestamp apptStart = checkappt[i].TimeSpan().StartTime();
					if( apptStart.getDay() == app.TimeSpan().StartTime().getDay() || checkappt[i].getFrequency() == AppScheduler.DAILY || checkappt[i].getFrequency() == AppScheduler.MONTHLY){	
						Timestamp apptEnd = checkappt[i].TimeSpan().EndTime();
						
						int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 
								+ ( apptEnd.getMinutes() - apptStart.getMinutes());
						int durationBetweenStarts =  ( apptStart.getHours() - app.TimeSpan().StartTime().getHours())*60 
								+ ( apptStart.getMinutes() - app.TimeSpan().StartTime().getMinutes());
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
			else{
				for(int i=0;i<checkappt.length;i++){
					Timestamp apptStart = checkappt[i].TimeSpan().StartTime();
					if( apptStart.getDate() == app.TimeSpan().StartTime().getDate() || checkappt[i].getFrequency() == AppScheduler.DAILY || checkappt[i].getFrequency() == AppScheduler.WEEKLY){
						Timestamp apptEnd = checkappt[i].TimeSpan().EndTime();
						int thisApptDuration = ( apptEnd.getHours() - apptStart.getHours())*60 
								+ ( apptEnd.getMinutes() - apptStart.getMinutes());
						int durationBetweenStarts =  ( apptStart.getHours() - app.TimeSpan().StartTime().getHours())*60 
								+ ( apptStart.getMinutes() - app.TimeSpan().StartTime().getMinutes());
						
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
	
	private static Appt[] getUserDayAppt(Timestamp thisDay, User user,ApptStorageControllerImpl controller){
			// Get The Appts of a specific day 
			Timestamp start = new Timestamp(0);				
			start.setYear(thisDay.getYear());
			start.setMonth(thisDay.getMonth());
			start.setDate(thisDay.getDate());
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			
			Timestamp end = new Timestamp(0);
			end.setYear(thisDay.getYear());
			end.setMonth(thisDay.getMonth());
			end.setDate(thisDay.getDate());
			end.setHours(23);
			end.setMinutes(59);
			end.setSeconds(59);
			
			TimeSpan period = new TimeSpan(start, end);
			return controller.RetrieveAppts(user, period);
		
	}
	
	private static Appt[] getAllUserAppt(User user,ApptStorageControllerImpl controller){
		Appt[] all=controller.RetrieveAllAppts();
		ArrayList<Appt> userappt=new ArrayList<Appt>();
		int index=0;
		boolean duplicated=false;
		Appt[] part;
		if(all!=null){
			for(int i=0;i<all.length;i++){
				TimeSpan apptdur=all[i].TimeSpan();
				part=controller.RetrieveAppts(user, apptdur);
				if(part!=null){
					for(int k=0;k<part.length;k++){
						duplicated=false;
						for(int z=0;z<userappt.size();z++){
							if(part[k].equals(userappt.get(z)))
								duplicated=true;
						}
						if(duplicated!=true){
							userappt.add(part[k]);
							index++;
						}
					}
				}
			}
		}
		if(userappt.isEmpty()!=true){
			Appt[] us=new Appt[userappt.size()];
			return userappt.toArray(us);
		}
		return null;
		
	}

}