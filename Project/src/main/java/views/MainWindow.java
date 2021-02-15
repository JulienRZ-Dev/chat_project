package views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.MessageManagement;
import database.DatabaseConfig;
import exceptions.ChatAlreadyExists;
import exceptions.UdpConnectionFailure;
import exceptions.UserNotFound;
import models.User;

public class MainWindow {
	
	private MessageManagement messageManager;
	
	private ArrayList<User> users = new ArrayList<User>();
	private DefaultListModel<String> userList = new DefaultListModel<>();
	private WindowAdapter windowAdapter;  

	// Change nickname
    JPanel container = new JPanel();
    JLabel pseudonymeLabel = new JLabel("Changer de pseudonyme");
    JLabel messageLabel = new JLabel();
    JTextField pseudonymeField = new JTextField();
    JButton validateButton = new JButton("Valider");
    
    // user list graphic
	JFrame frame= new JFrame();  
	JButton chatButton = new JButton("Clavarder"); 
	JButton fileButton = new JButton("Envoyer fichier"); 
	JList<String> list1 = new JList<>(userList);
    
    public MainWindow(MessageManagement messageManagement) {
    	this.messageManager = messageManagement;
		
		messageManagement.listenForConnections(this);
		messageManagement.startChatManager();
		messageManagement.startFileTransferManager();

		ArrayList<User> usersToAdd = messageManagement.getActiveUsers();

		for(User user : usersToAdd) {   		 
			addUser(user);
		}
		
        setStyle();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
		
		
		//Define what to do when we close the window
        this.windowAdapter = new WindowAdapter() {

            // WINDOW_CLOSED event handler
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                
                try {
                	//We stop the Notification Listener to close the UDP socket properly
                	//Furthermore, we don't need to refresh our active list anymore
                	messageManager.stopListener();
                } catch (InterruptedException e1) {
                	System.out.println("Chat already stopped");
                }
                
                try {
                	//We stop the chatManager thread, and every launched chat in the same time
                	messageManager.stopChatManager();
                } catch (InterruptedException e1) {
                	System.out.println("Chat already stopped");
                }
                
                try {
                	//We stop the chatManager thread, and every launched chat in the same time
                	messageManager.stopFileTransferManager();
                } catch (InterruptedException e1) {
                	System.out.println("FileTransfer Manager already stopped");
                }
                
                try {
                	//We send a disconnect message to let every active user know that they can remove us from their active list
                	messageManager.disconnect();
                } catch (UdpConnectionFailure e1) {
					System.out.println("Could not broadcast udp disconnect message");
				}
                
                try {
                	//We will also close the connection with the centralized database
                	DatabaseConfig.conn.close();
                }
                 catch (SQLException e1) {
					System.out.println("Could not close the database connection");
				}
                
                //We terminate the program with an exit code 0 to show that everything went well
                System.exit(0);
            }
        };
        frame.addWindowListener(windowAdapter);
	}  

    
    /*
     * Set the components style
     */
    private void setStyle() {
        messageLabel.setVisible(false);
    }
    
    
    /*
     * Set the locations and sizes
     */
    private void setLocationAndSize() {
        pseudonymeLabel.setBounds(250, 50, 200, 30);
        pseudonymeField.setBounds(250, 100, 200, 30);
        validateButton.setBounds(250, 150, 200, 30);
        messageLabel.setBounds(250, 200, 200, 30);
        
		list1.setBounds(50, 50, 100, 300);  
		chatButton.setBounds(160, 280, 120, 30); 
		fileButton.setBounds(160, 320, 120, 30);
    }
    
    
    /*
     * Add the components to the container and style container
     */
    private void addComponentsToContainer() {
		frame.add(list1);
		frame.add(chatButton);
		frame.add(fileButton);
		frame.add(pseudonymeLabel);
		frame.add(pseudonymeField);
		frame.add(validateButton);
		frame.add(messageLabel);

		frame.setBounds(10, 10, 600, 600);  
		frame.setLayout(null);  
		frame.setVisible(true); 
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    
    /*
     * Print a error message on the window to show what the user did bad
     */
    private void showError(String content) {
    	messageLabel.setForeground(Color.RED);
        messageLabel.setText(content);
        messageLabel.setVisible(true);
    }
    
    
    /*
     * Print a success message 
     */
    private void showSuccess(String content) {
    	messageLabel.setForeground(Color.BLUE);
        messageLabel.setText(content);
        messageLabel.setVisible(true);
    }
    
    
    /*
     * Add the action listener 
     */
    private void addActionEvent() {
		chatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(list1.getSelectedIndex() >= 0) {
					User user = users.get(list1.getSelectedIndex());
					try {
						messageManager.startChat(user);
					} catch (ChatAlreadyExists e1) {
						System.out.println("Chat already running with " + user.getNickname());
					}
				}
			}
		});
		
		fileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(list1.getSelectedIndex() >= 0) {
					User user = users.get(list1.getSelectedIndex());
					System.out.println("sending file to " + user.getNickname());
					messageManager.startTransfer(user);
				}
			}
		});
		
		validateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		        messageLabel.setVisible(false); // reset the error message
		        try {
					if(messageManager.tryToChangeMyNickname(pseudonymeField.getText())) {
						showSuccess("Le pseudonyme a bien été changé");
					} else {
						showError("Le pseudonyme est en cours d'utilisation.");
					}
				} catch (UdpConnectionFailure ex) {
					ex.printStackTrace();
				}
			}
		});
    }
    
    
	/*
	 * Use this method to add a user to the graphic user list (so that the user can select a user to chat with him)
	 * 
	 * @param user
	 * 		  The user that needs to be added to the graphic user list (who just connected for example)
	 */
	public void addUser(User user) {
		if(!isUserInList(user)) {
			//System.out.println("adding the user " + user.getNickname() + " to the graphic user list");
			userList.addElement(user.getNickname());
			users.add(user);
		}
		else {
			System.out.println("Tried to add the user " + user.getNickname() + " but he was already in list");
		}
	}

	
	/*
	 * Use this method to remove a certain user from the graphic user list
	 * 
	 * @param user
	 * 		  The user that needs to be removed from the graphic user list (if he disconnected for example)
	 */
	public void removeUser(User user) throws UserNotFound{
		if(isUserInList(user)) {
			for (int i = 0; i < users.size(); i++) {
				if (user.getNickname().equals(users.get(i).getNickname())) {
					userList.remove(i);
					users.remove(users.get(i));
				}
			}
		}
		else {
			throw (new UserNotFound());
		}
	}

	
	/*
	 * Use this method to check if a user is already printed in the graphic user list
	 * 
	 * @param userToCheck
	 * 		  The user you want to check
	 * 
	 * @return true if the user is already printed in the graphic user list, else false
	 */
	public boolean isUserInList(User userToCheck) {
		for(User user : users) {
			if(user.getNickname().equals(userToCheck.getNickname())) {
				return true;
			}
		}
		return false;
	}
	
	public void showChangeNicknamePopUp(String oldNickname, String newNickname) {
   	 JOptionPane.showMessageDialog(frame, oldNickname + " is now " + newNickname);  
   }
}
