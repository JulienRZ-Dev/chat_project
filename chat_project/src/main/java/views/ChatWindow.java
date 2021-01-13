package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import models.Message;
import models.User;

public class ChatWindow {

	// Graphical Logic
    ChatWindow  chatwindow;
    String		appName = "";
    JFrame      frame;
    JButton     sendButton;
    JTextField  messageBox;
    TextArea chatBox;
    ArrayList<Message> history;
    
    // Backend Logic
    MessageManagement messageManager;
    User currentUser;
    User otherUser;
    
	private WindowAdapter windowAdapter;
    
    public ChatWindow(MessageManagement messageManager, User user) {
    	
    	this.messageManager = messageManager;
    	this.currentUser = this.messageManager.getCurrentUser();
    	this.otherUser = user;
    	this.appName = "Clavardage avec " + user.getNickname();
    	
    	history = this.messageManager.getHistory(this.otherUser);
    	display();
        for (Message message : history) {
            if (message.getTransmitter() == this.currentUser.getId()) {
            	chatBox.append(currentUser.getNickname() + " <" + message.getDate().toString() + "> : " + message.getContent() + "\n");
            }
            else {
            	chatBox.append(otherUser.getNickname() + " <" + message.getDate().toString() + "> : " + message.getContent() + "\n");
            }
        }
    }
    
    public ChatWindow(MessageManagement messageManagement) {
    	this.messageManager = messageManagement;
    	this.currentUser = messageManagement.getCurrentUser();
    }
    
    /*
     * Use this method if the ChatWindow could not be created with the otherUser
     * This way, it will refresh the chatWindow and open it with every information required
     * 
     * @param nickname
     * 		  The nickname of the other user
     * 
     * @throws UserNotFound if the nickname was not associated with any active user
     */
    public void setUser(String nickname) throws UserNotFound {
    	this.otherUser = messageManager.getUserByNickname(nickname);
    	this.appName = "Clavardage avec " + nickname;
    	history = this.messageManager.getHistory(this.otherUser);
    	display(); // The app should be displayed only when the other user has been fetched
        for (Message message : history) {
            if (message.getTransmitter() == this.currentUser.getId()) {
            	chatBox.append(currentUser.getNickname() + " <" + message.getDate().toString() + "> : " + message.getContent() + "\n");
            }
            else {
            	chatBox.append(otherUser.getNickname() + " <" + message.getDate().toString() + "> : " + message.getContent() + "\n");
            }
        }
    }
    
    /*
     * Prints a received message in the chatWindow
     * 
     * @param date
     *           The moment the message was sent
     * 
     * @param message
     *           The message you want to print
     */
    public void printReceivedMessage(Timestamp date, String message) {
    	Timestamp ts = date;
    	Date dateDate = new Date();
    	date.setTime(ts.getTime());
    	String formattedDate = new SimpleDateFormat("yyyy:MM:dd:hh:mm").format(dateDate);
    	messageManager.addMessage(currentUser, otherUser, message);
    	chatBox.append(otherUser.getNickname() + " <" + formattedDate + "> " + ": " + message + "\n");
    }
    
    
    /*
     * Prints a sent message in the chatWindow
     * 
     * @param date
     *        The moment the message was sent
     * 
     * @param message
     *        The message you want to print
     */
    public void printSentMessage(Timestamp date, String message) {
    	Timestamp ts = date;
    	Date dateDate = new Date();
    	date.setTime(ts.getTime());
    	String formattedDate = new SimpleDateFormat("yyyy:MM:dd:hh:mm").format(dateDate);
    	messageManager.addMessage(otherUser, currentUser, message);
    	chatBox.append(currentUser.getNickname() + " <" + formattedDate + "> " + ": " + message + "\n");
    }


    /*
     * Use this method to display the chatWindow with every information required
     */
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

        chatBox = new TextArea();
        
        mainPanel.add(new JScrollPane(chatBox));

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
        frame.setSize(600, 400);
        frame.setVisible(true);
        
        
        
        //Define what to do when we close the window
        this.windowAdapter = new WindowAdapter() {

            // WINDOW_CLOSED event handler
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                	messageManager.stopChat(otherUser);
                	disconnect();
                } catch (InterruptedException e1) {
                	System.out.println("Chat already stopped");
                }
            }
            
        };
        this.frame.addWindowListener(windowAdapter);
    }
    

    /*
     * Sends a disconnect message to the other user and close the window
     */
    public void disconnect() {
    	try {
			this.messageManager.sendMessage(otherUser, "#disconnect#");
		} catch (ChatNotFound e) {
			System.out.println("Chat already closed.");
		}
    	this.frame.dispose();
    }
    
    /*
     * Prints a particular message to show the current user that the other user closed the chatWindow
     * and that this window can not be used anymore
     */
    public void printDisconnectMessage() {
    	chatBox.append("The other user has closed the chat \n");
    }

    /*
     * Defines the actions to do when the send button has been pressed
     */
    class sendMessageButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
                // do nothing
            } else {
            	printSentMessage(new Timestamp(System.currentTimeMillis()), messageBox.getText());
                try {
					messageManager.sendMessage(otherUser, messageBox.getText());
				} catch (ChatNotFound e) {
					System.out.println("You have been disconnected from the chat...");
				}
                messageBox.setText("");
            }
            messageBox.requestFocusInWindow();
        }
    }
}