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
                	messageManagement.stopChatManager();
                	messageManagement.disconnect();
                } catch (InterruptedException e1) {
                	System.out.println("Chat already stopped");
                } catch (UdpConnectionFailure e1) {
					System.out.println("Could not broadcast udp disconnect message");
				}
                System.exit(0);
            }
        };
        frame.addWindowListener(windowAdapter);
	}  


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

	public boolean isUserInList(User userToCheck) {
		for(User user : users) {
			if(user.getNickname().equals(userToCheck.getNickname())) {
				return true;
			}
		}
		return false;
	}
}