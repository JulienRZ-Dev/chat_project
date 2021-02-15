package communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.UUID;

import controllers.MessageManagement;
import views.GetFileWindow;
import views.SendFileWindow;

public class FileTransfer extends Thread {

	private Socket socket;
	private String otherUser;
	private MessageManagement messageManager;
	
	private String myName;
	
	private SendFileWindow sendFileWindow;
	private GetFileWindow getFileWindow;
	
	public FileTransfer(Socket socket, String otherUser, MessageManagement messageManager) {
		this.socket = socket;
		this.otherUser = otherUser;
		this.messageManager = messageManager;
	}
	
	/*
	 * Use this method to send the user's nickname to the user who will receive the file
	 * 
	 * @param nickname
	 * 		  The user wishing to send a file
	 */
	public void openSendFileWindow(String nickname) {
		this.myName = nickname;
	    this.sendFileWindow = new SendFileWindow(this.otherUser, this, this.messageManager);
	}
	
	public void sendFile(File file) throws FileNotFoundException {
		int bytes = 0;
		FileInputStream input = new FileInputStream(file);
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.socket.getOutputStream(), true);
			writer.write(this.myName +"\n");
	        writer.flush();
		} catch (IOException e1) {
			System.out.println("Could not connect to the other user");
		}
		
		try {
			DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
			
			// send file size
	        dataOutputStream.writeLong(file.length());  
	        // break file into chunks
	        byte[] buffer = new byte[4*1024];
	        while ((bytes=input.read(buffer))!=-1){
	            dataOutputStream.write(buffer,0,bytes);
	            dataOutputStream.flush();
	        }
	        input.close();
		}
		catch (IOException e) {
			System.out.println("Could not send the file");
		}
	}
	
	//Reception
	public FileTransfer(Socket socket) {
		this.socket = socket;
	}
	
	public void openGetFileWindow() {
		try {
			System.out.println("Waiting for nickname (transfer)");
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message = reader.readLine();
			System.out.println("Nickname received : " + message);
			this.otherUser = message;
			this.getFileWindow = new GetFileWindow(this.otherUser, this, this.messageManager);
		} catch (IOException e1) {
			System.out.println("Could not discover who sent the file");
		}
	}
	
	public void receiveFile() throws FileNotFoundException {
		int bytes = 0;
        FileOutputStream fileOutputStream;
        
        String uuid = UUID.randomUUID().toString();
        File new_file = new File("./" + uuid + ".txt");
        
		try {
			System.out.println("Trying to create the file " + new_file.getName());
			new_file.createNewFile();
			System.out.println("The file " + new_file.getName() + " was created");
	        
	        fileOutputStream = new FileOutputStream(new_file);
			
	        DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());
	        
	        long size = dataInputStream.readLong();     // read file size
	        System.out.println("Le fichier reçu est de taille : " + size);
	        byte[] buffer = new byte[100*1024];
	        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
	            fileOutputStream.write(buffer,0,bytes);
	            size -= bytes;      // read upto file size
	        }
	        fileOutputStream.close();
		} catch (IOException e) {
			System.out.println("Could not receive the file");
		}
    }

	public void stopTransfer() {
		try {
			this.socket.close();
		} catch (IOException e) {
			System.out.println("Could not properly stop the communication with " + this.socket.getInetAddress().toString());
		}
	}
	
	public String getOtherUser() {
		return this.otherUser;
	}
}
