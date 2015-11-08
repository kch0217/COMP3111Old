package hkust.cse.calendar.unit;

import java.io.Serializable;
import java.util.LinkedList;

public class Location implements Serializable{
	
	private String _name;	
	private int capacity;
	private boolean mState;				// exist = true; to be delete = false
	private LinkedList<String> mNotifier;
	
	public Location(String name){
		_name=name;
		capacity=10;
		mState = true;
		mNotifier = null;
	}
	
	public Location(String name, int cap){
		_name=name;
		capacity=cap;
		mState = true;
		mNotifier = null;
	}
	
	public String getName(){
		return _name;
	}
	public void setName(String name){
		_name = name;
	}
	public String toString(){
		return _name;
	}
	
	public int getCapacity(){
		return capacity;
	}
	public void setCapacity(int number){
		capacity=number;
	}

	public LinkedList<String> getMyNotifier(){
		return mNotifier;
	}
	
	public void setMyNotifier( LinkedList<String> notifier){
		mNotifier = notifier;
	}
	
	public boolean getMyState(){
		return mState;
	}
	
	public void setMyState(boolean state){
		mState = state;
	}
}
