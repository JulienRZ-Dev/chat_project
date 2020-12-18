package utils;

import java.util.ArrayList;

import exceptions.UserNotFound;
import models.User;

public class Utils {
	
	/*
	 * Method that find a user with a given nickname in the userList
	 * 
	 * @return the user 
	 * 
	 * @throws UserNotFound
	 */
	public User getUserFromNickname(String nickname, ArrayList<User> userList) throws UserNotFound {
		User user = null;
		for(User userTemp : userList) {
			if(userTemp.getNickname() == nickname) {
				user = userTemp;
			}
		}
		if(user == null) {
			throw new UserNotFound(); 
		}
		return user;
	}
	
}
