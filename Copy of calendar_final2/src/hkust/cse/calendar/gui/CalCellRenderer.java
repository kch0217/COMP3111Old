package hkust.cse.calendar.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

class CalCellRenderer extends DefaultTableCellRenderer

{

	private int r;

	private int c;

	public CalCellRenderer(Object value, Boolean hasAppt, Boolean validDate, String summary,BufferedImage[] image) {
		if (value != null) {
			setForeground(Color.red);
		} else
			setForeground(Color.black);
		if ( hasAppt){								// Set Background to yellow if have Appt at the day of this cell
			setBackground(Color.yellow);
			if (summary!=null &&image !=null){ //display weather image according to the summary
				//System.out.println(summary);
				if (summary.contains("cloudy") || summary.contains("Cloudy")){
					setIcon(new ImageIcon(image[1]));
				}
				else
					if (summary.contains("drizzle") || summary.contains("Drizzle")|| summary.contains("rain")||summary.contains("Rain")){
						
						setIcon(new ImageIcon(image[2]));
					}
					else
						if (summary.contains("overcast")||summary.contains("Overcast")){
							setIcon(new ImageIcon(image[3]));
						}
						else
							setIcon(new ImageIcon(image[0]));
			}
		} else
			if (validDate){
			setBackground(Color.white);
			if (summary!=null &&image !=null){ //display weather image according to the summary
				//System.out.println(summary);
				if (summary.contains("cloudy") || summary.contains("Cloudy")){
					setIcon(new ImageIcon(image[1]));
				}
				else
					if (summary.contains("drizzle") || summary.contains("Drizzle")||summary.contains("rain")||summary.contains("Rain")){
						setIcon(new ImageIcon(image[2]));
					}
					else
						if (summary.contains("overcast")||summary.contains("Overcast")){
							setIcon(new ImageIcon(image[3]));
						}
						else
							setIcon(new ImageIcon(image[0]));
			}
			
			}
			else
				setBackground(Color.gray);
		
		
//		if (image!=null)
//			setIcon(new ImageIcon(image[1]));
		
		
		setHorizontalAlignment(SwingConstants.RIGHT);
		setVerticalAlignment(SwingConstants.TOP);

	}

	public int row() {
		return r;
	}

	public int col() {
		return c;
	}

}
