package Controllers;

import Models.Message;

import java.util.ArrayList;

public class MessageManagement {

    /*
    *   Récupère une liste de messages entre l'utilisateur 1 et l'utilisateur 2
    *   depuis la base de données.
    *
    *   @param user1
    *
    *   @param user2
    *
    *   @return L'historique des messsages de ces deux utilisateurs
    */
    public ArrayList<Message> getHistory(String user1, String user2) {
        ArrayList<Message> messageList = new ArrayList<Message>();

        // TODO

        return messageList;
    }


    /*
     *   Ajoute un message à la base de données
     *
     *   @param recipient
     *          Le destinataire du message
     *
     *   @param transmitter
     *          L'emetteur du message
     *
     */
    public void AddMessage(String recipient, String transmitter, String content) {

        // TODO

    }
}
