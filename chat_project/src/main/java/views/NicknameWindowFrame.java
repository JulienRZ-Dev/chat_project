package views;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import controllers.MessageManagement;
import exceptions.UdpConnectionFailure;
import models.User;

public class NicknameWindowFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	// Graphic interface
    Container container = getContentPane();
    JLabel nicknameLabel = new JLabel("Veuillez saisir un nickname");
    JLabel messageLabel = new JLabel();
    JTextField nicknameField = new JTextField();
    JButton validateButton = new JButton("Valider");
    
    // User info
    User user;
    
    // Message controller instance
    MessageManagement messageManagement; 
    
    
    public NicknameWindowFrame(User user) {
    	this.user = user;
    	this.messageManagement = new MessageManagement(user);
    	this.setVisible(false);
        setLayoutManager();
        setStyle();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
    }
    
    public void setNicknameFrameVisible() {
        this.setTitle("Choisir un nickname");
        this.setBounds(10, 10, 300, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
    }
    
    private void setLayoutManager() {
        container.setLayout(null);
    }

    private void setStyle() {
        messageLabel.setForeground(Color.RED);
        messageLabel.setVisible(false);
    }
    
    private void setLocationAndSize() {
        nicknameLabel.setBounds(50, 50, 200, 30);
        nicknameField.setBounds(50, 100, 200, 30);
        validateButton.setBounds(50, 150, 200, 30);
        messageLabel.setBounds(50, 200, 200, 30);        
    }
    
    private void addComponentsToContainer() {
        container.add(nicknameLabel);
        container.add(nicknameField);
        container.add(validateButton);
        container.add(messageLabel);
    }

    private void addActionEvent() {
        validateButton.addActionListener(this);
    }

    private void showError(String content) {
        // Show error message and reset the form
        messageLabel.setText(content);
        messageLabel.setVisible(true);
    }
    
    
	@Override
	public void actionPerformed(ActionEvent e) {
		
        // reset the error message
        messageLabel.setVisible(false);
        
//        try {
			if(true) { // messageManagement.isNicknameAvailable(nicknameField.getText())
				
				new MainWindowFrame(user);
				
			}
//			} else {
//				showError("Le nickname est déjà utilisé.");
//			}
//		} catch (UdpConnectionFailure ex) {
//			ex.printStackTrace();
//		}
	}
}
