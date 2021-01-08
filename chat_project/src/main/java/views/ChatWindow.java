package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import controllers.MessageManagement;
import exceptions.ChatNotFound;
import exceptions.UserNotFound;
import models.User;

public class ChatWindow {

	// Graphical Logic
    ChatWindow  chatwindow;
    String		appName = "";
    JFrame      frame;
    JButton     sendButton;
    JTextField  messageBox;
    JPanel      chatBox;
    
    // Backend Logic
    MessageManagement messageManagement;
    User currentUser;
    User otherUser;
    
	private WindowAdapter windowAdapter;
    
    public ChatWindow(MessageManagement messageManagement, User user) {
    	
    	this.messageManagement = messageManagement;
    	this.currentUser = messageManagement.getCurrentUser();
    	this.otherUser = user;
    	this.appName = "Clavardage avec " + user.getNickname();
      	display();
    }
    
    public ChatWindow(MessageManagement messageManagement) {
    	this.messageManagement = messageManagement;
    	this.currentUser = messageManagement.getCurrentUser();
    }
    
    
    public void setUser(String nickname) throws UserNotFound {
    	this.otherUser = messageManagement.getUserByNickname(nickname);
    	this.appName = "Clavardage avec " + nickname;
    
    	display(); // The app should be displayed only when the other user has been fetched
    }



    public void display() {
    	
    	frame = new JFrame(appName);
    	
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();

        sendButton = new JButton("Send Message");
        sendButton.addActionListener(new sendMessageButtonListener());

        chatBox = new JPanel();
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));

        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendButton, right);

        mainPanel.add(BorderLayout.SOUTH, southPanel);

        frame.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(470, 300);
        frame.setVisible(true);
        
        //Define what to do when we close the window
        this.windowAdapter = new WindowAdapter() {

            // WINDOW_CLOSED event handler
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                	messageManagement.stopChat(otherUser);
                	disconnect();
                } catch (InterruptedException e1) {
                	System.out.println("Chat already stopped");
                }
            }
            
        };
        this.frame.addWindowListener(windowAdapter);
        
    }
    
    
    public void printMessage(String message) {
        chatBox.add(new JLabel("<html><font color='blue'>" + otherUser.getNickname() + "</font>:  " + message + "<br></html>"));
    }
    
    public void disconnect() {
    	try {
			this.messageManagement.sendMessage(otherUser, "#disconnect#");
		} catch (ChatNotFound e) {
			System.out.println("Chat already closed.");
		}
    	this.frame.dispose();
    }
    
    public void printDisconnectMessage() {
    	chatBox.add(new JLabel("<html><font color='red'>The other user has closed the chat.</font><br></html>"));
    }

    class sendMessageButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
                // do nothing
            } else {
            	chatBox.add(new JLabel("<html><font color='green'>" + currentUser.getNickname() + "</font>:  " + messageBox.getText() + "<br></html>"));
                try {
					messageManagement.sendMessage(otherUser, messageBox.getText());
				} catch (ChatNotFound e) {
					System.out.println("You have been disconnected from the chat...");
				}
                messageBox.setText("");
            }
            messageBox.requestFocusInWindow();
        }
    }
}