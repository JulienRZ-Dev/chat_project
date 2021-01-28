package controllers;

import communication.ChatManager;
import communication.NotificationsListener;
import exceptions.ChatAlreadyExists;
import exceptions.ChatNotFound;
import exceptions.UdpConnectionFailure;
import exceptions.UserNotFound;
import models.Message;
import models.User;
import views.MainWindow;
import communication.UdpCommunication;
import database.DatabaseMessages;

import java.io.IOException;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.lang.model.element.ModuleElement.UsesDirective;

public class MessageManagement {

    private final int TIMEOUT_RECEPTION_REPONSE = 300;

    private User currentUser;
    private ArrayList<User> activeUsers = new ArrayList<User>();
    
    // Update view
    private MainWindow mainWindow;
    
    // TCP message listener and manager
    private NotificationsListener listener;
    private ChatManager chatManager;

    // Database
    private DatabaseMessages db = new DatabaseMessages();
    
    public MessageManagement(User currentUser) {
        this.currentUser = currentUser;
        this.chatManager = new ChatManager(this.currentUser.getPort(), this);
    }

    /*
     *   Call this method once a user has been successfully connected and wants to chose a nickname
     *   It will then broadcast the nickname on the local network to check if anyone has already taken this nickname
     *   In the same time, everyone will actualize their active user list
     *
     *   @param nickname
     *          the nickname that the user chose
     *
     *   @param id
     *          the id associated with the user
     *
     *   @return false if the nickname is already used, else true
     */
    public boolean isNicknameAvailable(String nickname) throws UdpConnectionFailure {
        int id = this.currentUser.getId();
        Random random = new Random();
    	int broadcast_port = random.nextInt(65535 + 1 - 6000) + 6000; //send the message from a random port between 6000 and 65535
        String message = "login_request:" + nickname + ":" + Integer.toString(id) + ":" + this.currentUser.getPort();

        try {
        	UdpCommunication communication = new UdpCommunication();
        	if (!communication.openSocket(broadcast_port))
        		throw (new UdpConnectionFailure("opening the socket"));
        	//We chose port 5000 to receive all the connections and disconnection messages
        	if (!communication.broadcastMessage(message, 5000))
        		throw (new UdpConnectionFailure("authorizing broadcast"));
        
            ArrayList<String> responses = communication.receiveMessages(TIMEOUT_RECEPTION_REPONSE);
            for (int i = 0; i < responses.size(); i++) {
            	System.out.println("isNicknameAvailable - message re�u : " + responses.get(i));
                //Location in "infos"      0                               1                             2       3            4                   5               6
                //Messages format : login_response:<0 if the sender uses the nickname, 1 otherwise>:<nickname>:<id>:<Sender's tcp_port>:<Sender's IP Address>:<udp_port>
            	String[] infos = responses.get(i).split(":");
                //If the response doesn't have the right type, we ignore it
                if (!infos[0].equals("login_response")) {
                    continue;
                }
                //If the response is from ourself, we ignore it
                else if ((infos[5].equals(InetAddress.getLocalHost().toString())) && (this.currentUser.getPort() == Integer.parseInt(infos[4]))) {
                	continue;
                }
                //If infos[1] != 0 then another user has already chosen the nickname
                else if (Integer.parseInt(infos[1]) == 0) {
                	this.activeUsers.clear();
                    return false;
                }
                else {
                	System.out.println("Calling addUser(" + infos[2] + ") from " + this.currentUser.getNickname() +"'s messageManager");
                	this.addUser(new User(Integer.parseInt(infos[3]), infos[2], InetAddress.getByName(infos[5]), Integer.parseInt(infos[4])));
                }
            }
            communication.closeSocket();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.currentUser.setNickname(nickname);
        return true;
    }
    
    
    /*
     * Send a message to every active user to let them know that the current user has left the application
     * and is not active anymore
     */
    public void disconnect() throws UdpConnectionFailure {
    	int id = this.currentUser.getId();
    	String nickname = this.currentUser.getNickname();
    	Random random = new Random();
    	int broadcast_port = random.nextInt(65535 + 1 - 6000) + 6000; //send the message from a random port between 6000 and 65535
        String message = "disconnect_message:" + nickname + ":" + Integer.toString(id);
        try {
        	
        	UdpCommunication communication = new UdpCommunication();
        	
        	if (!communication.openSocket(broadcast_port))
        		throw (new UdpConnectionFailure("opening the socket"));
        	
        	//We chose port 5000 to receive all the connections and disconnection messages
        	if (!communication.broadcastMessage(message, 5000))
        		throw (new UdpConnectionFailure("authorizing broadcast"));
        	
        	communication.closeSocket();
        	
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*
     *   Call this method when a user who was already connected with an approved nickname
     *   wants to change his nickname
     *
     *   @param newNickname
     *          the nickname that the current user wants
     *
     *   @return false if the newNickname is already used, else true
     *   
     *   @throws UdpConnectionFailure if the connection could not be established to send the message
     */
    public boolean tryToChangeMyNickname(String newNickname) throws UdpConnectionFailure {
    	int id = this.currentUser.getId();
    	String nickname = this.currentUser.getNickname();
    	int port = this.currentUser.getPort();
        Random random = new Random();
    	int broadcast_port = random.nextInt(65535 + 1 - 6000) + 6000; //send the message from a random port between 6000 and 65535
        String message = "nickname_request:" + nickname + ":" + newNickname + ":" + Integer.toString(id) + ":" + Integer.toString(port);

        try {
        	
        	UdpCommunication communication = new UdpCommunication();
        	
        	if (!communication.openSocket(broadcast_port))
        		throw (new UdpConnectionFailure("opening the socket"));
        	
        	//We chose port 5000 to receive all the connections and disconnection messages
        	if (!communication.broadcastMessage(message, 5000))
        		throw (new UdpConnectionFailure("authorizing broadcast"));
        
            ArrayList<String> responses = communication.receiveMessages(TIMEOUT_RECEPTION_REPONSE);
            for (int i = 0; i < responses.size(); i++) {
                //Location in "infos"      0                                 1                                  2                         3                      4               5
                //Messages format : nickname_response:<0 if the sender uses the nickname, 1 otherwise>:<other user's nickname>:<other user's tcp port>:<Sender's IP Address>:<udp_port>
            	String[] infos = responses.get(i).split(":");
                //If the response doesn't have the right type, we ignore it
                if (!infos[0].equals("nickname_response")) {
                    continue;
                }
                //If the response is from ourself, we ignore it
                else if ((infos[4].equals(InetAddress.getLocalHost().toString())) && (this.currentUser.getPort() == Integer.parseInt(infos[3]))) {
                	continue;
                }
                //If infos[1] != 0 then another user has already chosen the nickname
                else if (Integer.parseInt(infos[1]) == 0) {
                	communication.closeSocket();
                	return false;
                }
            }
            
            String confirm_nickname_change_message = "nickname_change:" + nickname + ":" + newNickname + ":" + Integer.toString(id) + ":" + Integer.toString(port);
            if (!communication.broadcastMessage(confirm_nickname_change_message, 5000)) {
            	System.out.println("Could not confirm the nickname change. Aborting.");
            	communication.closeSocket();
            	return false;
            }
            
            communication.closeSocket();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.currentUser.setNickname(newNickname);
        return true;
    }
    
    /*
     * Change the nickname of another user in the active user list and in the graphic user list
     * 
     * @param oldNickname
     * 		  The nickname that the user was using
     * 
     * @param newNickname
     * 		  The nickname that he uses now
     * 
     * @throws UserNotFound if no user was found with the oldNickname (you should add him)
     */
    public void changeOtherUserNickname(String oldNickname, String newNickname) throws UserNotFound {
    	for (User user : this.activeUsers) {
    		if (user.getNickname().equals(oldNickname)) {
    			user.setNickname(newNickname);
    			mainWindow.removeUser(user);
    			mainWindow.addUser(user);
    			return;
    		}
    	}
    	throw (new UserNotFound());
    }

    /*
     *   Call this method once the user is connected AND has chosen a nickname that has been approved
     *   to listen for new connections and actualize the active user list regularly with a thread
     */
    public void listenForConnections(MainWindow mainWindow) {
    	this.mainWindow = mainWindow;
    	this.listener = new NotificationsListener(this);
        this.listener.start();
    }
    
    /*
     * Properly stop the listener thread and close the socket
     */
    public void stopListener() throws InterruptedException {
    	this.listener.stopListening();
    	this.listener.join();
    }
    
    /*
     * Once a user is connected, you will want to use this method to start a thread who will listen
     * for any TCP connection (chat request)
     */
    public void startChatManager() {
    	this.chatManager.start();
    }
    
    /*
     * Properly stop a particular chat (if the associated chat window was closed for example
     */
    public void stopChat(User user) throws InterruptedException {
    	this.chatManager.stopChat(user);
    }
    
    /*
     * Properly stop the chat manager, its server socket, and every chat running
     */
    public void stopChatManager() throws InterruptedException {
    	this.chatManager.stopChatManager();
    	this.chatManager.join();
    }
    
    /*
     * Use this method to start a tcp connection between the current user and another user.
     * You have to call this method before sending any message !
     * 
     * @param user
     * 		  The user the current user wants to chat with
     * 
     * @return false if the chat could not be started, else true
     * 
     * @throws ChatAlreadyExists if the chat was already started
     */
    public boolean startChat(User user) throws ChatAlreadyExists {
    	if (this.chatManager.doesUserChatWith(user)) {
    		throw (new ChatAlreadyExists(user));
    	}
    	else {
    		return this.getChatManager().startChat(user);
    	}
    }
    
    /*
     * Use this method to send a message to another user.
     * A chat must have been started between the two users.
     * 
     * @param user
     * 		  The user the current user wants to send a message to
     * 
     * @param message
     * 		  The message that the current user wants to send
     * 
     * @return false if the message could not be sent, else true
     * 
     * @throws ChatNotFound if no chat was previously started between the two users
     */
    public boolean sendMessage(User user, String message) throws ChatNotFound {
		return this.getChatManager().getChat(user).sendMessage(message);
    }

    /*
     *   Gets  all the messages that were sent between the two users
     *
     *   @param otherUser
     *
     *   @return the history of messages between the two users
     */
    public ArrayList<Message> getHistory(User otherUser) {
    	ArrayList<Message> messageList = new ArrayList<Message>();

        try {
            messageList = db.getHistory(this.currentUser.getId(), otherUser.getId());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        
        return messageList;
    }
    
    /*
     *   @return the user for which this messageManager instance is ran
     */
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    /*
     * @return the users currently active on the application
     */
    public ArrayList<User> getActiveUsers() {
    	return this.activeUsers;
    }
    
    /*
     * Get a particular user with his IP address and his port
     * 
     * @return the user that has been found
     * 
     * @throw UserNotFound if there was no such user currently active
     */
    public User getUser(InetAddress address, int port) throws UserNotFound {
    	for (User user : this.activeUsers) {
    		if ((user.getIpAddress().equals(address)) && (user.getPort() == port))
    			return user;
    	}
    	throw (new UserNotFound());
    }
    
    /*
     * Find a user in the active user list with his nickname
     * 
     * @param nickname
     * 		  The nickname that the user is supposed to have chosen
     * 
     * @throws UserNotFound if no such user was found in the active user list
     */
	public User getUserByNickname(String nickname) throws UserNotFound {

		for(User user : this.activeUsers) {
			if(user.getNickname().equals(nickname)) {
				return user;
			}
		}
		throw new UserNotFound();
	}
    
    /*
     * @return the associated Chat Manager
     */
    public ChatManager getChatManager() {
    	return this.chatManager;
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
     *   @param content
     *   		The actual message sent
     *
     */
    public void addMessage(User recipient, User transmitter, String content) {

        try {
			db.appendHistory(recipient.getId(), transmitter.getId(), content);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

    }
    
    /*
     *   Adds a user to the active users list (if it doesn't contain it already)
     *
     *   @param user
     *          the user you want to add
     */
    public void addUser(User user) {
        if (!activeUsers.contains(user)) {
        	if(mainWindow != null) {
            	System.out.println("refresh de la graphic user list de " + this.currentUser.getNickname() + " depuis add users");
            	mainWindow.addUser(user);        		
            }
            activeUsers.add(user);
        }
    }
    
    /*
     * Remove a user from the active user list
     * Also remove it from the graphic user list
     * 
     * @param user
     * 		  The user you want to remove
     * 
     * @throws UserNotFound if the user was not found in the active user list
     */
    public void removeUser(User user) throws UserNotFound {
    	mainWindow.removeUser(user);
    	for (int i = 0; i < this.activeUsers.size(); i++) {
    		if (user.getNickname().equals(this.activeUsers.get(i).getNickname())) {
    			this.activeUsers.remove(i);
    		}
    	}
    }
}
