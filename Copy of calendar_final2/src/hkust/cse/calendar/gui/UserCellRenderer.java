package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.User;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class UserCellRenderer extends DefaultListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList<?> list,
            Object value,int index,boolean isSelected,boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		 if (value instanceof User) {
			 	User user = (User)value;
	            setText(user.getUsername());		//set the text to be location's _name
	            if( user.getMyState() == User.ADMIN)
	            	setBackground( Color.YELLOW);
	            if( user.getMyState() == User.TO_BE_DELETE)
	            	setForeground( Color.RED);
	            // setIcon(ingredient.getIcon());
	        }
		 return this;
	}

}
