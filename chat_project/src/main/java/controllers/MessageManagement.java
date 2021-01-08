package controllers;

import communication.ChatManager;
import communication.NotificationsListener;
import exceptions.ChatAlreadyExists;
import exceptions.ChatNotFound;
import exceptions.UdpConnectionFailure;
import exceptions.UserNotFound;
import models.Message;
import models.User;
import views.GraphicUserList;
import communication.UdpCommunication;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Random;

import javax.lang.model.element.ModuleElement.UsesDirective;

public class MessageManagement {

    private final int TIMEOUT_RECEPTION_REPONSE = 300;

    private User currentUser;
    private ArrayList<User> activeUsers = new ArrayList<User>();
    private GraphicUserList userList;
    
    private NotificationsListener listener;
    private ChatManager chatManager;

    public MessageManagement(User currentUser) {
        this.currentUser = currentUser;
        this.chatManager = new ChatManager(this.currentUser.getPort(), this);
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
        	if (!communication.openSocket(broadcast_port, InetAddress.getLocalHost()))
        		throw (new UdpConnectionFailure("opening the socket"));
        	//We chose port 5000 to receive all the connections and disconnection messages
        	if (!communication.broadcastMessage(message, 5000))
        		throw (new UdpConnectionFailure("authorizing broadcast"));
        
            ArrayList<String> responses = communication.receiveMessages(TIMEOUT_RECEPTION_REPONSE);
            for (int i = 0; i < responses.size(); i++) {
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
    
    public void disconnect() throws UdpConnectionFailure {
    	int id = this.currentUser.getId();
    	String nickname = this.currentUser.getNickname();
    	Random random = new Random();
    	int broadcast_port = random.nextInt(65535 + 1 - 6000) + 6000; //send the message from a random port between 6000 and 65535
        String message = "disconnect_message:" + nickname + ":" + Integer.toString(id);
        try {
        	
        	UdpCommunication communication = new UdpCommunication();
        	
        	if (!communication.openSocket(broadcast_port, InetAddress.getLocalHost()))
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
     */
    private boolean tryToChangeMyNickname(String newNickname) throws UdpConnectionFailure {
    	int id = this.currentUser.getId();
    	String nickname = this.currentUser.getNickname();
    	int port = this.currentUser.getPort();
        Random random = new Random();
    	int broadcast_port = random.nextInt(65535 + 1 - 6000) + 6000; //send the message from a random port between 6000 and 65535
        String message = "nickname_change:" + nickname + ":" + newNickname + ":" + Integer.toString(id) + ":" + Integer.toString(port);

        try {
        	
        	UdpCommunication communication = new UdpCommunication();
        	
        	if (!communication.openSocket(broadcast_port, InetAddress.getLocalHost()))
        		throw (new UdpConnectionFailure("opening the socket"));
        	
        	//We chose port 5000 to receive all the connections and disconnection messages
        	if (!communication.broadcastMessage(message, 5000))
        		throw (new UdpConnectionFailure("authorizing broadcast"));
        
            ArrayList<String> responses = communication.receiveMessages(TIMEOUT_RECEPTION_REPONSE);
            for (int i = 0; i < responses.size(); i++) {
                //Location in "infos"      0                                 1                                 2                3
                //Messages format : nickname_response:<0 if the sender uses the nickname, 1 otherwise><Sender's IP Address>:<udp_port>
            	String[] infos = responses.get(i).split(":");
                //If the response doesn't have the right type, we ignore it
                if (!infos[0].equals("nickname_response")) {
                    continue;
                }
                //If the response is from ourself, we ignore it
                else if ((infos[5].equals(InetAddress.getLocalHost().toString())) && (this.currentUser.getPort() == Integer.parseInt(infos[4]))) {
                	continue;
                }
                //If infos[1] != 0 then another user has already chosen the nickname
                else if (Integer.parseInt(infos[1]) == 0) {
                    return false;
                }
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
     *   Call this method once the user is connected AND has chosen a nickname that has been approved
     *   to listen for new connections and actualize the active user list regularly with a thread
     */
    public void listenForConnections(GraphicUserList userList) {
    	this.userList = userList;
    	this.listener = new NotificationsListener(this);
        this.listener.start();
    }
    
    public void startChatManager() {
    	this.chatManager.start();
    }
    
    public void stopListener() throws InterruptedException {
    	this.listener.stopListening();
    	this.listener.join();
    }
    
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
    
    public void stopChat(User user) throws InterruptedException {
    	this.chatManager.stopChat(user);
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
     *   Broadcast a message to everyone on the local network to notify that the user is not active anymore
     */
    public void SignOutUser() {

    }

    /*
     *   @return the user for which this messageManager instance is ran
     */
    public User getCurrentUser() {
        return this.currentUser;
    }
    
    public ArrayList<User> getActiveUsers() {
    	return this.activeUsers;
    }
    
    public User getUser(InetAddress address, int port) throws UserNotFound {
    	for (User user : this.activeUsers) {
    		if ((user.getIpAddress().equals(address)) && (user.getPort() == port))
    			return user;
    	}
    	throw (new UserNotFound());
    }
    
    public ChatManager getChatManager() {
    	return this.chatManager;
    }

    /*
     *   Adds a user to the active users list (if it doesn't contain it already)
     *
     *   @param user
     *          the user you want to add
     */
    public void addUser(User user) {
        if (!activeUsers.contains(user)) {
        	if(userList != null) {
            	System.out.println("refresh de la graphic user list de " + this.currentUser.getNickname() + " depuis add users");
            	userList.addUser(user);        		
            }
            activeUsers.add(user);
        }
    }
    
    public void removeUser(User user) throws UserNotFound {
    	userList.removeUser(user);
    	for (int i = 0; i < this.activeUsers.size(); i++) {
    		if (user.getNickname().equals(this.activeUsers.get(i).getNickname())) {
    			this.activeUsers.remove(i);
    		}
    	}
    }


	public User getUserByNickname(String nickname) throws UserNotFound {

		for(User user : this.activeUsers) {
			if(user.getNickname().equals(nickname)) {
				return user;
			}
		}
		throw new UserNotFound();
	}
    
    public void changeOtherUserNickname(String oldNickname, String newNickname) throws UserNotFound {
    	for (User user : this.activeUsers) {
    		if (user.getNickname().equals(oldNickname)) {
    			user.setNickname(newNickname);
    			return;
    		}
    	}
    	throw (new UserNotFound());
    }
    
//    public static void main(String[] a) {
//    	try {
//			MessageManagement messageManager1 = new MessageManagement(new User(1, InetAddress.getLocalHost(), 6001));
//			MessageManagement messageManager2 = new MessageManagement(new User(2, InetAddress.getLocalHost(), 6002));
//			MessageManagement messageManager3 = new MessageManagement(new User(3, InetAddress.getLocalHost(), 6003));
//			
//			messageManager1.listenForConnections();
//			
//			if (!messageManager1.isNicknameAvailable("Celestin")) {
//				System.out.println("Celestin is unavailable for 1");
//			}
//			else {
//				messageManager1.startChatManager();
//			}
//			
//			System.out.println("Active users list for 1 : " + messageManager1.getActiveUsers().toString());
//			System.out.println("Active users list for 2 : " + messageManager2.getActiveUsers().toString());
//			System.out.println("Active users list for 3 : " + messageManager3.getActiveUsers().toString());
//			System.out.println();
//			
//			if (!messageManager2.isNicknameAvailable("Julien")) {
//				System.out.println("Julien is unavailable for 2");
//			}
//			else {
//				messageManager2.startChatManager();
//			}
//			
//			System.out.println("Active users list for 1 : " + messageManager1.getActiveUsers().toString());
//			System.out.println("Active users list for 2 : " + messageManager2.getActiveUsers().toString());
//			System.out.println("Active users list for 3 : " + messageManager3.getActiveUsers().toString());
//			System.out.println();
//			
//			if (!messageManager3.isNicknameAvailable("Celestin")) {
//				System.out.println("Celestin is unavailable for 3");
//			}
//			else {
//				messageManager3.startChatManager();
//			}
//
//			System.out.println("Active users list for 1 : " + messageManager1.getActiveUsers().toString());
//			System.out.println("Active users list for 2 : " + messageManager2.getActiveUsers().toString());
//			System.out.println("Active users list for 3 : " + messageManager3.getActiveUsers().toString());
//			System.out.println();
//			
//			if (!messageManager3.isNicknameAvailable("Robert")) {
//				System.out.println("Robert is unavailable for 3");
//			}
//			else {
//				messageManager3.startChatManager();
//			}
//
//			System.out.println("Active users list for 1 : " + messageManager1.getActiveUsers().toString());
//			System.out.println("Active users list for 2 : " + messageManager2.getActiveUsers().toString());
//			System.out.println("Active users list for 3 : " + messageManager3.getActiveUsers().toString());
//			System.out.println();
//			
//			System.out.println("TCP Communication tests (chat tests)");
//			
//			try {
//				//messageManager1.getActiveUsers().get(0) should be Julien
//				if (!messageManager1.startChat(messageManager1.getActiveUsers().get(0))) {
//					System.out.println("Could not start the chat with Julien");
//				}
//			
//				Thread.sleep(2000);
//				
//				messageManager2.sendMessage(messageManager2.getActiveUsers().get(0), "Hey");
//				Thread.sleep(1000);
//				messageManager1.sendMessage(messageManager1.getActiveUsers().get(0), "Cv?");
//				Thread.sleep(1000);
//				messageManager2.sendMessage(messageManager2.getActiveUsers().get(0), "Cv et toi ?");
//				Thread.sleep(1000);
//				messageManager1.sendMessage(messageManager1.getActiveUsers().get(0), "Trkl trkl");
//				Thread.sleep(1000);
//				messageManager2.sendMessage(messageManager2.getActiveUsers().get(0), "");
//			
//				Thread.sleep(2000);
//				
//			} catch (ChatNotFound e) {
//				System.out.println(e.getMessage());
//			} catch (ChatAlreadyExists e) {
//				System.out.println(e.getMessage());
//			}
//			
//			messageManager1.stopListener();
//			messageManager1.stopChatManager();
//			messageManager2.stopChatManager();
//			messageManager3.stopChatManager();
//			
//		} catch (UnknownHostException e) {
//			System.out.println("Impossible to get local address.");
//		} catch (UdpConnectionFailure e) {
//			System.out.println("Udp error.");
//		} catch (InterruptedException e) {
//			System.out.println("Interrupted exception");
//		}
//    }
    
    //Tests avec l'ordinateur de Eva
    /*public static void main(String[] a) {
    	try {
			MessageManagement messageManager1 = new MessageManagement(new User(2, InetAddress.getLocalHost(), 6002));
			
			messageManager1.listenForConnections();
			
			if (!messageManager1.isNicknameAvailable("Celestin")) {
				System.out.println("Celestin is unavailable for 2");
			}
			else {
				messageManager1.startChatManager();
			}
			
			System.out.println("Active users list for 2 : " + messageManager1.getActiveUsers().toString());
			System.out.println();
			
			if (!messageManager1.isNicknameAvailable("Julien")) {
				System.out.println("Julien is unavailable for 2");
			}
			else {
				messageManager1.startChatManager();
			}
			
			System.out.println("Active users list for 2 : " + messageManager1.getActiveUsers().toString());
			System.out.println();
			
			Thread.sleep(1000);
			
			while(true) {
				messageManager1.getChatManager().getChat(messageManager1.getActiveUsers().get(0)).sendMessage("HEY de Julien");
			}
			
		} catch (UnknownHostException e) {
			System.out.println("Impossible to get local address.");
		} catch (UdpConnectionFailure e) {
			System.out.println("Udp error.");
		} catch (ChatNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }*/
    
}
