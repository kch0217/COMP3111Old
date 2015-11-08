package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.unit.TimeMachine;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class ChangePasswordDialog extends JFrame implements ActionListener{

	private JPasswordField oldpassword;
	private JPasswordField password;
	private JPasswordField repassword;
	private JButton cancelBtn;
	private JButton comfirmBtn;
	private User mCurrUser;
	private UserStorageController controller;
	
	public ChangePasswordDialog( User user){
		
		controller = UserStorageController.getInstance();
		setTitle("Change Password");
		mCurrUser = user;				
		Container content = getContentPane();
		setLocationByPlatform(true);
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
		
		JPanel oldpassPanel = new JPanel();
		oldpassPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		oldpassPanel.add( new JLabel("Old Password : "));
		oldpassword = new JPasswordField(15);
		oldpassPanel.add( oldpassword);
		top.add( oldpassPanel);
		
		JPanel passPanel = new JPanel();
		passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		passPanel.add( new JLabel("New Password : "));
		password = new JPasswordField(15);
		passPanel.add( password);
		top.add( passPanel);
		
		JPanel repassPanel = new JPanel();
		repassPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		repassPanel.add( new JLabel("Re-Enter the New Password : "));
		repassword = new JPasswordField(15);
		repassPanel.add(repassword);
		top.add( repassPanel);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		comfirmBtn = new JButton("Comfirm");
		comfirmBtn.addActionListener(this);
		btnPanel.add(comfirmBtn);
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		btnPanel.add(cancelBtn);
		
		content.add("Center", top);
		content.add("South", btnPanel);
		
		pack();
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if( e.getSource() == comfirmBtn){
			try{
				controller.isCorrectPassword( mCurrUser.getUsername(), oldpassword.getPassword());
				String passwordText = isCorrectPassword( password, repassword);
				mCurrUser.setPassword( controller.encryptPassword(passwordText));
				controller.ManageUser(mCurrUser, UserStorageController.MODIFY);
				JOptionPane.showMessageDialog(this, "The Password has been modified !",
						"Comfirm", JOptionPane.INFORMATION_MESSAGE);
				setVisible(false);
				dispose();
			}
			catch( Exception ex){
				JOptionPane.showMessageDialog(this, ex.getMessage(),
						"Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if( e.getSource() == cancelBtn){
			setVisible(false);
			dispose();
		}
	}
	
	private String isCorrectPassword( JPasswordField pass, JPasswordField repass) throws Exception{
		char[] password = pass.getPassword();
		char[] repassword = repass.getPassword();
		if( password.length <= 0)
			throw new Exception("Invalid Password !\nPassword Cannot be empty !");
		if( password.length < 5)
			throw new Exception("Invalid Password !\nThe Password is too short !");
		if( password.length != repassword.length)
			throw new Exception("Invalid Password !\nPassword and re-entered Password do not match !");
		if( !ValidString( password))
			throw new Exception("Invalid Password !\nPassword can contains only letters and numbers!");		
		else{
			for( int i = 0; i < password.length; i++){
				if( password[i] != repassword[i])
					throw new Exception("Invalid Password !\nPassword and re-entered Password do not match !");
			}
		}
		return String.valueOf(password);
	}
	
	private boolean ValidString(char[] sChar)
	{
		for(int i = 0; i < sChar.length; i++)
		{
			int sInt = (int)sChar[i];
			if(sInt < 48 || sInt > 122)
				return false;
			if(sInt > 57 && sInt < 65)
				return false;
			if(sInt > 90 && sInt < 97)
				return false;
		}
		return true;
	}
	
}

class ChangeNameDialog extends JFrame implements ActionListener{

	private JTextField firstname; 
	private JTextField lastname;
	private User mCurrUser;
	private JButton comfirmBtn;
	private JButton cancelBtn;
	private UserStorageController controller;
	private UserDialog parent;		
	
	public ChangeNameDialog( User user, UserDialog par){
		
		parent = par;
		mCurrUser = user;
		controller = UserStorageController.getInstance();
		Container content = getContentPane();
		setLocationByPlatform(true);
		setTitle("Change Name");
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout( top, BoxLayout.Y_AXIS));
		
		JPanel firstNamePanel = new JPanel();
		firstNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		firstNamePanel.add( new JLabel("First Name : "));
		firstname = new JTextField(20);
		firstNamePanel.add( firstname);
		top.add(firstNamePanel);
		
		JPanel lastNamePanel = new JPanel();
		lastNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		lastNamePanel.add( new JLabel("Last Name : "));
		lastname = new JTextField(20);
		lastNamePanel.add( lastname);		
		top.add(lastNamePanel);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		comfirmBtn = new JButton("Comfirm");
		comfirmBtn.addActionListener(this);
		btnPanel.add(comfirmBtn);
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		btnPanel.add(cancelBtn);
		
		content.add("Center", top);
		content.add("South", btnPanel);
		
		pack();
		setVisible(true);		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if( e.getSource() == comfirmBtn){
			try{
				String[] Names = isCorrectName( firstname, lastname);
				mCurrUser.setName( Names[0], Names[1]);
				controller.ManageUser(mCurrUser, UserStorageController.MODIFY);
				JOptionPane.showMessageDialog(this, "The Name has been modified !",
						"Comfirm", JOptionPane.INFORMATION_MESSAGE);
				parent.setName( mCurrUser.getName());
				setVisible(false);
				dispose();
			}
			catch( Exception ex){
				JOptionPane.showMessageDialog(this, ex.getMessage(),
						"Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if( e.getSource() == cancelBtn){
			setVisible(false);
			dispose();
		}
		
	}
	
	private String[] isCorrectName( JTextField firstname, JTextField lastname) throws Exception{
		String first = firstname.getText().trim();
		String last = lastname.getText().trim();
		if( first.isEmpty() || last.isEmpty()){
			throw new Exception("Invalid Name !\nName must be exists !");
		}
		String[] name = new String[2];
		name[0] = first;
		name[1] = last;
		return name;
	}
	
}

class ChangeEmailDialog extends JFrame implements ActionListener{

	
	private User mCurrUser;
	private UserStorageController controller;
	private JTextField email;
	private JButton comfirmBtn;
	private JButton cancelBtn;
	private UserDialog parent;

	public ChangeEmailDialog( User user, UserDialog par){
		
		parent = par;
		mCurrUser = user;
		controller = UserStorageController.getInstance();
		Container content = getContentPane();
		setLocationByPlatform(true);
		setTitle("Change Email");
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout( top, BoxLayout.Y_AXIS));
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		emailPanel.add( new JLabel("Email : "));
		email = new JTextField(40);
		emailPanel.add(email);
		top.add(emailPanel);

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		comfirmBtn = new JButton("Comfirm");
		comfirmBtn.addActionListener(this);
		btnPanel.add(comfirmBtn);
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		btnPanel.add(cancelBtn);
		
		content.add("Center", top);
		content.add("South", btnPanel);
		
		pack();
		setVisible(true);		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if( e.getSource() == comfirmBtn){
			try{
				String Email = isCorrectEmail( email);
				mCurrUser.setEmail(Email);
				controller.ManageUser(mCurrUser, UserStorageController.MODIFY);
				JOptionPane.showMessageDialog(this, "The Email has been modified !",
						"Comfirm", JOptionPane.INFORMATION_MESSAGE);
				parent.setEmail( mCurrUser.getEmail());
				setVisible(false);
				dispose();
			}
			catch( Exception ex){
				JOptionPane.showMessageDialog(this, ex.getMessage(),
						"Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if( e.getSource() == cancelBtn){
			setVisible(false);
			dispose();
		}		
	}
	
	private String isCorrectEmail( JTextField emailText) throws Exception{
		
		String emailString = emailText.getText().trim();
		Pattern pattern;
		Matcher matcher;
	 
		String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		
		pattern = Pattern.compile(EMAIL_PATTERN);
		
		matcher = pattern.matcher( emailString);
		if( !matcher.matches())
			throw new Exception("Invalid Email !\nPlease Enter a valid Email !");
		else
			return emailString;
	}
	
}

class ChangeBirthdayDialog extends JFrame implements ActionListener{

	private User mCurrUser;
	private UserStorageController controller;
	private JTextField bdayYear;
	private JTextField bdayMonth;
	private JTextField bdayDate;
	private JButton comfirmBtn;
	private JButton cancelBtn;
	private UserDialog parent;

	public ChangeBirthdayDialog( User user, UserDialog par){
		
		parent = par;
		mCurrUser = user;
		controller = UserStorageController.getInstance();
		Container content = getContentPane();
		setLocationByPlatform(true);
		setTitle("Change Birthday");
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout( top, BoxLayout.Y_AXIS));
		
		JPanel bdayPanel = new JPanel();
		bdayPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		bdayPanel.add( new JLabel("Year : "));
		bdayYear = new JTextField(6);
		bdayPanel.add(bdayYear);
		bdayPanel.add( new JLabel("Month : "));
		bdayMonth = new JTextField(4);
		bdayPanel.add(bdayMonth);
		bdayPanel.add( new JLabel("Date : "));
		bdayDate = new JTextField(4);
		bdayPanel.add(bdayDate);
		top.add(bdayPanel);		

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		comfirmBtn = new JButton("Comfirm");
		comfirmBtn.addActionListener(this);
		btnPanel.add(comfirmBtn);
		
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(this);
		btnPanel.add(cancelBtn);
		
		content.add("Center", top);
		content.add("South", btnPanel);
		
		pack();
		setVisible(true);		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if( e.getSource() == comfirmBtn){
			try{
				Timestamp bday = isCorrectBirthday( bdayYear, bdayMonth, bdayDate);
				mCurrUser.setBirthday(bday);
				controller.ManageUser(mCurrUser, UserStorageController.MODIFY);
				JOptionPane.showMessageDialog(this, "The Birthday has been modified !",
						"Comfirm", JOptionPane.INFORMATION_MESSAGE);
				parent.setBirthday( mCurrUser.getBirthdayString());
				setVisible(false);
				dispose();
			}
			catch( Exception ex){
				JOptionPane.showMessageDialog(this, ex.getMessage(),
						"Input Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if( e.getSource() == cancelBtn){
			setVisible(false);
			dispose();
		}		
	}
	
	private Timestamp isCorrectBirthday(JTextField year, JTextField month, JTextField date) throws Exception{
		Timestamp birthday = new Timestamp(0);		
		int yearInt = hkust.cse.calendar.gui.Utility.getNumber( year.getText());
		int monthInt = hkust.cse.calendar.gui.Utility.getNumber( month.getText());
		int dateInt =hkust.cse.calendar.gui.Utility.getNumber( date.getText());
		
		GregorianCalendar g = new GregorianCalendar(yearInt - 1900, monthInt - 1, 1);
		int maximumDay = g.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);	
		
		if( yearInt == -1 || monthInt == -1 || dateInt == -1)
			throw new Exception("Invalid Birthday !\nThe value of the Birthday must be numbers");
		if( yearInt < 1850)
			throw new Exception("Invalid Birthday !\nYear number cannot smaller than 1850");
		if( monthInt > 12)
			throw new Exception("Invalid Birthday !\nMonth number cannot larger than 12");
		if( monthInt < 1)
			throw new Exception("Invalid Birthday !\nMonth number cannot smaller than 1");
		if( dateInt > maximumDay)
			throw new Exception("Invalid Birthday !\nThe date is too large !");
		if( dateInt < 1)
			throw new Exception("Invalid Birthday !\nThe date cannot smaller than 1 !");
		
		birthday.setYear( yearInt- 1900);
		birthday.setMonth(monthInt - 1);
		birthday.setDate( dateInt);
		birthday.setHours(23);
		birthday.setMinutes(59);
		birthday.setSeconds(59);
		
		if( birthday.getTime() >= TimeMachine.getTime().getTime())
			throw new Exception("Invalid Birthday !\nYou cannot born in future !");
		
		return birthday;
	}
	
}

public class UserDialog extends JFrame implements ActionListener{

	private JButton exitBtn;
	private JButton removeBtn;
	private JScrollPane scroll;
	private JList<User> userList;
	private User[] Alluser;
	private DefaultListModel<User> listModel;
	private UserStorageController controller;
	private JLabel username;
	private JLabel fullname;
	private JLabel email;
	private JLabel birthday;
	private JButton changePass;
	private JButton changeName;
	private JButton changeEmail;
	private JButton changeBirthday;
	private User selectedUser;
	private User mCurrUser;

	public UserDialog(User thisUser){		
		controller = UserStorageController.getInstance();
		mCurrUser = thisUser;
		if( thisUser.getMyState() == User.ADMIN)
			CreateAdminDialog();
		else
			CreateUserDialog( thisUser);
		
	}	
	
	public void CreateAdminDialog(){
		Container content = getContentPane();
		setLocationByPlatform(true);
		setTitle("User Information");
		
		listModel = new DefaultListModel<User>();
		UserCellRenderer renderer = new UserCellRenderer();
		Alluser = controller.RetrieveAllUsers();
		for( User user: Alluser)
			listModel.addElement(user);
		
		userList = new JList<User>(listModel);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.setSelectedIndex(0);
		selectedUser = Alluser[0];
		userList.setCellRenderer( renderer);	//to ensure the list displays the _name of the location
		userList.setVisibleRowCount( 12);
		
		scroll = new JScrollPane(userList);		//to create the scrollpane
		scroll.setPreferredSize(new Dimension(150, 240));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JPanel panel = new JPanel();
		Border border = new TitledBorder(null, "Information");
		panel.setLayout( new BorderLayout());
		panel.setPreferredSize( new Dimension(350, 240));
		panel.setBorder(border);
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));	
		
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		userPanel.add( new JLabel("Username :"));
		username = new JLabel( Alluser[0].getUsername());
		userPanel.add( username);
		top.add(userPanel);
		
		JPanel passPanel = new JPanel();
		passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		passPanel.add( new JLabel("Password : "));
		passPanel.add( new JLabel(" ******** "));
		changePass = new JButton("Change");
		changePass.addActionListener(this);
		passPanel.add( changePass);
		top.add(passPanel);
		
		JPanel bottom = new JPanel();
		Border bottomBorder = new TitledBorder(null, "Personal Information");
		bottom.setBorder(bottomBorder);
		bottom.setLayout(new BoxLayout( bottom, BoxLayout.Y_AXIS));		
		
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));		
		namePanel.add( new JLabel("Full Name : "));
		fullname = new JLabel( Alluser[0].getName());
		namePanel.add( fullname);
		changeName = new JButton("Change");
		changeName.addActionListener(this);
		namePanel.add(changeName);
		bottom.add(namePanel);
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));		
		emailPanel.add( new JLabel("Email : "));
		email = new JLabel( Alluser[0].getEmail());
		emailPanel.add( email);
		changeEmail = new JButton("Change");
		changeEmail.addActionListener(this);
		emailPanel.add( changeEmail);
		bottom.add(emailPanel);
		
		JPanel bdayPanel = new JPanel();
		bdayPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bdayPanel.add( new JLabel("Birthday : "));
		birthday = new JLabel( Alluser[0].getBirthdayString());
		bdayPanel.add( birthday);
		changeBirthday = new JButton("Change");
		changeBirthday.addActionListener(this);
		bdayPanel.add(changeBirthday);		
		bottom.add(bdayPanel);
		
		panel.add("North", top);
		panel.add("South", bottom);		
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		removeBtn = new JButton("Delete");
		removeBtn.addActionListener(this);
		btnPanel.add( removeBtn);
		exitBtn = new JButton("Exit");
		exitBtn.addActionListener(this);
		btnPanel.add( exitBtn);
		
		userList.addListSelectionListener(new ListSelectionListener(){	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if(e.getValueIsAdjusting()==false){
					if(userList.getSelectedIndex()==-1){		//to ensure remove button is not enabled
						removeBtn.setEnabled(false);		//when no items available
						selectedUser = null;
					}
					else{
						removeBtn.setEnabled(true);
						selectedUser = Alluser[ userList.getSelectedIndex()];
						username.setText(selectedUser.getUsername());
						fullname.setText(selectedUser.getName());
						email.setText(selectedUser.getEmail());
						birthday.setText(selectedUser.getBirthdayString());
					}
				}
			}			
		});
		
		content.add("West", scroll);
		content.add("East", panel);
		content.add("South", btnPanel);
		pack();
		setVisible(true);
	}
	
	public void CreateUserDialog(User mCurrUser){
		Container content = getContentPane();
		setLocationByPlatform(true);
		setTitle("User Information");
		selectedUser = mCurrUser;
		
		JPanel panel = new JPanel();
		Border border = new TitledBorder(null, "Information");
		panel.setLayout( new BorderLayout());
		panel.setPreferredSize( new Dimension(350, 240));
		panel.setBorder(border);
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));	
		
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		userPanel.add( new JLabel("Username :"));
		username = new JLabel( mCurrUser.getUsername());
		userPanel.add( username);
		top.add(userPanel);
		
		JPanel passPanel = new JPanel();
		passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		passPanel.add( new JLabel("Password : "));
		passPanel.add( new JLabel(" ******** "));
		changePass = new JButton("Change");
		changePass.addActionListener(this);
		passPanel.add( changePass);
		top.add(passPanel);
		
		JPanel bottom = new JPanel();
		Border bottomBorder = new TitledBorder(null, "Personal Information");
		bottom.setBorder(bottomBorder);
		bottom.setLayout(new BoxLayout( bottom, BoxLayout.Y_AXIS));		
		
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));		
		namePanel.add( new JLabel("Full Name : "));
		fullname = new JLabel( mCurrUser.getName());
		namePanel.add( fullname);
		changeName = new JButton("Change");
		changeName.addActionListener(this);
		namePanel.add(changeName);
		bottom.add(namePanel);
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));		
		emailPanel.add( new JLabel("Email : "));
		email = new JLabel( mCurrUser.getEmail());
		emailPanel.add( email);
		changeEmail = new JButton("Change");
		changeEmail.addActionListener(this);
		emailPanel.add( changeEmail);
		bottom.add(emailPanel);
		
		JPanel bdayPanel = new JPanel();
		bdayPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		bdayPanel.add( new JLabel("Birthday : "));
		birthday = new JLabel( mCurrUser.getBirthdayString());
		bdayPanel.add( birthday);
		changeBirthday = new JButton("Change");
		changeBirthday.addActionListener(this);
		bdayPanel.add(changeBirthday);		
		bottom.add(bdayPanel);
		
		panel.add("North", top);
		panel.add("South", bottom);		
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		exitBtn = new JButton("Exit");
		exitBtn.addActionListener(this);
		btnPanel.add( exitBtn);

		content.add("East", panel);
		content.add("South", btnPanel);
		pack();
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if( e.getSource() == exitBtn){
			this.setVisible(false);
			dispose();
		}
		if( e.getSource() == removeBtn){
			if( selectedUser == null)
				return;
			if( selectedUser.getMyState() == User.ADMIN)
				JOptionPane.showMessageDialog(this, "Adminstrator Account Cannot be Delete !",
						"Warning", JOptionPane.ERROR_MESSAGE);
			else if( selectedUser.getMyState() == User.TO_BE_DELETE)
				JOptionPane.showMessageDialog(this, "You Cannot Delete a Deleted Account !",
						"Warning", JOptionPane.ERROR_MESSAGE);
			else{
				int n = JOptionPane.showOptionDialog(null, "Are You Sure to Delete This User " + selectedUser.getUsername()
						+ " ?\n You CANNOT re-do this action","Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);
				if (n == JOptionPane.YES_OPTION){
					selectedUser.setMyState(User.TO_BE_DELETE);
					ApptStorageControllerImpl appcon = new ApptStorageControllerImpl( ApptStorageNullImpl.getStorageInstance( mCurrUser));
					LinkedList<String> list = appcon.RemoveApptAndNotifyTheInitializer( selectedUser);
					if( list == null){
						list = new LinkedList<String>();
					}
					list.add( selectedUser.getUsername());
					selectedUser.setMyNotifier( list);
					controller.ManageUser( selectedUser, UserStorageController.MODIFY);
					userList.repaint();	
				}
			}
		}
		if( e.getSource() == changePass){
			if(selectedUser.getMyState() == User.TO_BE_DELETE){
				JOptionPane.showMessageDialog(this, "You Cannot Modify a Deleted Account !",
						"Warning", JOptionPane.ERROR_MESSAGE);
				return;
			}
			ChangePasswordDialog dlg = new ChangePasswordDialog( selectedUser);
		}
		if( e.getSource() == changeName){
			if(selectedUser.getMyState() == User.TO_BE_DELETE){
				JOptionPane.showMessageDialog(this, "You Cannot Modify a Deleted Account !",
						"Warning", JOptionPane.ERROR_MESSAGE);
				return;
			}
			ChangeNameDialog dlg = new ChangeNameDialog( selectedUser, this);
		}
		if( e.getSource() == changeEmail){
			if(selectedUser.getMyState() == User.TO_BE_DELETE){
				JOptionPane.showMessageDialog(this, "You Cannot Modify a Deleted Account !",
						"Warning", JOptionPane.ERROR_MESSAGE);
				return;
			}
			ChangeEmailDialog dlg = new ChangeEmailDialog( selectedUser, this);
		}
		if( e.getSource() == changeBirthday){
			if(selectedUser.getMyState() == User.TO_BE_DELETE){
				JOptionPane.showMessageDialog(this, "You Cannot Modify a Deleted Account !",
						"Warning", JOptionPane.ERROR_MESSAGE);
				return;
			}
			ChangeBirthdayDialog dlg = new ChangeBirthdayDialog( selectedUser, this);
		}
	}
	
	public void setName( String name){
		fullname.setText( name);
	}
	
	public void setEmail( String mail){
		email.setText( mail);
	}
	
	public void setBirthday( String bday){
		birthday.setText( bday);
	}

}
