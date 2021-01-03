package exceptions;

import models.User;

public class ChatAlreadyExists extends Exception {

	public ChatAlreadyExists(User user) {
		super("A chat already exists with " + user.getNickname());
	}
	
}
