package views;

import javax.swing.*;

import controllers.MessageManagement;
import models.User;

import java.awt.event.*;
import java.util.ArrayList; 

public class GraphicUserList  {  
	
	private ArrayList<User> users = new ArrayList<User>();
	private DefaultListModel<String> l1 = new DefaultListModel<>();  
	
     public GraphicUserList(MessageManagement messageManagement) {  
    	 
    	 for(int i = 0; i < users.size(); i++) {   		 
    		 refreshUserList(users.get(i));
    	 }
    	 
    	 
    	 JFrame f= new JFrame();  
    	 JButton b=new JButton("Chat"); 
    	 JList<String> list1 = new JList<>(l1);
    	 
    	 f.setSize(400,400);  
    	 f.setLayout(null);  
    	 f.setVisible(true); 
    	 
          
         b.setBounds(200,150,80,30); 
         
         
         list1.setBounds(100,100, 75,75);  
         
    	 f.add(list1);  
    	 f.add(b);
    	 
         f.setSize(450,450);  
         f.setLayout(null);  
         f.setVisible(true); 
    	 
         
    	 messageManagement.listenForConnections(this);
    	 
         b.addActionListener(new ActionListener() {  
             public void actionPerformed(ActionEvent e) {
            	 if(list1.getSelectedIndex() > 0) {
                	 System.out.println("hey je suis la");
                	 new ChatWindow(messageManagement, users.get(list1.getSelectedIndex()));
            	 }
             }
         });
     }  
 
     
     public void refreshUserList(User user) {
    	 System.out.println(users);
    	 if(!userExists(user)) {
    		 System.out.println("add user to the list");
        	 users.add(user);
        	 System.out.println(users.get(0).getNickname() + ", " + users.get(1).getNickname());
        	 l1.add(users.size(), user.getNickname());
    	 }

     }
     
     public boolean userExists(User userToCheck) {
    	for(User user : users) {
    		if(user.getNickname().equals(userToCheck.getNickname())) {
    			return true;
    		}
    	}
    	return false;
     }
}