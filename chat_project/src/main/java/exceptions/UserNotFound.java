package exceptions;

import models.User;

public class UserNotFound extends Exception {
	
    public UserNotFound() {
        super("The user could not be found");
    }

}
