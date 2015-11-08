package hkust.cse.calendar.userstorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;

import hkust.cse.calendar.apptstorage.ApptStorageControllerImpl;
import hkust.cse.calendar.apptstorage.ApptStorageNullImpl;
import hkust.cse.calendar.unit.Admin;
import hkust.cse.calendar.unit.NormalUser;
import hkust.cse.calendar.unit.User;

// By Kelvin
public class UserStorageNullImpl extends UserStorage {
	
	public UserStorageNullImpl(){
		numOfUser = 0;
		LoadUserFromXml();
	}

	@Override
	public void SaveUser(User user) {
		// TODO Auto-generated method stub
		// add new user to  mUser
		// update xml
		if( user != null){
			numOfUser++;
			mUsers.put(user.toString(), user);
			SaveUserToXml();
		}
	}

	@Override
	public void UpdateUser(User user) {
		// TODO Auto-generated method stub
		// add new user to  mUser
		// update xml
		// update nextID
		if( user != null){
			if( mUsers.get( user.toString()) != null){
				mUsers.remove(user.toString());
				mUsers.put(user.toString(), user);
			}	
		}
		SaveUserToXml();
	}

	@Override
	public void RemoveUser(User user) {
		// TODO Auto-generated method stub
		// remove user to  mUser
		// update xml
		// update nextID
		if( user != null){
			if( mUsers.get( user.toString()) != null){
				mUsers.remove( user.toString());
				numOfUser--;				
			}
		}
		SaveUserToXml();
	}

	@Override
	public User RetrieveUserByName(String username) {
		// TODO Auto-generated method stub
		return mUsers.get(username);
	}

	@Override
	public User[] RetrieveAllUsers() {
		// TODO Auto-generated method stub
		if( mUsers.size() > 0 ){
			User[] returnList = new User[ mUsers.size()];
			int currentPos = 0;
			for( String key:mUsers.keySet()){
				returnList[ currentPos] = (User) mUsers.get(key);
				currentPos++;
			}			
			return (User[]) returnList;
		}
		return null;
	}	

	@Override
	public void LoadUserFromXml() {
		// TODO Auto-generated method stub
		mUsers = new HashMap<String, User>();
		
		try{
			DataInputStream fin = new DataInputStream( 
					new BufferedInputStream ( 
							new FileInputStream("UsersInfo.xml")));
			
			XStream xin = new XStream();
			xin.autodetectAnnotations(true);
			xin.alias("User-Array", User[].class);
			xin.alias("Admin", Admin.class);
			xin.alias("User", NormalUser.class);
			
			User[] allUser = ( User[]) xin.fromXML(fin);
			numOfUser = allUser.length;
//			User[] allUser = (User[]) xin.fromXML(fin);
			
			for( User eachUser: allUser)
				mUsers.put(eachUser.toString(),eachUser);
			
			fin.close();			
		}
		catch(FileNotFoundException e){
			try {
				DataOutputStream fout = new DataOutputStream( 
						new BufferedOutputStream ( 
								new FileOutputStream("UsersInfo.xml")));
				
				fout.flush();
				fout.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void SaveUserToXml() {
		// TODO Auto-generated method stub
		try{
			XStream xout = new XStream();
			User[] outputUsers = RetrieveAllUsers();
			xout.alias("User-Array", User[].class);
			xout.alias("Admin", Admin.class);
			xout.alias("User", NormalUser.class);
			
			String storexml = xout.toXML( outputUsers);
			
			DataOutputStream fout = new DataOutputStream( 
					new BufferedOutputStream ( 
							new FileOutputStream("UsersInfo.xml")));
			fout.writeUTF(storexml);
			fout.flush();
			fout.close();
		}
		catch( FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
