package views;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import controllers.MessageManagement;
import exceptions.UdpConnectionFailure;
import models.User;

public class NicknameFrame extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	// Graphic interface
    Container container = getContentPane();
    JLabel pseudonymeLabel = new JLabel("Veuillez saisir un pseudonyme");
    JLabel messageLabel = new JLabel();
    JTextField pseudonymeField = new JTextField();
    JButton validateButton = new JButton("Valider");
    
    // User info
    User user;
    
    // Message controller instance
    MessageManagement messageManagement; 
    
    
    public NicknameFrame(User user) {
    	this.setBounds(10, 10, 300, 300);
    	this.user = user;
    	this.messageManagement = new MessageManagement(user);
    	this.setVisible(true);
        setLayoutManager();
        setStyle();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
    }
    
    
    private void setLayoutManager() {
        container.setLayout(null);
    }

    private void setStyle() {
        messageLabel.setForeground(Color.RED);
        messageLabel.setVisible(false);
    }
    
    private void setLocationAndSize() {
        pseudonymeLabel.setBounds(50, 50, 200, 30);
        pseudonymeField.setBounds(50, 100, 200, 30);
        validateButton.setBounds(50, 150, 200, 30);
        messageLabel.setBounds(50, 200, 200, 30);        
    }
    
    private void addComponentsToContainer() {
        container.add(pseudonymeLabel);
        container.add(pseudonymeField);
        container.add(validateButton);
        container.add(messageLabel);
    }

    private void addActionEvent() {
        validateButton.addActionListener(this);
    }
    
    /*
     * Print a error message on the window to show what the user did bad
     */
    private void showError(String content) {
        // Show error message and reset the form
        messageLabel.setText(content);
        messageLabel.setVisible(true);
    }
    
    
	@Override
	public void actionPerformed(ActionEvent e) {
		
        // reset the error message
        messageLabel.setVisible(false);
        
        try {
        	
			if(messageManagement.isNicknameAvailable(pseudonymeField.getText())) {
			
				new MainWindow(messageManagement);
				this.dispose(); // We also close the nickname frame
				
			} else {
				showError("Le pseudonyme ou l'id est en cours d'utilisation.");
			}
		} catch (UdpConnectionFailure ex) {
			ex.printStackTrace();
		}
	}
}
