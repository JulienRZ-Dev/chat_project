package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import controllers.MessageManagement;
import exceptions.UserNotFound;
import views.ChatWindow;

public class ChatCommunication extends Thread {
	
	private boolean running = false;
	
	private Socket socket;
	private boolean awaitconfig = true;
	private ChatWindow chatWindow;
	private String otherUser;
	
	
	public ChatCommunication(Socket socket, ChatWindow chatWindow, String otherUser) {
		this.chatWindow = chatWindow;
		this.socket = socket;
		this.otherUser = otherUser;
		this.awaitconfig = false;
	}
	
	public ChatCommunication(Socket socket, ChatWindow chatWindow) {
		this.chatWindow = chatWindow;
		this.socket = socket;
		this.awaitconfig = true;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public boolean sendMessage(String message) {
		//System.out.println(this.socket.getLocalAddress().toString() + " has sent a message on port " + this.socket.getLocalPort() + " :\n" + message + "\nThis message was sent to " + this.socket.getInetAddress().toString() + " on port " + this.socket.getPort() + "\n");
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.socket.getOutputStream(), true);
			writer.write(message+"\n");
	        writer.flush();
	        return true;
		} catch (IOException e) {
			//System.out.println("Could not create a PrintWriter while sending a message");
			return false;
		}
	}
	
	public void run() {
		this.running = true;
		
		while (this.running) {
			try {
				//System.out.println("Waiting for messages on port " + this.socket.getLocalPort());
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = reader.readLine();
				//Give the message to the correct class
				if (message != null) {
					System.out.println(this.socket.getLocalAddress().toString() + " has received a message on port " + this.socket.getLocalPort() + " :\n" + message + "\n");
					if(this.awaitconfig) {
						chatWindow.setUser(message);
						this.otherUser = message;
						this.awaitconfig = false;
					} else {
						chatWindow.printMessage(message);
					}
					
				}
			} catch (SocketException se) {
				System.out.println("Chat communication stopped between port " + this.socket.getLocalPort() + " and port " + this.socket.getPort());
				//TODO
				//Stop the related ChatWindow
			} catch (IOException e) {
				System.out.println("Error while receiving a message");
			} catch (UserNotFound e) {
				System.out.println("No user active for this nickname");
			}
		}
	}
	
	public void stopCommunication() {
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Could not properly stop the communication with " + this.socket.getInetAddress().toString());
		}
		this.running = false;
	}
	
	public InetAddress getRemoteAddress() {
		return this.socket.getInetAddress();
	}
	
	public int getRemotePort() {
		return this.socket.getPort();
	}
	
	public String getOtherUser() {
		return this.getOtherUser();
	}
	
}
