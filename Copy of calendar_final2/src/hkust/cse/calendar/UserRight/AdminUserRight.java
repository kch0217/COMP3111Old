package hkust.cse.calendar.UserRight;

import javax.swing.JOptionPane;

public class AdminUserRight implements UserRight {

	@Override
	public void getUserRight() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, "Here is the Admin user privileges: \nYou can manage your OWN appointments.\n"
				+ "You can manage, modify or delete All user info.\nYou can manage, modify or delete All location info.", "Privileges", JOptionPane.INFORMATION_MESSAGE);
	}
}
