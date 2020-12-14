package exceptions;

public class UserCreationFailure extends Exception {

    public UserCreationFailure(int id) {
        super("L'id : " + id + " est déjà utilisé.");
    }

}
