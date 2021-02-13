package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import controllers.MessageManagement;
import models.User;
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
			System.out.println("Error while creating FileTransferManager.");
		}
	}
	
	/*
	 * Use this method to initiate a transfer
	 * 
	 * @param user
	 * 		  The user the current user wants to transfer a file to
	 * 
	 * @return false if the file could not be properly created and started
	 */
	public boolean startTransfer(User user) {
		try {
			System.out.println("Starting a file transfer between port " + this.port + " and port " + user.getFilePort());
			this.transfers.add(new FileTransfer(new Socket(user.getIpAddress(), user.getFilePort()), user.getNickname(), this.messageManagement));
			this.transfers.get(this.transfers.size() - 1).openSendFileWindow(messageManagement.getCurrentUser().getNickname());
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
				System.out.println("Waiting for transfers on port " + this.port);
				this.transfers.add(new FileTransfer(this.serverSocket.accept()));
				this.transfers.get(this.transfers.size() - 1).openGetFileWindow();
				System.out.println("Connection received on port " + this.port);
			} catch (SocketException se) {
				System.out.println("Transfer stopped on port " + this.port);
			} catch (IOException e) {
				System.out.println("Error while accepting a transfer");
			}
		}
	}
	
	/*
	 * Use this method to stop the FileTransferManager thread and all the transfers running
	 */
	public void stopTransferManager() throws InterruptedException {
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
	
	public void stopTransfer(String otherUser) {
		for (int i = 0; i < transfers.size(); i++) {
			if (transfers.get(i).getOtherUser().equals(otherUser)) {
				transfers.get(i).stopTransfer();
				transfers.remove(i);
				return;
			}
		}
	}
}
