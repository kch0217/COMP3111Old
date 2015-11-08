package hkust.cse.calendar.gui;

import hkust.cse.calendar.unit.Location;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class LocationCellRenderer extends DefaultListCellRenderer{
	public Component getListCellRendererComponent(JList<?> list,
            Object value,int index,boolean isSelected,boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		 if (value instanceof Location) {
	            Location location = (Location)value;
	            setText(location.getName());		//set the text to be location's _name
	            if( location.getMyState() == false)
	            	setForeground( Color.RED);
	            // setIcon(ingredient.getIcon());
	        }
		 return this;
	}
}
