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

public class MessageManagement {

    private final int TIMEOUT_RECEPTION_REPONSE = 500;

    private User currentUser;
    private ArrayList<User> activeUsers = new ArrayList<User>();

    public MessageManagement(User currentUser) {
        this.currentUser = currentUser;
    }

    /*
     *   Gets  all the messages that were sent between the two users
     *
     *   @param user1
     *
     *   @param user2
     *
     *   @return the history of messages between the two users
     */
    public ArrayList<Message> getHistory(int user1, int user2) {
        ArrayList<Message> messageList = new ArrayList<Message>();

        // TODO

        return messageList;
    }


    /*
     *   Adds a message to the database
     *
     *   @param recipient
     *          The person the message is sent to
     *
     *   @param transmitter
     *          The person who sent the message
     *
     */
    public void AddMessage(int recipient, int transmitter, String content) {

        // TODO

    }


    /*
     *   Call this method once a user has been successfully connected and wants to chose a nickname
     *   It will then broadcast the nickname on the local network to check if anyone has already taken this nickname
     *   In the same time, everyone will actualise their active user list
     *
     *   @param nickname
     *          the nickname that the user chose
     *
     *   @param id
     *          the id associated with the user
     *
     *   @return a list containing every active users (the ones who answered)
     */
    public ArrayList<User> isNicknameAvailable(String nickname, int id) throws NicknameAlreadyUsed, UdpConnectionFailure {
        ArrayList<User> activeUsers = new ArrayList();
        int port = 1025 + (int)(Math.random() * ((65535 - 1025) + 1)); //send the message from a random port between 1025 and 65535
        String message = "login_request:" + nickname + ":" + Integer.toString(id);

        UdpCommunication communication = new UdpCommunication();
        if (!communication.openSocket(port))
            throw (new UdpConnectionFailure("opening the socket"));
        //We chose port 1024 to receive all the connections
        if (!communication.broadcastMessage(message, 1024))
            throw (new UdpConnectionFailure("authorizing broadcast"));

        try {
            ArrayList<String> messagesRetour = communication.receiveMessages(TIMEOUT_RECEPTION_REPONSE);
            for (int i = 0; i < messagesRetour.size(); i++) {
                //Location in "infos"      0                               1                             2       3            4                   5
                //Messages format : login_response:<0 if the sender uses the nickname, 1 otherwise>::<nickname>:<id>:<Sender's IP Address>:<Sender's port>
                String[] infos = messagesRetour.get(i).split(":");
                if (infos[0].compareTo("login_response") != 0) {
                    continue;
                }
                else if (Integer.parseInt(infos[1]) == 0) {
                    throw (new NicknameAlreadyUsed(nickname));
                }
                activeUsers.add(new User(Integer.parseInt(infos[3]), infos[2], InetAddress.getByName(infos[4]), Integer.parseInt(infos[5])));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        communication.closeSocket();

        return activeUsers;
    }

    /*
     *   Call this method once the user is connected AND has chosen a nickname that has been approved
     *   to listen for new connections and actualise the active user list regularly with a thread
     */
    public void listenForConnections() {
        ConnectionsListener listener = new ConnectionsListener(this);
    }

    /*
     *   Call this method when a user who was already connected with an approved nickname
     *   has changed his nickname
     *
     *   @param newNickname
     *          the nickname that the current user wants
     *
     *   @return false if the newNickname is already used, else true
     */
    private boolean notifyNicknameChanged(String newNickname) {
        return false;
    }

    /*
     *   Broadcast a message to everyone on the local network to notify that the user is not active anymore
     */
    public void SignOutUser() {

    }

    /*
     *   @return the user for which this messageManager instance is ran
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /*
     *   Adds a user to the active users list (if it doesn't contain it already)
     *
     *   @param user
     *          the user you want to add
     */
    public void addUser(User user) {
        if (!activeUsers.contains(user))
            activeUsers.add(user);
    }
}
