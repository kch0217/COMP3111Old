package hkust.cse.calendar.gui;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.github.dvdme.ForecastIOLib.FIODaily;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class TemperatureDialog extends JFrame{
	private JLabel[] days;
	private JLabel date;
	private JLabel temperature;
	private JLabel humidity;
	private JLabel summary;
	private JTable data;
	private ApptStorageControllerImpl cons;
	
	public TemperatureDialog(ApptStorageControllerImpl con){
		cons = con;
		String[] columnNames={"date","maximum temperature","minimum temperature","humidity","summary"};
		ForecastIO fio = new ForecastIO("489df2e79718f315d73e43529f0ab2f2"); //instantiate the class with the API key. 
		fio.setUnits(ForecastIO.UNITS_SI);             //sets the units as SI - optional
		fio.setExcludeURL("hourly,minutely");             //excluded the minutely and hourly reports from the reply
		fio.getForecast("22.266666", "114.183334");   //sets the latitude and longitude - not optional
		                                               //it will fail to get forecast if it is not set
		                                               //this method should be called after the options were set
		FIODaily daily = new FIODaily(fio);
		Object[][] data1=new Object[8][5];
		for(int i=0;i<data1.length;i++){
			for(int j=0;j<data1[i].length;j++){
				switch(j){
				case 0: data1[i][j]=daily.getDay(i).time();break;
				case 1: data1[i][j]=daily.getDay(i).temperatureMax();break;
				case 2: data1[i][j]=daily.getDay(i).temperatureMin();break;
				case 3: data1[i][j]=daily.getDay(i).humidity();break;
				case 4: data1[i][j]=daily.getDay(i).summary();break;
				}		
			}
			String summary = new String(daily.getDay(i).summary());
			
			String test=daily.getDay(i).time().substring(0, 10);
			String[] day=test.split("-");
			String combine="";
		    for(int a=day.length-1;a>=0;a--){
		    	combine+=day[a];
		    }
		    try{
		    Integer key=Integer.parseInt(combine);
		    	if(!cons.getTemperature().containsKey(key)){ //store the information

		    		cons.getTemperature().put(key, daily.getDay(i).temperatureMax());
		    		cons.getHumidity().put(key, daily.getDay(i).humidity());
		    		
		    		cons.getSummary().put(key, daily.getDay(i).summary());

		    	}
		    }
		    catch(Exception ex){
		    	ex.printStackTrace();
		    }
		}
		
		data=new JTable(data1,columnNames);
		JScrollPane scr=new JScrollPane(data);
		data.setFillsViewportHeight(true);
		this.add(scr);
		this.setLocationByPlatform(true);
		this.setSize(1000, 200);
		this.setVisible(true);
	}
}
