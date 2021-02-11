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

import controllers.MessageManagement;
import views.GetFileWindow;
import views.SendFileWindow;

public class FileTransfer extends Thread {

	private Socket socket;
	private boolean awaitconfig;
	private String otherUser;
	private MessageManagement messageManager;
	
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
	public boolean openSendFileWindow(String nickname) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(this.socket.getOutputStream(), true);
			writer.write(nickname+"\n");
	        writer.flush();
	        this.sendFileWindow = new SendFileWindow(this.otherUser, this, this.messageManager);
	        return true;
		} catch (IOException e) {
			//System.out.println("Could not create a PrintWriter while sending a message");
			return false;
		}
	}
	
	public void sendFile(File file) throws FileNotFoundException {
		int bytes = 0;
		FileInputStream input = new FileInputStream(file);
		
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
		this.getFileWindow = getFileWindow;
		this.socket = socket;
		this.otherUser = otherUser;
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
        
        fileOutputStream = new FileOutputStream(new File("./in_file" + ZonedDateTime.now()));
        
		try {
	        DataInputStream dataInputStream = new DataInputStream(this.socket.getInputStream());
	        
	        long size = dataInputStream.readLong();     // read file size
	        byte[] buffer = new byte[4*1024];
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
