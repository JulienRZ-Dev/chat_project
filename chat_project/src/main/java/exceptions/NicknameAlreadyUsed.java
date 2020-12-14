package exceptions;

public class NicknameAlreadyUsed extends Exception {

    public NicknameAlreadyUsed(String nickname) {
        super("Le pseudonyme " + nickname + " est déjà utilisé.");
    }

}
