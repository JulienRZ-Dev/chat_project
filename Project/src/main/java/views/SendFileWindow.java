package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import communication.FileTransfer;
import controllers.MessageManagement;
import models.User;

public class SendFileWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	JButton selectButton;
	JButton sendButton;
	
	private FileTransfer fSender;
	private File selected = null;
	private MessageManagement messageManager;
	private String otherUser;

	private WindowAdapter windowAdapter;
    
    public SendFileWindow(String receiver, FileTransfer fSender, MessageManagement messageManager) {
    	this.otherUser = receiver;
    	this.setTitle("Sending file to " + this.otherUser);
    	this.messageManager = messageManager;
    	selectButton = new JButton("Sélectionner un fichier");
    	sendButton = new JButton("Envoyer");
    	this.fSender = fSender;
    	setPosition();
    	setClosingBehaviour();
    }
    
    
    private void setPosition() {
    	this.setBounds(10, 10, 300, 300);
    	selectButton.setBounds(100, 60, 100, 60);
    	sendButton.setBounds(100, 240, 100, 60);
    }	
    
    private void setClosingBehaviour() {
    	//Define what to do when we close the window
        this.windowAdapter = new WindowAdapter() {

            // WINDOW_CLOSED event handler
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                messageManager.stopTransfer(otherUser);
            }
        };
    }


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == selectButton) {
			JFileChooser jFile = new JFileChooser();	
			selected = jFile.getSelectedFile();		
		} else if(selected != null) {
			try {
				fSender.sendFile(selected);
			} catch (FileNotFoundException e1) {
				//Peut-être afficher une erreur dans la view ?
				System.out.println("Could not find the selected file");
			}	
		}
	}
}