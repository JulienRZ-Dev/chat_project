package exceptions;

import models.User;

public class ChatNotFound extends Exception {
	public ChatNotFound(User user) {
		super("No chat found with " + user.getNickname());
	}
}
