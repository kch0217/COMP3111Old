package hkust.cse.calendar.gui;
import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;


import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Location;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.*;

public class LocationDialog extends JFrame{
	private static final long serialVersionUID=1L;
	private ApptStorageControllerImpl _controller;
	
	private DefaultListModel<Location> listModel;
	private JList<Location> list;
	private JTextField locNameText;
	private JScrollPane lis;
	private JPanel buttons;
	private JButton remove;
	private JButton add;
	private JScrollBar listbar;
	private JButton modify;
	public LocationDialog(final ApptStorageControllerImpl controller){
		_controller=controller;
		this.setLayout(new BorderLayout());
		this.setLocationByPlatform(true);
		this.setSize(400, 200);
		
		remove=new JButton("remove");
		add=new JButton("add");
		modify=new JButton("modify");
		locNameText=new JTextField(10);
		buttons=new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttons.add(locNameText);
		buttons.add(add);
		buttons.add(remove);
		buttons.add(modify);

		
		listModel = new DefaultListModel<Location>();
		LocationCellRenderer dlr=new LocationCellRenderer();
		Location []loc=_controller.getLocationList();
		for(int i=0;i<loc.length;i++){
			listModel.addElement(loc[i]);
		}
		list=new JList<Location>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.setCellRenderer(dlr);	//to ensure the list displays the _name of the location
		list.setVisibleRowCount(5);
		
		lis=new JScrollPane();		//to create the scrollpane
		lis.setLayout(new ScrollPaneLayout());
		lis.setViewportView(list);
		lis.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		lis.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		
		list.addListSelectionListener(new ListSelectionListener(){	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if(e.getValueIsAdjusting()==false){
					if(list.getSelectedIndex()==-1){		//to ensure remove button is not enabled
						remove.setEnabled(false);		//when no items available
					}
					else{
						remove.setEnabled(true);
					}
				}
			}
			
			
		});
		
		modify.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				detailbox dtb=new detailbox(_controller);
			}
			
		});
		
		remove.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int index=list.getSelectedIndex();
				boolean isPresent=false;
				if( listModel.getElementAt(index).getMyState() == true){
					int n = JOptionPane.showOptionDialog(null, "Are You Sure to Delete This Location " + listModel.getElementAt(index).getName()
							+ " ?\n You CANNOT re-do this action","Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
					if (n == JOptionPane.YES_OPTION){
						listModel.getElementAt(index).setMyState(false);
						LinkedList<String> notifyList = controller.RemoveLocationAndNotifyTheInitializer(listModel.getElementAt(index));
						if( notifyList == null){
							controller.removeTargetLocation( listModel.getElementAt(index));
							listModel.remove(index);
						}
						else
							listModel.getElementAt(index).setMyNotifier( notifyList);
						list.repaint();
					}
				}
				else{
					ShowErrorMessage("You cannot delete a Deleted Location !");
				}
//				Appt []cases=_controller.RetrieveAllAppts();
//				if(cases!=null){	//if there exists appointments
//					for(int i=0;i<cases.length;i++){	//make sure the locations used would not be deleted
//						if(listModel.getElementAt(index).getName().equals(cases[i].getLocation().getName())){
//							isPresent=true;
//						}
//					}
//				}
//				if(listModel.getElementAt(index).getName().equals("  ") ||isPresent){
//					JOptionPane.showMessageDialog(null, "The location cannot be deleted");  
//					return;
//				}
//				listModel.remove(index);
				
				int size=listModel.getSize();
				if(size==0){
					remove.setEnabled(false);
				}
				else{
					if(index==listModel.getSize()){
						index--;
					}
					list.setSelectedIndex(index);
					list.ensureIndexIsVisible(index);
				}

			}
			
			
		});
		
		add.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String name=locNameText.getText();
				int index=-1;
				int selindex;
				for(int i=0;i<listModel.getSize();i++){
					Location test=listModel.getElementAt(i);
					if(test.getName().equals(name)){		//avoid the presence of repeated location
						index=i;
					}
				}
				
				if(name.equals("")	|| index!=-1){
					Toolkit.getDefaultToolkit().beep();		//if the user tries to add blank location
					locNameText.requestFocusInWindow();		//or repeated location, stop it.
					locNameText.selectAll();
					return;
				}
				selindex=list.getSelectedIndex();
				if(selindex==-1){
					selindex=0;
				}
				else{
					selindex++;
				}
				listModel.add(selindex, new Location(name));
				locNameText.requestFocusInWindow();
				locNameText.setText("");
				list.setSelectedIndex(selindex);
				list.ensureIndexIsVisible(selindex);
			}
			
		});
		this.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				//Location[] locate=(Location[])listModel.toArray();
				//_controller.setLocationList(locate);
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				Location[] locate=new Location[listModel.getSize()];	//when the user closes the
				for(int i=0;i<listModel.getSize();i++){		//window using the cross at the right top corner
					locate[i]=listModel.elementAt(i);		//the list of location is saved
				}
				_controller.setLocationList(locate);
				_controller.updateLocation();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}
			
		});
		
		this.add(lis, BorderLayout.NORTH);
		this.add(buttons, BorderLayout.SOUTH);
		this.setVisible(true);
	}
	class detailbox extends JFrame{
		private JLabel locationL;
		private JTextField locationT;
		private JButton ok;
		private JTextField capacityT;
		private JLabel capacityL;
		public detailbox(final ApptStorageControllerImpl controller){
			this.setLayout(new BorderLayout());
			JPanel location=new JPanel();
			JPanel capacityok=new JPanel();
			ok=new JButton("ok");
			capacityT=new JTextField(10);
			capacityL=new JLabel("set the capacity");
			locationL=new JLabel("location name");
			locationT=new JTextField(10);
			location.add(locationL);
			location.add(locationT);
			capacityok.add(capacityL);
			capacityok.add(capacityT);
			capacityok.add(ok);
			this.add(location, BorderLayout.NORTH);
			this.add(capacityok, BorderLayout.CENTER);
			int index=list.getSelectedIndex();
			
			
			if(index!=-1){
				Location test=list.getSelectedValue();
				locationT.setText(test.getName());
				capacityT.setText(Integer.toString(list.getSelectedValue().getCapacity()));
				Appt[] used=controller.RetrieveApptsbyLocation(test);
				if(used!=null){
					capacityT.setEditable(false);
				}
			}
			ok.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					String name=locationT.getText();
					boolean present=false;
					Appt[] locused=controller.RetrieveApptsbyLocation(list.getSelectedValue());
					for(int i=0;i<listModel.size();i++){
						Location test=listModel.getElementAt(i);
						if(test.getName().equals(name) &&!test.equals(list.getSelectedValue())){		//avoid the presence of repeated location
							present=true;
							break;
						}
					}
					if(present){
						JOptionPane.showMessageDialog(null, "location name has been used");
						return;
					}
					else if(name.equals("")){
						JOptionPane.showMessageDialog(null, "please fill in the location name");
						return;
					}
					try{
						list.getSelectedValue().setName(name);
						list.getSelectedValue().setCapacity(Integer.parseInt(capacityT.getText()));
						if(locused!=null){
							for(int i=0;i<locused.length;i++){
								locused[i].setLocation(list.getSelectedValue());
							}
						}
					}
					catch(Exception ex){
						JOptionPane.showMessageDialog(null, "capacity in wrong format");
						return;
					}
					dispose();
				}
				
			});
			
			this.setLocationByPlatform(true);
			this.setSize(300, 200);
			this.setVisible(true);
			
		}
	}
	
	private void ShowErrorMessage( String msg){
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
