package communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import controllers.MessageManagement;
import exceptions.ChatNotFound;
import exceptions.UserNotFound;
import models.User;
import views.ChatWindow;

public class ChatManager extends Thread {
	
	private boolean running;
	
	private int port;
	
	private ServerSocket serverSocket;
	private ArrayList<ChatCommunication> chats;
	private MessageManagement messageManagement;
	
	
	/*
	 * The Chat manager is used to initiate a thread for each chat (received or launch)
	 * 
	 * @param port
	 * 		  The port on which the current user wants to chat
	 */
	public ChatManager(int port, MessageManagement messageManagement) {
		this.messageManagement = messageManagement;
		this.port = port;
		try {
			this.serverSocket = new ServerSocket(this.port);
			this.chats = new ArrayList<ChatCommunication>();
		} catch (IOException e) {
			System.out.println("Error while creating ChatManager.");
		}
	}
	
	/*
	 * Use this method to initiate a chat session
	 * 
	 * @param user
	 * 		  The user the current user wants to chat with
	 * 
	 * @return false if the Chat could not be properly created and started
	 */
	public boolean startChat(User user) {
		try {
			System.out.println("Starting a chat between port " + this.port + " and port " + user.getPort());
			this.chats.add(new ChatCommunication(new Socket(user.getIpAddress(), user.getPort()), new ChatWindow(messageManagement, user), user.getNickname()));
			this.chats.get(this.chats.size() - 1).start();
			this.chats.get(this.chats.size() - 1).sendMessage(messageManagement.getCurrentUser().getNickname());
			return true;
		} catch (IOException e) {
			return false;
		} /*catch (ChatNotFound ce) {
			//This should not happen
			System.out.println("Error while trying to start the just created chat");
			return false;
		}*/
	}
	
	public boolean doesUserChatWith(User user) {
		for(ChatCommunication chat : chats) {
            if ((user.getIpAddress().equals(chat.getRemoteAddress())) && (user.getPort() == chat.getRemotePort())) {
            	return true;
            }
        }
		return false;
	}
	
	public ChatCommunication getChat(User user) throws ChatNotFound {
		for(ChatCommunication chat : chats) {
            if (user.getIpAddress().equals(chat.getRemoteAddress())) {
            	return chat;
            }
        }
		throw (new ChatNotFound(user));
	}
	
	/*
	 * Once this thread is ran, it will launch a thread for each TCP chat request
	 */
	public void run() {
		//Wait for TCP connections and launch a thread for each one of these
		this.running = true;
		while (running) {
			try {
				System.out.println("Waiting for connections on port " + this.port);
				this.chats.add(new ChatCommunication(this.serverSocket.accept(), new ChatWindow(messageManagement)));
				this.chats.get(this.chats.size() - 1).start();
				System.out.println("Connection received on port " + this.port);
			} catch (SocketException se) {
				System.out.println("Chat stopped on port " + this.port);
			} catch (IOException e) {
				System.out.println("Error while accepting a chat connection");
			}
		}
	}
	
	/*
	 * Use this method to stop the ChatManager thread
	 */
	public void stopChatManager() throws InterruptedException {
		for (ChatCommunication chat : chats) {
			chat.getChatWindow().disconnect();
			chat.stopCommunication();
			chat.join();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Could not properly stop the server socket");
		}
		running = false;
	}
	
	public void stopChat(User user) throws InterruptedException {
		for (int i = 0; i < this.chats.size(); i++) {
			if (user.getNickname().equals(chats.get(i).getOtherUser())) {
				chats.get(i).getChatWindow().disconnect();
				chats.get(i).stopCommunication();
				chats.get(i).join();
				chats.remove(i);
				return;
			}
		}
	}
}
