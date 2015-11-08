package hkust.cse.calendar.gui;


import hkust.cse.calendar.unit.TimeMachine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TimeDisplayDialog extends JFrame {
	private JLabel currentTime;
	private JLabel LYear;
	private JLabel LMonth;
	private JLabel LDate;
	private JLabel LHour;
	private JLabel LMinute;
	private JLabel LSecond;

	private JTextField setYear;
	private JTextField setMonth;
	private JTextField setDate;
	private JTextField setHour;
	private JTextField setMinute;
	private JTextField setSecond;
	
	private JButton SetTime;
	
	public TimeDisplayDialog(){		
		
		setTitle("Time Machine Management");
		Container contentpane = getContentPane();
		contentpane.setPreferredSize(new Dimension(500,150));
		setLocationByPlatform(true);
		
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		
		currentTime = new JLabel("Loading...");
		currentTime.setFont(new Font(currentTime.getFont().getName(),Font.PLAIN,30));
		top.add(currentTime);
		contentpane.add(top, BorderLayout.NORTH);
		
		JPanel center = new JPanel();
		center.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		LYear = new JLabel("Year: ");
		setYear = new JTextField(4);
		setYear.setText(Integer.toString(TimeMachine.outputYear()));
		LMonth = new JLabel("Month: ");
		setMonth = new JTextField(2);
		setMonth.setText(Integer.toString(TimeMachine.outputMonth()));
		LDate = new JLabel("Date: ");
		setDate = new JTextField(2);
		setDate.setText(Integer.toString(TimeMachine.outputDay()));
		LHour = new JLabel("Hour: ");
		setHour = new JTextField(2);
		setHour.setText(Integer.toString(TimeMachine.outputHour()));
		LMinute = new JLabel("Minute: ");
		setMinute = new JTextField(2);
		setMinute.setText(Integer.toString(TimeMachine.outputMinute()));
		LSecond = new JLabel("Second: ");
		setSecond = new JTextField(2);
		setSecond.setText(Integer.toString(TimeMachine.outputSecond()));
		
		SetTime = new JButton("Set");
		
		center.add(LYear);
		center.add(setYear);
		center.add(LMonth);
		center.add(setMonth);
		center.add(LDate);
		center.add(setDate);
		center.add(LHour);
		center.add(setHour);
		center.add(LMinute);
		center.add(setMinute);
		center.add(LSecond);
		center.add(setSecond);
		
		
		contentpane.add(center, BorderLayout.CENTER);
		contentpane.add(SetTime, BorderLayout.SOUTH);
		
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){ //update the timer in GUI
			public void run(){
				TimeMachine mytime = TimeMachine.getInstance(null);
				currentTime.setText(mytime.outputDay()+"/"+mytime.months[mytime.outputMonth()-1]+"/"+mytime.outputYear()+"   "
				+mytime.outputHour()+":"+mytime.outputMinute()+":"+mytime.outputSecond()+"   "+mytime.weekdays[mytime.outputWeekday()]);
			}
		}, 500, 1000);
		
		SetTime.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				TimeMachine.setTimeMac(Integer.parseInt(setYear.getText()), Integer.parseInt(setMonth.getText()), Integer.parseInt(setDate.getText()), 
						Integer.parseInt(setHour.getText()), Integer.parseInt(setMinute.getText()), Integer.parseInt(setSecond.getText()));
			}
			
		});
		
		setContentPane(contentpane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}
	

	
	
	
}
