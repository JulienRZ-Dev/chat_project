package views;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controllers.MessageManagement;
import exceptions.UserNotFound;
import models.User;
import utils.Utils;

public class MainWindowFrame extends JFrame implements ActionListener, ListSelectionListener {
	
	private static final long serialVersionUID = 1L;
	
	// User Logic
	private MessageManagement messageManagement;
	private User user;
	private User otherUser; // store the user that is selected to start a chat session
	private ArrayList<User> userList;
	private Utils utils = new Utils();
	
	// Graphicals elements
    private Container container = getContentPane();
    private JLabel welcomeLabel = new JLabel("Bienvenue dans Chat App !");
    private JLabel messageLabel = new JLabel();
    private JButton chatButton = new JButton("Démarrer une session de clavardage");
    private JScrollPane scrollableWrapper;
    
    // List
	private JList<String> graphicUserList;
	private DefaultListModel<String> listModel = new DefaultListModel<>();
	private Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
	private String itemSelected;
	
	
	public MainWindowFrame(User user) {
		
		// User logic
		this.messageManagement = new MessageManagement(user);
//		this.user = messageManagement.getCurrentUser();
//		this.userList = messageManagement.getActiveUsers();
	
		this.configureListDisplay();
		this.addElementsAndSetPostions();
		this.setStyle();
		this.addEventHandlers();
		this.configureWindow();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	
	private void configureListDisplay() {
		this.graphicUserList.setAlignmentX(LEFT_ALIGNMENT);
		for(User user : userList) {
			listModel.addElement(user.getNickname());
		}
		this.graphicUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.graphicUserList.setBorder(this.border);
		
		JScrollPane scrollableWrapper = new JScrollPane(this.graphicUserList);
		scrollableWrapper.setAlignmentX(LEFT_ALIGNMENT);
		scrollableWrapper.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollableWrapper.setSize(new Dimension(300, 600));
	}
	
	private void addElementsAndSetPostions() {
		container.add(welcomeLabel);
		container.add(messageLabel);
		container.add(scrollableWrapper);
		container.add(chatButton);
		
        welcomeLabel.setBounds(200, 100, 300, 90);
        messageLabel.setBounds(200, 1100, 300, 30);
        scrollableWrapper.setBounds(600, 0, 400, 1200);
        chatButton.setBounds(200, 1000, 300, 60);
	}
	
	private void setStyle() {
		messageLabel.setForeground(Color.RED);
		messageLabel.setVisible(false);
	}
	
	private void addEventHandlers() {
		graphicUserList.addListSelectionListener(this);
		chatButton.addActionListener(this);
	}
	
	private void configureWindow() {
		setSize(1000, 1200);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
    private void showError(String content) {
        // Show error message and reset the form
        messageLabel.setText(content);
        messageLabel.setVisible(true);
    }
	

    
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(e.getSource() == graphicUserList) {
			itemSelected = graphicUserList.getSelectedValue();
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		messageLabel.setVisible(false);
		
		if(e.getSource() == this.chatButton) {
			try {
				otherUser = utils.getUserFromNickname(itemSelected, userList);
				// TODO start chat window
			} catch (UserNotFound ex) {
				ex.printStackTrace();
			}	
		}
	}
}
