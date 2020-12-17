package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatManager extends Thread {
	
	private boolean running;
	
	private ServerSocket serverSocket;
	private ArrayList<ChatCommunication> chats;
	
	public ChatManager(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
			this.chats = new ArrayList<ChatCommunication>();
		} catch (IOException e) {
			System.out.println("Error while creating ChatManager.");
		}
	}
	
	public void startChat() {
		//Initiate a tcp connection
	}
	
	public void run() {
		//Wait for tcp connections and launch a thread for each one of these
		this.running = true;
		while (running) {
			try {
				chats.add(new ChatCommunication(serverSocket.accept()));
			} catch (IOException e) {
				System.out.println("Error while accepting a chat connection")
			}
		}
	}
	
	public void stopChatManager() {
		running = false;
	}
}
