package hkust.cse.calendar.gui;

import hkust.cse.calendar.Main.CalendarMain;
import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.Reminder;
import hkust.cse.calendar.unit.TimeMachine;
import hkust.cse.calendar.unit.TimeSpan;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.metal.MetalBorders.Flush3DBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class CalGrid extends JFrame implements ActionListener {

	// private User mNewUser;
	private static final long serialVersionUID = 1L;
	public ApptStorageControllerImpl controller;
	public User mCurrUser;
	private String mCurrTitle = "Desktop Calendar - No User - ";
	private GregorianCalendar today;
	public int currentD;
	public int currentM;
	public int currentY;
	public int previousRow;
	public int previousCol;
	public int currentRow;
	public int currentCol;
	private BasicArrowButton eButton;
	private BasicArrowButton wButton;
	private JLabel year;
	private JLabel currentTime;
	private JComboBox month;
	private final Object[][] data = new Object[6][7];
//	private final Vector<Appt>[][] apptMarker = new Vector[6][7];
	private boolean[][] hasAppt = new boolean[6][7];
	private final String[] names = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	private final String[] months = { "January", "Feburary", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };
	private JTable tableView;
	private AppList applist;
	public static final int[] monthDays = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
			31, 30, 31 };
	private JTextPane note;

	private JSplitPane upper;
	private JSplitPane whole;
	private JScrollPane scrollpane;
	private StyledDocument mem_doc = null;
	private SimpleAttributeSet sab = null;
	// private boolean isLogin = false;
	private JMenu Appmenu = new JMenu("Appointment");

	private final String[] holidays = {
			"New Years Day\nSpring Festival\n",
			"President's Day (US)\n",
			"",
			"Ching Ming Festival\nGood Friday\nThe day following Good Friday\nEaster Monday\n",
			"Labour Day\nThe Buddhaþý™s Birthday\nTuen Ng Festival\n",
			"",
			"Hong Kong Special Administrative Region Establishment Day\n",
			"Civic Holiday(CAN)\n",
			"",
			"National Day\nChinese Mid-Autumn Festival\nChung Yeung Festival\nThanksgiving Day\n",
			"Veterans Day(US)\nThanksgiving Day(US)\n", "Christmas\n" };

	private AppScheduler setAppDial;
	
	public TimeMachine timeCounter; //added by Hin
	private Reminder scheduleRemainder;
	
	private boolean[][] checkDate = new boolean[6][7];
	private String[][] weatherOption = new String[6][7];
	BufferedImage[] images = loadImages();  
    final BufferedImage[] scaled = getScaledImages(images, 0);  

	public CalGrid(ApptStorageControllerImpl con) {
		super();
		
		timeCounter = TimeMachine.getInstance(this); //set up the time machine

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				controller.save();
				System.exit(0);
			}
		});
		controller = con;
		mCurrUser = null;

		previousRow = 0;
		previousCol = 0;
		currentRow = 0;
		currentCol = 0;

		if( con.getDefaultUser().getMyState() == User.ADMIN)
			paintAdminGrid();
		else
			paintUserGrid();
		

		
	}
	
	private void paintAdminGrid(){
		
		applist = new AppList();
		applist.setParent(this);

		setJMenuBar(createAdminMenuBar());

		today = new GregorianCalendar();
		
//		currentY = today.get(Calendar.YEAR);
//		currentD = today.get(today.DAY_OF_MONTH);
//		int temp = today.get(today.MONTH) + 1;
				
		
		//Modified by Hin


		currentY = timeCounter.outputYear(); //use the Time Machine to get the time
		currentD = timeCounter.outputDay();
		currentM = timeCounter.outputMonth();
		int temp = timeCounter.outputMonth();
		

		getDateArray(data);

		JPanel leftP = new JPanel();
		leftP.setLayout(new BorderLayout());
		leftP.setPreferredSize(new Dimension(500, 300));

		JLabel textL = new JLabel("Important Days");
		textL.setForeground(Color.red);

		note = new JTextPane();
		note.setEditable(false);
		note.setBorder(new Flush3DBorder());
		mem_doc = note.getStyledDocument();
		sab = new SimpleAttributeSet();
		StyleConstants.setBold(sab, true);
		StyleConstants.setFontSize(sab, 30);

		JPanel noteP = new JPanel();
		noteP.setLayout(new BorderLayout());
		noteP.add(textL, BorderLayout.NORTH);
		noteP.add(note, BorderLayout.CENTER);

		leftP.add(noteP, BorderLayout.CENTER);

		eButton = new BasicArrowButton(SwingConstants.EAST);
		eButton.setEnabled(true);
		eButton.addActionListener(this);
		wButton = new BasicArrowButton(SwingConstants.WEST);
		wButton.setEnabled(true);
		wButton.addActionListener(this);

		year = new JLabel(new Integer(currentY).toString());
		month = new JComboBox();
		month.addActionListener(this);
		month.setPreferredSize(new Dimension(200, 30));
		for (int cnt = 0; cnt < 12; cnt++)
			month.addItem(months[cnt]);
		month.setSelectedIndex(temp - 1);

		JPanel yearGroup = new JPanel();
		yearGroup.setLayout(new FlowLayout());
		yearGroup.setBorder(new Flush3DBorder());
		yearGroup.add(wButton);
		yearGroup.add(year);
		yearGroup.add(eButton);
		yearGroup.add(month);

		leftP.add(yearGroup, BorderLayout.NORTH);
		


		TableModel dataModel = prepareTableModel();
		
		tableView = new JTable(dataModel) {
			public TableCellRenderer getCellRenderer(int row, int col) {
				String tem = (String) data[row][col];
//				Boolean hasAppt = ! (apptMarker[row][col].isEmpty());	// Check if this day have Appt
				
				if (tem.equals("") == false) {
					try {
						if (timeCounter.outputYear() == currentY
								&& timeCounter.outputMonth() == currentM
								&& timeCounter.outputDay() == Integer
										.parseInt(tem)) {
							return new CalCellRenderer(today, hasAppt[row][col], checkDate[row][col], weatherOption[row][col],scaled);
						}
					} catch (Throwable e) {
						System.exit(1);
					}

				}
				return new CalCellRenderer(null, hasAppt[row][col], checkDate[row][col],weatherOption[row][col],scaled);
			}
		};

		tableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableView.setRowHeight(40);
		tableView.setRowSelectionAllowed(false);
		tableView.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				mousePressResponse();
			}

			public void mouseReleased(MouseEvent e) {
				mouseResponse();
			}
		});

		JTableHeader head = tableView.getTableHeader();
		head.setReorderingAllowed(false);
		head.setResizingAllowed(true);

		scrollpane = new JScrollPane(tableView);
		scrollpane.setBorder(new BevelBorder(BevelBorder.RAISED));
		scrollpane.setPreferredSize(new Dimension(536, 260));

		upper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftP, scrollpane);

		whole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, applist);
		getContentPane().add(whole);

		
		initializeSystem(); // for you to add.
		//mCurrUser = getCurrUser(); // totally meaningless code
		Appmenu.setEnabled(true);

		UpdateCal();
		scheduleRemainder = new Reminder(this, timeCounter);
		pack();				// sized the window to a preferred size
		setVisible(true);	//set the window to be visible
		loginMessage();
		

	}
	
	private void paintUserGrid(){
		
		applist = new AppList();
		applist.setParent(this);

		setJMenuBar(createUserMenuBar());

		today = new GregorianCalendar();
		
//		currentY = today.get(Calendar.YEAR);
//		currentD = today.get(today.DAY_OF_MONTH);
//		int temp = today.get(today.MONTH) + 1;
				
		
		//Modified by Hin


		currentY = timeCounter.outputYear(); //use the Time Machine to get the time
		currentD = timeCounter.outputDay();
		currentM = timeCounter.outputMonth();
		int temp = timeCounter.outputMonth();
		

		getDateArray(data);

		JPanel leftP = new JPanel();
		leftP.setLayout(new BorderLayout());
		leftP.setPreferredSize(new Dimension(500, 300));

		JLabel textL = new JLabel("Important Days");
		textL.setForeground(Color.red);

		note = new JTextPane();
		note.setEditable(false);
		note.setBorder(new Flush3DBorder());
		mem_doc = note.getStyledDocument();
		sab = new SimpleAttributeSet();
		StyleConstants.setBold(sab, true);
		StyleConstants.setFontSize(sab, 30);

		JPanel noteP = new JPanel();
		noteP.setLayout(new BorderLayout());
		noteP.add(textL, BorderLayout.NORTH);
		noteP.add(note, BorderLayout.CENTER);

		leftP.add(noteP, BorderLayout.CENTER);

		eButton = new BasicArrowButton(SwingConstants.EAST);
		eButton.setEnabled(true);
		eButton.addActionListener(this);
		wButton = new BasicArrowButton(SwingConstants.WEST);
		wButton.setEnabled(true);
		wButton.addActionListener(this);

		year = new JLabel(new Integer(currentY).toString());
		month = new JComboBox();
		month.addActionListener(this);
		month.setPreferredSize(new Dimension(200, 30));
		for (int cnt = 0; cnt < 12; cnt++)
			month.addItem(months[cnt]);
		month.setSelectedIndex(temp - 1);

		JPanel yearGroup = new JPanel();
		yearGroup.setLayout(new FlowLayout());
		yearGroup.setBorder(new Flush3DBorder());
		yearGroup.add(wButton);
		yearGroup.add(year);
		yearGroup.add(eButton);
		yearGroup.add(month);

		leftP.add(yearGroup, BorderLayout.NORTH);
		


		TableModel dataModel = prepareTableModel();
		
		tableView = new JTable(dataModel) {
			public TableCellRenderer getCellRenderer(int row, int col) {
				String tem = (String) data[row][col];
//				Boolean hasAppt = ! (apptMarker[row][col].isEmpty());	// Check if this day have Appt
				
				if (tem.equals("") == false) {
					try {
						if (timeCounter.outputYear() == currentY
								&& timeCounter.outputMonth() == currentM
								&& timeCounter.outputDay() == Integer
										.parseInt(tem)) {
							return new CalCellRenderer(today, hasAppt[row][col],checkDate[row][col],weatherOption[row][col], scaled);
						}
					} catch (Throwable e) {
						System.exit(1);
					}

				}
				return new CalCellRenderer(null, hasAppt[row][col],checkDate[row][col],weatherOption[row][col], scaled);
			}
		};

		tableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableView.setRowHeight(40);
		tableView.setRowSelectionAllowed(false);
		tableView.addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				mousePressResponse();
			}

			public void mouseReleased(MouseEvent e) {
				mouseResponse();
			}
		});

		JTableHeader head = tableView.getTableHeader();
		head.setReorderingAllowed(false);
		head.setResizingAllowed(true);

		scrollpane = new JScrollPane(tableView);
		scrollpane.setBorder(new BevelBorder(BevelBorder.RAISED));
		scrollpane.setPreferredSize(new Dimension(536, 260));

		upper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftP, scrollpane);

		whole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upper, applist);
		getContentPane().add(whole);

		
		initializeSystem(); // for you to add.
		//mCurrUser = getCurrUser(); // totally meaningless code
		Appmenu.setEnabled(true);

		UpdateCal();
		scheduleRemainder = new Reminder(this, timeCounter);
		pack();				// sized the window to a preferred size
		setVisible(true);	//set the window to be visible
		loginMessage();
	}

	public TableModel prepareTableModel() {

		TableModel dataModel = new AbstractTableModel() {

			public int getColumnCount() {
				return names.length;
			}

			public int getRowCount() {
				return 6;
			}

			public Object getValueAt(int row, int col) {
				return data[row][col];
			}

			public String getColumnName(int column) {
				return names[column];
			}

			public Class getColumnClass(int c) {
				return getValueAt(0, c).getClass();
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public void setValueAt(Object aValue, int row, int column) {
				System.out.println("Setting value to: " + aValue);
				data[row][column] = aValue;
			}
		};
		return dataModel;
	}

	public void getDateArray(Object[][] data) {
		GregorianCalendar c = new GregorianCalendar(currentY, currentM - 1, 1);
		int day;
		int date;
		Date d = c.getTime();
		c.setTime(d);
		day = d.getDay();
		date = d.getDate();

		if (c.isLeapYear(currentY)) {

			monthDays[1] = 29;
		} else
			monthDays[1] = 28;

		int temp = day - date % 7;
		if (temp > 0)
			day = temp + 1;
		else if (temp < 0)
			day = temp + 1 + 7;
		else
			day = date % 7;
		day %= 7;
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 7; j++) {
				int temp1 = i * 7 + j - day + 1;
				if (temp1 > 0 && temp1 <= monthDays[currentM - 1])
					data[i][j] = new Integer(temp1).toString();
				else
					data[i][j] = new String("");
			}

	}

	JMenuBar createAdminMenuBar() {

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Manual Scheduling")) {
					Appt newAppt = new Appt();
					newAppt.setID(-1);
					InviteDialog inviteDialog=new InviteDialog(CalGrid.this, controller, newAppt, false, -1, "New");
					
					
					TableModel t = prepareTableModel();
					tableView.setModel(t);
					tableView.repaint();
				}

			}
		};
		
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.getAccessibleContext().setAccessibleName("Calendar Choices");
		JMenuItem mi;

		currentTime = (JLabel) menuBar.add( new JLabel());
		currentTime.setText("Loading... ");
		menuBar.add(currentTime);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){ //update the timer in GUI
			public void run(){
				String Hour;
				String Minute;
				String Second;
				int hour = TimeMachine.outputHour();
				int minute = TimeMachine.outputMinute();
				int second = TimeMachine.outputSecond();
				if( hour < 10)
					Hour = "0" + String.valueOf(hour);
				else
					Hour = String.valueOf(hour);
				if( minute < 10)
					Minute = "0" + String.valueOf(minute);
				else
					Minute = String.valueOf(minute);
				if( second < 10)
					Second = "0" + String.valueOf(second);
				else
					Second = String.valueOf(second);
				currentTime.setText("    " + Hour+":"+Minute+":"+Second+"    ");
			}
		}, 500, 1000);
		
		JMenu Access = (JMenu) menuBar.add(new JMenu("Access"));
		Access.setMnemonic('A');
		Access.getAccessibleContext().setAccessibleDescription(
				"Account Access Management");

		mi = (JMenuItem) Access.add(new JMenuItem("Logout"));	//adding a Logout menu button for user to logout
		mi.setMnemonic('L');
		mi.getAccessibleContext().setAccessibleDescription("For user logout");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Logout?",
						"Comfirm", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION){
					//controller.dumpStorageToFile();
					//System.out.println("closed");
					controller.save();
					dispose();
					CalendarMain.logOut = true;
					return;	//return to CalendarMain()
				}
			}
		});
		
		mi = (JMenuItem) Access.add(new JMenuItem("Exit"));
		mi.setMnemonic('E');
		mi.getAccessibleContext().setAccessibleDescription("Exit Program");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Exit Program ?",
						"Comfirm", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION){
					controller.save();
					System.exit(0);
				}

			}
		});

		menuBar.add(Appmenu);
		Appmenu.setEnabled(false);
		Appmenu.setMnemonic('p');
		Appmenu.getAccessibleContext().setAccessibleDescription(
				"Appointment Management");
		mi = new JMenuItem("Manual Scheduling");
		mi.addActionListener(listener);
		Appmenu.add(mi);
		
		
		mi = new JMenuItem("Manage Locations"); //by Ken
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				LocationDialog dlg= new LocationDialog(controller);
			}

		});
		Appmenu.add(mi);
		

		JMenu SetTimeMac = new JMenu("Time Machine");
		menuBar.add(SetTimeMac);
		SetTimeMac.setMnemonic('T');
		SetTimeMac.getAccessibleContext().setAccessibleDescription("Time Machine Management");
		
		mi = new JMenuItem("Manager Time");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				TimeDisplayDialog myTimeMac = new TimeDisplayDialog(); 
			}
			
		});
		SetTimeMac.add(mi);
			
		JMenu Usermenu = new JMenu("User");
		menuBar.add(Usermenu);
		Usermenu.setMnemonic('U');
		Usermenu.getAccessibleContext().setAccessibleDescription("User Management");

		mi = new JMenuItem("Manager User");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				UserDialog myUser = new UserDialog( mCurrUser);
			}

		});
		Usermenu.add(mi);
		
		mi = new JMenuItem("Show User Privileges");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				mCurrUser.ShowUserRight();
			}

		});
		Usermenu.add(mi);
		
		//
		JMenu tools=new JMenu("Tools");
		menuBar.add(tools);
		mi=new JMenuItem("Weather information");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				TemperatureDialog tpd=new TemperatureDialog(controller);
			}
			
		});
		tools.add(mi);
		
	
				
		return menuBar;
	}
	
	JMenuBar createUserMenuBar() {

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Manual Scheduling")) {
//					AppScheduler a = new AppScheduler("New", CalGrid.this);
//					a.updateSetApp(hkust.cse.calendar.gui.Utility
//							.createDefaultAppt(currentY, currentM, currentD,
//									mCurrUser));
//					a.setLocationRelativeTo(null);
//					a.show();
//					TableModel t = prepareTableModel();
//					tableView.setModel(t);
//					tableView.repaint();
					Appt newAppt = new Appt();
					newAppt.setID(-1);
					InviteDialog inviteDialog=new InviteDialog(CalGrid.this, controller, newAppt, false, -1, "New");
					
					
					TableModel t = prepareTableModel();
					tableView.setModel(t);
					tableView.repaint();
				}

			}
		};
		
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.getAccessibleContext().setAccessibleName("Calendar Choices");
		JMenuItem mi;

		currentTime = (JLabel) menuBar.add( new JLabel());
		currentTime.setText("Loading... ");
		menuBar.add(currentTime);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){ //update the timer in GUI
			public void run(){
				String Hour;
				String Minute;
				String Second;
				int hour = TimeMachine.outputHour();
				int minute = TimeMachine.outputMinute();
				int second = TimeMachine.outputSecond();
				if( hour < 10)
					Hour = "0" + String.valueOf(hour);
				else
					Hour = String.valueOf(hour);
				if( minute < 10)
					Minute = "0" + String.valueOf(minute);
				else
					Minute = String.valueOf(minute);
				if( second < 10)
					Second = "0" + String.valueOf(second);
				else
					Second = String.valueOf(second);
				currentTime.setText("    " + Hour+":"+Minute+":"+Second+"    ");
			}
		}, 500, 1000);
		
		JMenu Access = (JMenu) menuBar.add(new JMenu("Access"));
		Access.setMnemonic('A');
		Access.getAccessibleContext().setAccessibleDescription(
				"Account Access Management");

		mi = (JMenuItem) Access.add(new JMenuItem("Logout"));	//adding a Logout menu button for user to logout
		mi.setMnemonic('L');
		mi.getAccessibleContext().setAccessibleDescription("For user logout");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Logout?",
						"Comfirm", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION){
					//controller.dumpStorageToFile();
					//System.out.println("closed");
					dispose();
					CalendarMain.logOut = true;
					return;	//return to CalendarMain()
				}
			}
		});
		
		mi = (JMenuItem) Access.add(new JMenuItem("Exit"));
		mi.setMnemonic('E');
		mi.getAccessibleContext().setAccessibleDescription("Exit Program");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "Exit Program ?",
						"Comfirm", JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION)
					System.exit(0);

			}
		});

		menuBar.add(Appmenu);
		Appmenu.setEnabled(false);
		Appmenu.setMnemonic('p');
		Appmenu.getAccessibleContext().setAccessibleDescription(
				"Appointment Management");
		mi = new JMenuItem("Manual Scheduling");
		mi.addActionListener(listener);
		Appmenu.add(mi);

		JMenu SetTimeMac = new JMenu("Time Machine");
		menuBar.add(SetTimeMac);
		SetTimeMac.setMnemonic('T');
		SetTimeMac.getAccessibleContext().setAccessibleDescription("Time Machine Management");
		
		mi = new JMenuItem("Manager Time");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				TimeDisplayDialog myTimeMac = new TimeDisplayDialog(); 
			}
			
		});
		SetTimeMac.add(mi);
			
		JMenu Usermenu = new JMenu("User");
		menuBar.add(Usermenu);
		Usermenu.setMnemonic('U');
		Usermenu.getAccessibleContext().setAccessibleDescription("User Management");

		mi = new JMenuItem("Manager User");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				UserDialog myUser = new UserDialog( mCurrUser);
			}

		});
		Usermenu.add(mi);
		
		mi = new JMenuItem("Show User Privileges");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				mCurrUser.ShowUserRight();
			}

		});
		Usermenu.add(mi);
		
		JMenu tools=new JMenu("Tools");
		menuBar.add(tools);
		mi=new JMenuItem("Weather information");
		mi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				TemperatureDialog tpd=new TemperatureDialog(controller);
			}
			
		});
		tools.add(mi);
		
		return menuBar;
	}
	
	private void loginMessage(){
		
		UserStorageController con = UserStorageController.getInstance();
		String message = "";
		switch( mCurrUser.getMyState()){
			case User.ADMIN:
				message = "Welcome back Administrator !";
				break;
			case User.NEW:
				message = "Hello " + mCurrUser.getUsername() + " !\nThank you for using this calender !";
				mCurrUser.setMyState( User.OLD);
				con.ManageUser(mCurrUser, UserStorageController.MODIFY);
				break;
			case User.OLD:
				message = "Welcome Back " + mCurrUser.getUsername() + " !";
				break;
			case User.TO_BE_DELETE:
				message = "Dear " + mCurrUser.getUsername() + "\nYou Account has been deleted by the Adminstrator !";
				mCurrUser.getMyNotifier().remove(mCurrUser);
				con.ManageUser(mCurrUser, UserStorageController.MODIFY);
				controller.refreshList();
				break;			
		}
		JOptionPane.showMessageDialog(this, message,
				"Welcome", JOptionPane.INFORMATION_MESSAGE);
		String notifierString = con.getNotifierString(mCurrUser);
		if( !notifierString.isEmpty()){
			JOptionPane.showMessageDialog(this, "The following User has been deleted\n" + notifierString,
					"User Deleted", JOptionPane.INFORMATION_MESSAGE);
		}
		notifierString = controller.getNotifierString( mCurrUser);
		if( !notifierString.isEmpty()){
			JOptionPane.showMessageDialog(this, "The following Location has been deleted\n" + notifierString,
					"Location Deleted", JOptionPane.INFORMATION_MESSAGE);
		}
		if( mCurrUser.getMyState() == User.TO_BE_DELETE){
			System.exit(0);
		}
		
	}

	private void initializeSystem() {

		mCurrUser = this.controller.getDefaultUser();	//get User from controller
		controller.LoadApptFromXml();
		// Fix Me !
		// Load the saved appointments from disk
		
		checkUpdateJoinAppt();
		
		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == eButton) {
			if (year == null)
				return;
			currentY = currentY + 1;
			year.setText(new Integer(currentY).toString());
			CalGrid.this.setTitle("Desktop Calendar - No User - (" + currentY
					+ "-" + currentM + "-" + currentD + ")");
			getDateArray(data);
			if (tableView != null) {
				TableModel t = prepareTableModel();
				tableView.setModel(t);
				tableView.repaint();

			}
			UpdateCal();
		} else if (e.getSource() == wButton) {
			if (year == null)
				return;
			currentY = currentY - 1;
			year.setText(new Integer(currentY).toString());
			CalGrid.this.setTitle("Desktop Calendar - No User - (" + currentY
					+ "-" + currentM + "-" + currentD + ")");
			getDateArray(data);
			if (tableView != null) {
				TableModel t = prepareTableModel();
				tableView.setModel(t);
				tableView.repaint();

			}
			UpdateCal();
		} else if (e.getSource() == month) {
			if (month.getSelectedItem() != null) {
				currentM = month.getSelectedIndex() + 1;
				try {
					mem_doc.remove(0, mem_doc.getLength());
					mem_doc.insertString(0, holidays[currentM - 1], sab);
				} catch (BadLocationException e1) {

					e1.printStackTrace();
				}

				CalGrid.this.setTitle("Desktop Calendar - No User - ("
						+ currentY + "-" + currentM + "-" + currentD + ")");
				getDateArray(data);
				if (tableView != null) {
					TableModel t = prepareTableModel();
					tableView.setModel(t);
					tableView.repaint();
				}
				UpdateCal();
			}
		}
	}

	// update the appointment list on gui
	public void updateAppList() {
		applist.clear();
		applist.repaint();
		applist.setTodayAppt(GetTodayAppt());
	}

	public void UpdateCal() {	// Modified by Kelvin
		if (mCurrUser != null) {
			mCurrTitle = "Desktop Calendar - " + mCurrUser.toString() + " - ";
			this.setTitle(mCurrTitle + "(" + currentY + "-" + currentM + "-"
					+ currentD + ")");

			for (int i = 0; i < 6; i++)
				for (int j = 0; j < 7; j++){
//					apptMarker[i][j] = new Vector<Appt>(10, 1);
					hasAppt[i][j] = false;
					checkDate[i][j] = false;
				}
			
			setEachDayAppt();
			TableModel t = prepareTableModel();
			this.tableView.setModel(t);
			this.tableView.repaint();
			updateAppList();
		}
	}
	
	private void setEachDayAppt() {	// Get and Check if there is Appt in each Day of the current Month
									// By Kelvin
		Appt[] list;
		

		
		Timestamp start = new Timestamp(0);		// Set a timestamp for the start of each day
		start.setYear(currentY);
		start.setMonth(currentM - 1);
		start.setDate(1);
		start.setHours(0);
		start.setMinutes(0);

		Timestamp end = new Timestamp(0);		// Set a timestamp for the end of each day
		end.setYear(currentY);
		end.setMonth(currentM - 1);
		end.setHours(23);
		end.setMinutes(59);
		
		int day = hkust.cse.calendar.gui.Utility.getweekday(start) - 1;	// get the weekday of the 1st of current Month
		
		if( day < 0)	
			day = 6;
		
		TimeSpan period;
		
		GregorianCalendar g = new GregorianCalendar(currentY, currentM - 1, 1);	
		int maximum = g.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);	// get the maximum day of current Month
		
		for (int i = 0; i < 6; i++)	
			for (int j = 0; j < 7; j++) {
				int temp1 = i * 7 + j - day + 1;	
				if (temp1 > 0 && temp1 <= maximum){		
					
					start.setDate(temp1);		
					end.setDate(temp1);
					period = new TimeSpan(start, end);
					list = controller.RetrieveAppts(mCurrUser, period);		// get the Appt at specific day in current Month
					checkDate[i][j] = true;
					
					int year = start.getYear()+1900;
					int month = start.getMonth()+1;
					int date = start.getDate();
					
					String combineDate = new String();
					String date2 ;
					if (date<10){
						date2 = "0" +  Integer.toString(date);
					}
					else
						date2 =  Integer.toString(date);
					String month2;
					if (month<10){
						month2 = "0" +  Integer.toString(month);
					}
					else
						month2 =  Integer.toString(month);
					
					combineDate = Integer.toString(year)+month2+date2;
					
					int combinetheDate = Integer.parseInt(combineDate);
					
					//Object[] temp = controller.getSummary().keySet().toArray();
					
					
					if (controller.getSummary().containsKey(combinetheDate)){
						weatherOption[i][j] = new String(controller.getSummary().get(combinetheDate));
									
					}
					else{
						
						weatherOption[i][j] = null;
					}
					
					if( list != null)
						hasAppt[i][j] = true;
//						continue;
//					for( int k = 0; k < list.length; k++)
//						apptMarker[i][j].add(list[k]);		
				}
			}
	}

//	public void clear() {
//		for (int i = 0; i < 6; i++)
//			for (int j = 0; j < 7; j++)
//				apptMarker[i][j] = new Vector(10, 1);
//		TableModel t = prepareTableModel();
//		tableView.setModel(t);
//		tableView.repaint();
//		applist.clear();
//	}

	private Appt[] GetMonthAppts() {
		Timestamp start = new Timestamp(0);
		start.setYear(currentY);
		start.setMonth(currentM - 1);
		start.setDate(1);
		start.setHours(0);
		Timestamp end = new Timestamp(0);
		end.setYear(currentY);
		end.setMonth(currentM - 1);
		GregorianCalendar g = new GregorianCalendar(currentY, currentM - 1, 1);
		end.setDate(g.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));
		end.setHours(23);
		TimeSpan period = new TimeSpan(start, end);
		return controller.RetrieveAppts(mCurrUser, period);
	}

	private void mousePressResponse() {
		previousRow = tableView.getSelectedRow();
		previousCol = tableView.getSelectedColumn();
	}
	
	private void mouseResponse() {
		int[] selectedRows = tableView.getSelectedRows();
		int[] selectedCols = tableView.getSelectedColumns();
		if( selectedRows.length == 0 || selectedCols.length == 0){
			return;
		}
		if(tableView.getSelectedRow() == previousRow && tableView.getSelectedColumn() == previousCol){
			currentRow = selectedRows[selectedRows.length - 1];
			currentCol = selectedCols[selectedCols.length - 1];
		}
		else if(tableView.getSelectedRow() != previousRow && tableView.getSelectedColumn() == previousCol){
			currentRow = tableView.getSelectedRow();
			currentCol = selectedCols[selectedCols.length - 1];
		}
		else if(tableView.getSelectedRow() == previousRow && tableView.getSelectedColumn() != previousCol){
			currentRow = selectedRows[selectedRows.length - 1];
			currentCol = tableView.getSelectedColumn();
		}
		else{
			currentRow = tableView.getSelectedRow();
			currentCol = tableView.getSelectedColumn();
		}
		
		if (currentRow > 5 || currentRow < 0 || currentCol < 0
				|| currentCol > 6)
			return;

		if (!(tableView.getModel().getValueAt(currentRow, currentCol).equals("") ))
			try {
				currentD = new Integer((String) tableView.getModel()
						.getValueAt(currentRow, currentCol)).intValue();
			} catch (NumberFormatException n) {
				return;
			}
		CalGrid.this.setTitle(mCurrTitle + "(" + currentY + "-" + currentM
				+ "-" + currentD + ")");
		updateAppList();
	}

	public boolean IsTodayAppt(Appt appt) {
		if (appt.TimeSpan().StartTime().getYear() + 1900 != currentY)
			return false;
		if ((appt.TimeSpan().StartTime().getMonth() + 1) != currentM)
			return false;
		if (appt.TimeSpan().StartTime().getDate() != currentD)
			return false;
		return true;
	}

	public boolean IsMonthAppts(Appt appt) {

		if (appt.TimeSpan().StartTime().getYear() + 1900 != currentY)
			return false;

		if ((appt.TimeSpan().StartTime().getMonth() + 1) != currentM)
			return false;
		return true;
	}

	public Appt[] GetTodayAppt() {
		Integer temp;
		temp = new Integer(currentD);
		Timestamp start = new Timestamp(0);
		start.setYear(currentY);
		start.setMonth(currentM-1);
		start.setDate(currentD);
		start.setHours(0);
		start.setMinutes(0);
		start.setSeconds(0);
		
		Timestamp end = new Timestamp(0);
		end.setYear(currentY);
		end.setMonth(currentM-1);
		end.setDate(currentD);
		end.setHours(23);
		end.setMinutes(59);
		end.setSeconds(59);
		
		TimeSpan period = new TimeSpan(start, end);
		int numOfAppt = 0;
		Appt[] tempList = controller.RetrieveAppts(mCurrUser, period);
		if( tempList != null)
			numOfAppt += tempList.length;
		
		Appt[] publicList = controller.RetrieveAllPublicAppt(period);
		if( publicList != null)
			numOfAppt += publicList.length;
		
		Appt[] returnList = null;
		if( numOfAppt > 0){			
			returnList = new Appt[ numOfAppt]; 
			
			int i = 0;
			if( publicList != null){
				for( Appt appt: publicList){
					returnList[i] = appt;
					i++;
				}
			}
			if( tempList != null){
				for( Appt appt: tempList){
					returnList[i] = appt;
					i++;
				}	
			}			
		}
		return returnList;
	}
	
	public Appt[] GetThisDayAppt( Timestamp thisDay) {	// Get The Appts of a specific day 
		Timestamp start = new Timestamp(0);				// By Kelvin
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
		return controller.RetrieveAppts(mCurrUser, period);
	}

	public Appt[] GetAllAppt(){
		return controller.RetrieveAllAppts();
	}
	
	public AppList getAppList() {
		return applist;
	}

	public User getCurrUser() {
		return mCurrUser;
	}
	
	// check for any invite or update from join appointment
	public void checkUpdateJoinAppt(){//by Hin
		Appt[] waitingList = controller.RetrieveWaitingList();
		
		if (waitingList != null){
			for (int i = 0; i<waitingList.length; i++){
		
				GroupConfirmationDialog confirmDialog = new GroupConfirmationDialog(this, waitingList[i]);
			}
		}
		
		
	}
	
	public Timestamp getCurrentDate(){	// get the current day
		Timestamp stamp = new Timestamp(0);
		stamp.setYear(currentY-1900);
		stamp.setMonth(currentM-1);
		stamp.setDate(currentD);
		return stamp;		
	}
	
    private BufferedImage[] loadImages()   //load image from external source
    {  
        String[] fileNames = {  
            "rsz_sunny-icon.png", "rsz_cloudy-icon.png", "rsz_rainy.png", "rsz_overcast-icon.png"  
        };  
        BufferedImage[] images = new BufferedImage[fileNames.length];  
        for(int j = 0; j < images.length; j++)  
            try  
            {  
                URL url = getClass().getResource(fileNames[j]);  
                images[j] = ImageIO.read(url);  
            }  
            catch(MalformedURLException mue)  
            {
            	
                System.err.println("url: " + mue.getMessage());  
            }  
            catch(IOException ioe)  
            {  
            	
                System.err.println("read: " + ioe.getMessage());  
            }  
         return images;  
    }  
    
    private BufferedImage[] getScaledImages(BufferedImage[] in, int type)   //scale the input image
    {  

        int WIDTH = 40;  
        int HEIGHT = 40;  
        BufferedImage[] out = new BufferedImage[in.length];  
        for(int j = 0; j < out.length; j++)  
        {  
            out[j] = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);  
            Graphics2D g2 = out[j].createGraphics();  
            g2.setColor(Color.white);  
            g2.fillRect(0, 0, WIDTH, HEIGHT);  
            double width  = in[j].getWidth();  
            double height = in[j].getHeight();  
            double xScale = WIDTH  / width;  
            double yScale = HEIGHT / height;  
            double scale = 1.0;  
            switch(type)  
            {  
                case 1:  
                    scale = Math.min(xScale, yScale);  // scale to fit  
                    break;  
                case 2:  
                    scale = Math.max(xScale, yScale);  // scale to fill  
            }  
            double x = (WIDTH - width * scale)/2;  
            double y = (HEIGHT - height * scale)/2;  
            AffineTransform at = AffineTransform.getTranslateInstance(x, y);  
            at.scale(scale, scale);  
            g2.drawRenderedImage(in[j], at);  
            g2.dispose();  
        }  
        return out;  
    }

}
