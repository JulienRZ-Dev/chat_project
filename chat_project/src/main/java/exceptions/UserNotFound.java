package exceptions;

public class UserNotFound extends Exception {
	
    public UserNotFound() {
        super("L'utilisateur n'a pas �t� trouv�");
    }

}
