package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.unit.Admin;
import hkust.cse.calendar.unit.Appt;
import hkust.cse.calendar.unit.NormalUser;
import hkust.cse.calendar.unit.TimeMachine;
import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.userstorage.UserStorageController;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class SignupDialog extends JFrame implements ActionListener {
	
	private JTextField Username;
	private JPasswordField password;
	private JPasswordField repassword;
	private JRadioButton[] admin;
	private JPasswordField adminCode;
	private JTextField Firstname;
	private JTextField Lastname;
	private JTextField email;
	private JTextField bdayYear;
	private JTextField bdayMonth;
	private JTextField bdayDate;
	private JButton cancelButton;
	private JButton signupButton;	
	
	private final String code = "group20";
	
	private UserStorageController controller = UserStorageController.getInstance();	
	
	public SignupDialog(){
		
		setTitle("Signup Now");
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		Container contentPane;
		contentPane = getContentPane();
		
		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));	
		
		JPanel userPanel = new JPanel();
		userPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		userPanel.add( new JLabel("Username :"));
		Username = new JTextField(15);
		userPanel.add(Username);
		top.add(userPanel);
		
		JPanel passPanel = new JPanel();
		passPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		passPanel.add( new JLabel("Password : "));
		password = new JPasswordField(15);
		passPanel.add( password);
		top.add( passPanel);
		
		JPanel repassPanel = new JPanel();
		repassPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		repassPanel.add( new JLabel("Re-Enter the Password : "));
		repassword = new JPasswordField(15);
		repassPanel.add(repassword);
		top.add( repassPanel);
		
		
		
		JPanel adminPanel = new JPanel();
		adminPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		admin = new JRadioButton[2];
		admin[0] = new JRadioButton("No");
		admin[0].addActionListener(this);
		admin[1] = new JRadioButton("Yes");
		admin[1].addActionListener(this);
		admin[0].setSelected(true);
		ButtonGroup adminBG = new ButtonGroup();
		adminBG.add(admin[0]);
		adminBG.add(admin[1]);
		adminPanel.add( new JLabel("Adminstrator:"));
		adminPanel.add(admin[0]);
		adminPanel.add(admin[1]);
		adminCode = new JPasswordField(15);
		adminCode.setEnabled(false);
		adminPanel.add(adminCode);
		top.add(adminPanel);

		contentPane.add("North", top);
		
		JPanel bottom = new JPanel();
		Border bottomBorder = new TitledBorder(null, "Personal Information");
		bottom.setBorder(bottomBorder);
		bottom.setLayout(new BoxLayout( bottom, BoxLayout.Y_AXIS));
		
		
		JPanel namePanel = new JPanel();
		Border nameBorder = new TitledBorder(null, "Name");
		namePanel.setBorder(nameBorder);
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		namePanel.add( new JLabel("First Name : "));
		Firstname = new JTextField(20);
		namePanel.add( Firstname);
		namePanel.add( new JLabel("Last Name : "));
		Lastname = new JTextField(20);
		namePanel.add( Lastname);		
		bottom.add(namePanel);
		
		JPanel emailPanel = new JPanel();
		Border emailBorder = new TitledBorder(null, "Email");
		emailPanel.setBorder(emailBorder);
		emailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		emailPanel.add( new JLabel("Email : "));
		email = new JTextField(40);
		emailPanel.add(email);
		bottom.add(emailPanel);
		
		JPanel bdayPanel = new JPanel();
		Border bdayBorder = new TitledBorder( null, "Birthday");
		bdayPanel.setBorder( bdayBorder);
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
		bottom.add(bdayPanel);
		
		contentPane.add("Center", bottom);
		
		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		signupButton = new JButton("Comfirm");
		signupButton.addActionListener(this);
		btnPanel.add(signupButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		btnPanel.add(cancelButton);
		
		contentPane.add("South", btnPanel);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == admin[0]){
			adminCode.setEnabled(false);
		}
		else if( e.getSource() == admin[1]){
			adminCode.setEnabled(true);		
		}
		if(e.getSource() == signupButton){
			try{
				saveButtonRespond();
			}
			catch( Exception error){
				JOptionPane.showMessageDialog(this, error.getMessage(),
						"Input Error", JOptionPane.ERROR_MESSAGE);
			}
			if( this.isVisible())
				return;
		}
		else if( e.getSource() == cancelButton){
			int n = JOptionPane.showConfirmDialog(null, "Cancel Signup ?",
					"Confirm", JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION){
				this.setVisible(false);
				dispose();
			}
		}
		
	}
	
	private void saveButtonRespond() throws Exception{
		try{
			String username = isCorrectUsername( Username);
			String passwordText = isCorrectPassword( password, repassword);
			int userType = isAdmin( admin, adminCode);
			String[] name = isCorrectName( Firstname, Lastname);		// name[0] = FirstName, name[1] = LastName
			String emailText = isCorrectEmail( email);
			Timestamp bday = isCorrectBirthday( bdayYear, bdayMonth, bdayDate);
			
			User newUser = null;
			if( userType == User.ADMIN)
				newUser = new Admin( username, userType);
			else{
				newUser = new NormalUser( username, userType);
			}
			newUser.setPassword( controller.encryptPassword(passwordText));
			newUser.setName(name[0], name[1]);
			newUser.setEmail(emailText);
			newUser.setBirthday( bday);
			
			// Store the new user here if no error exist
			controller.ManageUser(newUser, UserStorageController.NEW);
			
			ApptStorageControllerImpl con = new ApptStorageControllerImpl(ApptStorageNullImpl.getStorageInstance( new User(" ", 1)));
			con.LoadApptFromXml();
			Appt[] appList = con.RetrieveAllAppts();
			if( appList != null){
				for( Appt appt: appList){
					User[] tempUserL = appt.getRemained();
					User[] newUserL = new User[ tempUserL.length + 1];
					for( int i = 0; i < tempUserL.length; i++){
						newUserL[i] = tempUserL[i];
					}
					newUserL[tempUserL.length] = newUser;
					appt.setRemained(newUserL);
				}
				con.updateAppt();
			}
			JOptionPane.showMessageDialog(this, "Your have Finished the Signup!",
					"Signup", JOptionPane.INFORMATION_MESSAGE);	
			
			this.setVisible(false);
			dispose();
		}
		catch( Exception ex){
			throw ex;
		}
		
	}
	
	private String isCorrectUsername(JTextField username) throws Exception{
		String userText = username.getText().trim();
		if( userText.isEmpty())
			throw new Exception("Invalid Username !\nUsername must be exists !");
		if( userText.length() < 5)
			throw new Exception("Invalid Username !\nYour Username is Too Short !");
		if( !ValidString( userText.toCharArray()))
			throw new Exception("Invalid Username !\nUsername can contains only letters and numbers!");
		
		User[] allUser = controller.RetrieveAllUsers();	
		if( allUser != null){
			for( User user: allUser){
				if( userText.equals(user.getUsername()))
					throw new Exception("Invalid Username !\nThis username have been used !");
			}				
		}
			
		return userText;
	}
	
	private String isCorrectPassword( JPasswordField pass, JPasswordField repass) throws Exception{
		char[] password = pass.getPassword();
		char[] repassword = repass.getPassword();
		if( password.length <= 0)
			throw new Exception("Invalid Password !\nPassword Cannot be empty !");
		if( password.length < 5)
			throw new Exception("Invalid Password !\nThe Password is Too short !");
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
	
	private int isAdmin( JRadioButton[] admin, JPasswordField code) throws Exception{
		if( admin[1].isSelected()){
			char[] codeChar = code.getPassword();
			char[] codeCheck = this.code.toCharArray();
			if( codeChar.length != codeCheck.length)
				throw new Exception("Invalid Admin Code !\nYou need a Correct Code to be Adminstrator");
			else{
				for( int i = 0; i < codeChar.length; i++){
					if( codeChar[i] != codeCheck[i])
						throw new Exception("Invalid Admin Code !\nYou need a Correct Code to be Adminstrator");						
				}
			}			
			return User.ADMIN;
		}		
		return User.NEW;
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
