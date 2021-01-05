package views;

import javax.swing.JFrame;

import controllers.MessageManagement;
import models.User;

public class ChatWindow {
	
	private MessageManagement messageManagement; 
	private User user;
	
	public ChatWindow(MessageManagement messageManagement, User user) {
		this.messageManagement = messageManagement;
		this.user = user;
		
		windowConfiguration();
	}
	
	public void windowConfiguration() {
	   	 JFrame f= new JFrame();  
		 
	   	 f.setSize(1800,1000);  
	   	 f.setLayout(null);  
	   	 f.setVisible(true); 
	}
}
