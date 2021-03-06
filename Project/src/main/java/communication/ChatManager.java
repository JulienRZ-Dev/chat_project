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
			System.out.println("Starting a chat connection between port " + this.port + " and port " + user.getChatPort() + " (" + user.getNickname() + ")");
			this.chats.add(new ChatCommunication(new Socket(user.getIpAddress(), user.getChatPort()), new ChatWindow(messageManagement, user), user.getNickname()));
			this.chats.get(this.chats.size() - 1).start();
			this.chats.get(this.chats.size() - 1).sendMessage(messageManagement.getCurrentUser().getNickname());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	/*
	 * Tells if a chat already exists with the user
	 * 
	 * @param user
	 * 		  The one you want to check
	 * 
	 * @return true if the current user already chats with the user, else false
	 */
	public boolean doesUserChatWith(User user) {
		for(ChatCommunication chat : chats) {
            if ((user.getIpAddress().equals(chat.getRemoteAddress())) && (user.getChatPort() == chat.getRemotePort())) {
            	return true;
            }
        }
		return false;
	}
	
	public void changeChatNickname(String oldNickname, String newNickname) {
		for(ChatCommunication chat : chats) {
            if (oldNickname.equals(chat.getOtherUser())) {
            	chat.setNickname(newNickname);
            }
        }
	}
	
	public ChatCommunication getChat(User user) throws ChatNotFound {
		for(ChatCommunication chat : chats) {
            if (user.getNickname().equals(chat.getOtherUser())) {
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
		System.out.println("Waiting for chats on port " + this.port);
		while (running) {
			try {
				this.chats.add(new ChatCommunication(this.serverSocket.accept(), new ChatWindow(messageManagement)));
				this.chats.get(this.chats.size() - 1).start();
				System.out.println("Chat connection received on port " + this.port);
			} catch (SocketException se) {
				System.out.println("Chat stopped on port " + this.port);
			} catch (IOException e) {
				System.out.println("Error while accepting a chat connection");
			}
		}
	}
	
	/*
	 * Use this method to stop the ChatManager thread and all the chat threads running
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
	
	/*
	 * Stops a particular chat only
	 * 
	 * @param user
	 * 		  The user in the chat that we want to stop
	 * 
	 * @throws InterruptedException if the chat was already stopped
	 */
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
