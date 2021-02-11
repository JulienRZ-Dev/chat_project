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

public class GetFileWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	JButton ignoreButton;
	JButton downloadButton;
	
	private FileTransfer fProvider;
	private MessageManagement messageManager;
	private String otherUser;

	private WindowAdapter windowAdapter;
	
    public GetFileWindow(String sender, FileTransfer fProvider, MessageManagement messageManager) {
    	this.otherUser = sender;
    	this.setTitle("Fichier en provenance de " + this.otherUser);
    	this.messageManager = messageManager;
    	ignoreButton = new JButton("Ignore");
    	downloadButton = new JButton("Download file");
    	this.fProvider = fProvider;
    	setPosition();
    	setClosingBehaviour();
    	this.setVisible(true);
    }
    
    private void setPosition() {
    	this.setBounds(10, 10, 300, 300);
    	ignoreButton.setBounds(100, 60, 100, 60);
    	downloadButton.setBounds(100, 240, 100, 60);
    	this.add(ignoreButton);
    	this.add(downloadButton);
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
		if(e.getSource() == ignoreButton) {
			this.dispose();	
		} else {
			try {
				fProvider.receiveFile();
			} catch (FileNotFoundException e1) {
				//Peut-être afficher une erreur dans la view ?
				System.out.println("Could not create the file");
			}	
		}
	}
}
