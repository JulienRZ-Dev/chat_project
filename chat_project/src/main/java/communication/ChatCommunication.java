package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ChatCommunication {
	
	private boolean running = false;
	
	private Socket socket;
	
	public ChatCommunication(Socket socket) {
		this.socket = socket;
	}
	
	public void sendMessage(String message) {
		//Send a message
	}
	
	public void run() {
		running = true;
		while (running) {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String message = "";
				String line;
				while((line=reader.readLine()) != null){  
					message.concat(line);
			    }
				//Give the message to the correct class
				
			} catch (IOException e) {
				System.out.println("Erreur lors de la réception d'un message");
			}
		}
	}
	
}
