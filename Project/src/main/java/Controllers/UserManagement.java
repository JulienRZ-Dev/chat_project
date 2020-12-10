package Controllers;

import Exceptions.NicknameAlreadyUsed;
import Models.User;
import java.util.ArrayList;

public class UserManagement {

    /*
     *   Ajoute un nouvel utilisateur à la base données centralisée
     *
     *   @param id
     *          identifiant pour identifier de manière unique un utilisateur et
     *          charger ses messages par la suite ( saisi par l'utilisateur )
     *
     *   @param password
     *          mot de passe ( saisi par l'utilisateur )
     *
     *   @return faux si le couple n'a pas été trouvé dans la base de données
     *   centralisée, vrai sinon
     */
    public boolean createNewUser(int id, String password) {

        // TODO

        return false;
    }


    /*
     *   Authentifie un utilisateur
     *
     *   @param id
     *          identifiant ( saisi par l'utilisateur dans la page de connexion )
     *
     *   @param password
     *          mot de passe ( saisi par l'utilisateur dans la page de connexion )
     *
     *   @return vrai si le couple (id, password) existe dans
     *   la base de données, faux sinon
     */
    public boolean signInUser(int id, String password) {

        // TODO

        return false;
    }


    /*
     *   Supprime l'utilisateur de la liste des utilisateurs actifs
     *   pour tous les autres utilisateurs
     *
     *   @param user
     *          utilisateur qui vient de quitter l'application
     *
     *   @return faux si l'utilisateur n'etait pas connecté, vrai sinon
     */
    public boolean SignOutUser(User user) {

        // TODO

        return false;
    }


    /*
     *   A appeller quand un nouvel utilisateur s'est connecté et a choisi un
     *   psuedonyme.
     *
     *   @param id
     *          id associé au pseudonyme lors de cette session
     *
     *   @param nickname
     *          pseudonyme sélectionné par l'utilisateur
     *
     *   @return la liste des utilisateurs actifs ( qui ont répondu au broadcast )
     *
     *   @throws NicknameAlreadyUsed
     *
     */
    public ArrayList<User> notifyUserConnected(int id, String nickname) throws NicknameAlreadyUsed {
        ArrayList<User> activeUsers = new ArrayList<User>();

        // TODO

        return activeUsers;
    }


    /*
     *   Envoie un signal aux clients pour notifier un changement de pseudonyme
     *   ( sauf pour le client concerné )
     *
     *   @param oldNickname
     *          ancien pseudonyme
     *
     *   @param newNickname
     *          nouveau pseudonyme
     */
    private boolean notifyNicknameChanged(String oldNickname, String newNickname) {

        // TODO

        return false;
    }


    /*
    *   @return la liste des utilisateurs actifs
     */
    public ArrayList<User> getActiveUsers() {
        ArrayList<User> activeUsersList = new ArrayList<User>();

        // TODO

        return activeUsersList;
    }

}
