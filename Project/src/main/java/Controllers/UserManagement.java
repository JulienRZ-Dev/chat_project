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
    *   Vérifie si le pseudonyme est disponible
    *
    *   @param nickname
    *          pseudonyme
    *
    *   @return faux si le pseudonyme est déjà pris, vrai sinon
     */
    private boolean isNicknameAvailable(String nickname) {

        // TODO

        return false;
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
    private void notifyNickname(String oldNickname, String newNickname) {

        // TODO

    }


    /*
    *   @return la liste des utilisateurs actifs
     */
    public ArrayList<User> getActiveUsers() {
        ArrayList<User> activeUsersList = new ArrayList<User>();

        // TODO

        return activeUsersList;
    }

    /*
    *   Permet de mettre a jour le pseudonyme d'un utilisateur.
    *   Il faut appeller isNicknameAvailable pour vérifier si le nouveau
    *   peudonyme est disponible.
    *
    *   @param oldNickname
    *          pseudonyme actuel de l'utilisateur
    *
    *   @param newNickname
    *          nouveau pseudonyme de l'utilisateur
     */
    public boolean changeNickname(String oldNickname, String newNickname) {

        // TODO

        return false;
    }

}
