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
    JFrame      newFrame;
    JButton     sendButton;
    JTextField  messageBox;
    JTextArea   chatBox;
    
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
    	
    	newFrame = new JFrame(appName);
    	
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();

        sendButton = new JButton("Send Message");
        sendButton.addActionListener(new sendMessageButtonListener());

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);

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

        newFrame.add(mainPanel);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setSize(470, 300);
        newFrame.setVisible(true);
        
        //Define what to do when we close the window
        this.windowAdapter = new WindowAdapter() {
            // WINDOW_CLOSING event handler
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                	messageManagement.stopChat(otherUser);
                } catch (InterruptedException e1) {
                	System.out.println("Chat already stopped");
                }
            }

            // WINDOW_CLOSED event handler
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                // Close application if you want to with System.exit(0)
                // but don't forget to dispose of all resources 
                // like child frames, threads, ...
                // System.exit(0);
            }
        };
    }
    
    
    public void printMessage(String message) {
        chatBox.append("<" + otherUser.getNickname() + ">:  " + message + "\n");
    }
    

    class sendMessageButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
                // do nothing
            } else if (messageBox.getText().equals(".clear")) {
                chatBox.setText("Cleared all messages\n");
                messageBox.setText("");
            } else {
                chatBox.append("<" + currentUser.getNickname() + ">:  " + messageBox.getText() + "\n");
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