package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import controllers.MessageManagement;
import models.User;
import views.ChatWindow;
import views.SendFileWindow;

public class FileTransferManager extends Thread {

	private boolean running;
	
	private int port;
	
	private ServerSocket serverSocket;
	private ArrayList<FileTransfer> transfers;
	private MessageManagement messageManagement;
	
	
	/*
	 * The manager is used to initiate a thread for each transfer request (received or launch)
	 * 
	 * @param port
	 * 		  The port on which the current user wants to receive transfer requests
	 */
	public FileTransferManager(int port, MessageManagement messageManagement) {
		this.messageManagement = messageManagement;
		this.port = port;
		try {
			this.serverSocket = new ServerSocket(this.port);
			this.transfers = new ArrayList<FileTransfer>();
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
	public boolean startTransfer(User user) {
		try {
			System.out.println("Starting a file transfer between port " + this.port + " and port " + user.getChatPort());
			this.transfers.add(new FileTransfer(new Socket(user.getIpAddress(), user.getChatPort()), new SendFileWindow(user), user.getNickname()));
			this.transfers.get(this.transfers.size() - 1).sendNickname(messageManagement.getCurrentUser().getNickname());
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/*
	 * Once this thread is ran, it will launch a thread for each TCP transfer request
	 */
	public void run() {
		//Wait for TCP connections and launch a thread for each one of these
		this.running = true;
		while (running) {
			try {
				System.out.println("Waiting for connections on port " + this.port);
				this.transfers.add(new FileTransfer(this.serverSocket.accept()));
				this.transfers.get(this.transfers.size() - 1).openGetFileWindow();
				System.out.println("Connection received on port " + this.port);
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
		for (FileTransfer transfer : transfers) {
			transfer.stopTransfer();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Could not properly stop the server socket");
		}
		running = false;
	}
}
