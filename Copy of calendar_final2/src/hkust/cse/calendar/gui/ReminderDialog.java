package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.Appt;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ReminderDialog extends JFrame implements ActionListener {	// By Kelvin
	
	private JLabel msgArea;
	private JButton exitBtn;
	
	public ReminderDialog( String msg) {		// Output the input message 
		paintDialog( msg);		
		setVisible( true);
		this.setSize(new Dimension(300,200));
	}
	
	public ReminderDialog( Appt appt) {			// Output the reminder information of the input Appt
		String msg = "The Appointment ";
		msg += appt.getTitle();
		msg += " will start ";
		msg += new Integer( hkust.cse.calendar.gui.Utility.timeDifferenceInMinute( appt.getReminder(), appt.TimeSpan().StartTime()) / 60).toString();
		msg += " Hours ";
		msg += new Integer( hkust.cse.calendar.gui.Utility.timeDifferenceInMinute( appt.getReminder(), appt.TimeSpan().StartTime()) % 60).toString();
		msg += " Minutes later";
		paintDialog( msg);
		setVisible( true);
		this.setSize(new Dimension(300,200));
	}
	
	private void paintDialog( String msg){
		
		Container content = getContentPane();
		setTitle("Reminder");
		
		JPanel panel = new JPanel();
		
		msgArea = new JLabel( msg);				// Create Label for the message
		msgArea.setHorizontalAlignment(SwingConstants.CENTER);

		panel.add(msgArea);

		exitBtn = new JButton("Exit");			// Create Exit button
		exitBtn.addActionListener(this);

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.CENTER));	// Set the layout of the panel

		p2.add(exitBtn);

		content.add("Center", panel);			// Add the Label and button to correct places
		content.add("South", p2);
		
		pack();									// Pack the elements together
	}
	
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == exitBtn) {
			dispose();
		}
	}
}
