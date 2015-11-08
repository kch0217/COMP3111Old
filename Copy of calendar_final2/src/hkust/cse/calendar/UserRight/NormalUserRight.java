package hkust.cse.calendar.UserRight;

import javax.swing.JOptionPane;

public class NormalUserRight implements UserRight {

	@Override
	public void getUserRight() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(null, "Here is the normal user privileges: \nYou can manage your OWN appointments.\n"
				+ "You can manage your OWN user info.", "Privileges", JOptionPane.INFORMATION_MESSAGE);
	}
}
