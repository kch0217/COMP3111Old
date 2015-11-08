package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.Appt;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class GroupConfirmationDialog extends JFrame implements ActionListener { //by Hin
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource()==Details){ //if the users click details button
			DetailsDialog detailDia = new DetailsDialog(currentAppt,"Appointment in pending");
			detailDia.setVisible(true);
			
		}
		else if (e.getSource() == Confirm){ //if the users click confirm
			currentAppt.getWaitingList().remove(parent.controller.getDefaultUser().getUsername());
			currentAppt.getAttendList().addLast(parent.controller.getDefaultUser().getUsername());
			parent.controller.save();
			parent.controller.refreshState();
			this.setVisible(false);
			parent.setEnabled(true);
			parent.UpdateCal();
			parent.repaint();
		}
		else if (e.getSource()==Reject){ //if the users click reject
			currentAppt.getWaitingList().remove(parent.controller.getDefaultUser().getUsername());
			currentAppt.getRejectList().addLast(parent.controller.getDefaultUser().getUsername());
			parent.controller.save();
			parent.controller.refreshState();
			this.setVisible(false);
			parent.setEnabled(true);
			parent.UpdateCal();
			parent.repaint();
		}
			
	}

	private JLabel Information;
	
	private JButton Details;
	private JButton Confirm;
	private JButton Reject;
	
	private CalGrid parent;
	
	private Appt currentAppt;
	
	public GroupConfirmationDialog(CalGrid parent, Appt appt){
		
		this.parent = parent;
		currentAppt = appt;
		
		parent.setEnabled(false);
		setTitle("Confirmation For Group Event");
		Container contentpane = getContentPane();
		contentpane.setPreferredSize(new Dimension(500,150));
		setLocationByPlatform(true);
		
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		Information = new JLabel("Do you want to attend "+appt.getTitle()+"?");
		
		Information.setFont(new Font(Information.getFont().getName(),Font.PLAIN,30));
		
		top.add(Information);
		contentpane.add(top, BorderLayout.NORTH);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		Details = new JButton("Details");
		
		Confirm = new JButton("Confirm");
		
		Reject = new JButton("Reject");
		
		bottom.add(Details);
		bottom.add(Confirm);
		bottom.add(Reject);
		
		Details.addActionListener(this);
		Confirm.addActionListener(this);
		Reject.addActionListener(this);
		
		contentpane.add(bottom, BorderLayout.SOUTH);
		
		setContentPane(contentpane);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //not allowed to close
		pack();
		setVisible(true);
	}
	
}
