package exceptions;

import models.User;

public class ChatNotFoundException extends Exception {
	public ChatNotFoundException(User user) {
		super("No chat found with " + user.getNickname());
	}
}
