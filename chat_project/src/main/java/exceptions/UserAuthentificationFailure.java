package exceptions;

public class UserAuthentificationFailure extends Exception {
    public UserAuthentificationFailure() {
        super("Echec de l'authentification, id ou mot de passe incorrect");
    }
}
