package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ChatCommunication extends Thread {
	
	private boolean running = false;
	
	private Socket socket;
	
	public ChatCommunication(Socket socket) {
		this.socket = socket;
		System.out.println("A socket has been opened between port " + this.socket.getLocalPort() + " and port " + this.socket.getPort());
	}
	
	public void sendMessage(String message) {
		//System.out.println(this.socket.getLocalAddress().toString() + " has sent a message on port " + this.socket.getLocalPort() + " :\n" + message + "\nThis message was sent to " + this.socket.getInetAddress().toString() + " on port " + this.socket.getPort() + "\n");
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.socket.getOutputStream(), true);
			writer.write(message+"\n");
	        writer.flush();
		} catch (IOException e) {
			System.out.println("Could not create a PrintWriter while sending a message");
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
				}
			} catch (SocketException se) {
				System.out.println("Chat communication stopped between port " + this.socket.getLocalPort() + " and port " + this.socket.getPort());
			} catch (IOException e) {
				System.out.println("Error while receiving a message");
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
	
	public InetAddress getAddress() {
		return this.socket.getInetAddress();
	}
	
}
