package Controllers;

import Models.User;

import java.util.ArrayList;

public class UserManagement {


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
    *   A appeller quand un nouvel utilisateur se connecte au serveur pour que les autres utilisateurs
    *   actualisent leur liste d'utilisateurs connectés
     */
    private void notifyUserConnected() {

        // TODO

    }


    /*
    *   Authentifie un utilisateur
    *
    *   @param nickname
    *          pseudonyme ( saisi par l'utilisateur dans la page de connexion )
    *
    *   @param password
    *          mot de passe ( saisi par l'utilisateur dans la page de connexion )
    *
    *   @return vrai si le couple (nickname, password) existe dans la base de données, faux sinon
     */
    public boolean signInUser(String nickname, String password) {

        // TODO

        return false;
    }

    /*
    *   Déconnecte un utilisateur
    *
    *   @param nickname
    *          pseudonyme
    *
    *   @return faux si l'utilisateur n'etait pas connecté, vrai sinon
     */
    public boolean SignOutUser(String nickname) {

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


    /*
    *   Ajoute un nouvel utilisateur à la base données.
    *   Il faut appeller isNicknameAvailable pour vérifier si le nouveau
    *   peudonyme est disponible.
    *
    *   @param nickname
    *          pseudonyme ( saisi par l'utilisateur dans la page de connexion )
    *
    *   @param password
    *          mot de passe ( saisi par l'utilisateur dans la page de connexion )
    *
    *   @return faux si le nom d'utilisateur n'a pas pu être crée, vrai sinon
     */
    public boolean createNewUser(String nickname, String password) {

        // TODO

        return false;
    }

}
