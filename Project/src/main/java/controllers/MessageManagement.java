package controllers;

import communication.ConnectionsListener;
import exceptions.UdpConnectionFailure;
import exceptions.NicknameAlreadyUsed;
import models.Message;
import models.User;
import communication.UdpCommunication;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class MessageManagement {

    private final int TIMEOUT_RECEPTION_REPONSE = 500;

    private User currentUser;
    private ArrayList<User> activeUsers = new ArrayList<User>();

    public MessageManagement(User currentUser) {
        this.currentUser = currentUser;
    }

    /*
     *   Récupère une liste de messages entre l'utilisateur 1 et l'utilisateur 2
     *   depuis la base de données centralisée
     *
     *   @param user1
     *
     *   @param user2
     *
     *   @return L'historique des messsages de ces deux utilisateurs
     */
    public ArrayList<Message> getHistory(int user1, int user2) {
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
    public void AddMessage(int recipient, int transmitter, String content) {

        // TODO

    }


    /*
     *
     *
     *
     */
    public ArrayList<User> isNicknameAvailable(String nickname, int id) throws NicknameAlreadyUsed, UdpConnectionFailure {
        ArrayList<User> activeUsers = new ArrayList();
        int port = 1025;
        String message = nickname + ":" + Integer.toString(id);

        UdpCommunication communication = new UdpCommunication();
        if (!communication.openSocket(port))
            throw (new UdpConnectionFailure("ouverture du socket"));
        if (!communication.broadcastMessage(message, 1024))
            throw (new UdpConnectionFailure("autorisation du broadcast"));

        try {
            ArrayList<String> messagesRetour = communication.receiveMessages(TIMEOUT_RECEPTION_REPONSE);
            InetAddress myAddress = InetAddress.getLocalHost();
            for (int i = 0; i < messagesRetour.size(); i++) {
                String[] infos = messagesRetour.get(i).split(":");
                if (infos[0].compareTo(myAddress.toString()) == 0) {
                    continue;
                }
                else if (Integer.parseInt(infos[1]) == 0) {
                    throw (new NicknameAlreadyUsed(nickname));
                }
                //                            0                                 1                               2      3
                //Messages format : <Sender's IP Address>:<0 if the sender uses the nickname, 1 otherwise>:<nickname>:<id>
                activeUsers.add(new User(Integer.parseInt(infos[3]), infos[2], InetAddress.getByName(infos[0])));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        communication.closeSocket();

        return activeUsers;
    }

    public void listenForConnections() {
        ConnectionsListener listener = new ConnectionsListener(this);
    }

    private boolean notifyNicknameChanged(String oldNickname, String newNickname) {
        return false;
    }

    public boolean SignOutUser(User user) {
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addUser(User user) {
        activeUsers.add(user);
    }
}
