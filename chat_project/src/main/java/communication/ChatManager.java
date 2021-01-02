package communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import exceptions.ChatNotFoundException;
import models.User;

public class ChatManager extends Thread {
	
	private boolean running;
	
	private int port;
	
	private ServerSocket serverSocket;
	private ArrayList<ChatCommunication> chats;
	
	/*
	 * The Chat manager is used to initiate a thread for each chat (received or launch)
	 * 
	 * @param port
	 * 		  The port on which the current user wants to chat
	 */
	public ChatManager(int port) {
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
	 * @param address
	 * 		  The IP Address that the user want to chat with
	 * @param port
	 * 		  The port on which the user is listening for chats
	 */
	public void startChat(User user) {
		try {
			System.out.println("Starting a chat between port " + this.port + " and port " + user.getPort());
			this.chats.add(new ChatCommunication(new Socket(user.getIpAddress(), user.getPort())));
			this.chats.get(this.chats.size() - 1).start();
		} catch (IOException e) {
			System.out.println("Could not start the chat with " + user.getNickname());
		}
	}
	
	public ChatCommunication getChat(User user) throws ChatNotFoundException {
		for(ChatCommunication chat : chats) {
            if (user.getIpAddress().equals(chat.getAddress())) {
            	return chat;
            }
        }
		throw (new ChatNotFoundException(user));
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
				this.chats.add(new ChatCommunication(this.serverSocket.accept()));
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
	public void stopChatManager() {
		for (ChatCommunication chat : chats) {
			chat.stopCommunication();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Could not properly stop the server socket");
		}
		running = false;
	}
}
