package views;

import javax.swing.*;

import communication.ChatManager;
import controllers.MessageManagement;
import exceptions.ChatAlreadyExists;
import exceptions.UdpConnectionFailure;
import exceptions.UserNotFound;
import models.User;

import java.awt.event.*;
import java.util.ArrayList; 

public class GraphicUserList  {  

	private ArrayList<User> users = new ArrayList<User>();
	private DefaultListModel<String> l1 = new DefaultListModel<>();
	private WindowAdapter windowAdapter;  

	public GraphicUserList(MessageManagement messageManagement) {
		
		messageManagement.listenForConnections(this);
		messageManagement.startChatManager();

		ArrayList<User> usersToAdd = messageManagement.getActiveUsers();

		for(User user : usersToAdd) {   		 
			addUser(user);
		}

		JFrame frame= new JFrame();  
		JButton b=new JButton("Chat"); 
		JList<String> list1 = new JList<>(l1);
		
		list1.setBounds(50, 50, 100, 300);  
		b.setBounds(160, 320, 80, 30); 
		
		frame.add(list1);  
		frame.add(b);

		frame.setBounds(10, 10, 300, 500);  
		frame.setLayout(null);  
		frame.setVisible(true); 
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		b.addActionListener(new ActionListener() {  
			public void actionPerformed(ActionEvent e) {
				if(list1.getSelectedIndex() >= 0) {
					User user = users.get(list1.getSelectedIndex());
					System.out.println("let's chat with " + user);
					try {
						messageManagement.startChat(user);
					} catch (ChatAlreadyExists e1) {
						System.out.println("Chat already running with " + user.getNickname());
					}
				}
			}
		});
		
		//Define what to do when we close the window
        this.windowAdapter = new WindowAdapter() {

            // WINDOW_CLOSED event handler
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                	//We stop the Notification Listener to close the UDP socket properly
                	//Furthermore, we don't need to refresh our active list anymore
                	messageManagement.stopListener();
                	//We stop the chatManager thread, and every launched chat in the same time
                	messageManagement.stopChatManager();
                	//We send a disconnect message to let every active user know that they can remove us from their active list
                	messageManagement.disconnect();
                } catch (InterruptedException e1) {
                	System.out.println("Chat already stopped");
                } catch (UdpConnectionFailure e1) {
					System.out.println("Could not broadcast udp disconnect message");
				}
                //We terminate the program with an exit code 0 to show that everything went well
                System.exit(0);
            }
        };
        frame.addWindowListener(windowAdapter);
	}  

	/*
	 * Use this method to add a user to the graphic user list (so that the user can select a user to chat with him)
	 * 
	 * @param user
	 * 		  The user that needs to be added to the graphic user list (who just connected for example)
	 */
	public void addUser(User user) {
		if(!isUserInList(user)) {
			System.out.println("adding the user " + user.getNickname() + " to the graphic user list");
			l1.addElement(user.getNickname());
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
					l1.remove(i);
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
}