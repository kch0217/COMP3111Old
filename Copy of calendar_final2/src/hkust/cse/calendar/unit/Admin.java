package hkust.cse.calendar.unit;

import hkust.cse.calendar.UserRight.AdminUserRight;
import hkust.cse.calendar.UserRight.NormalUserRight;
import hkust.cse.calendar.UserRight.UserRight;

public class Admin extends User {
	
	public Admin(String username, int state) {
		super(username, state);
		// TODO Auto-generated constructor stub
		super.setUserRight( new AdminUserRight());
	}
	
}
