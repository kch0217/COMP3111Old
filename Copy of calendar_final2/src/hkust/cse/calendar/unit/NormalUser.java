package hkust.cse.calendar.unit;

import hkust.cse.calendar.UserRight.NormalUserRight;
import hkust.cse.calendar.UserRight.UserRight;

public class NormalUser extends User {
	
	public NormalUser(String username, int state) {
		super(username, state);
		// TODO Auto-generated constructor stub
		super.setUserRight( new NormalUserRight());
	}
}
