package views;

import java.net.InetAddress;

import javax.swing.JFrame;

import controllers.MessageManagement;
import models.User;

public class MainWindow {
	
	private MessageManagement messageManagement;
    
    public MainWindow(MessageManagement messageManagement) {
    	this.messageManagement = messageManagement;
    	new GraphicUserList(messageManagement);
    }
}
