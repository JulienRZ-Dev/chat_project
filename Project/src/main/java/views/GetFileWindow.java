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

public class GetFileWindow {

	JFrame frame = new JFrame();
	JButton ignoreButton;
	JButton downloadButton;
	
	private FileTransfer fProvider;
	private MessageManagement messageManager;
	private String otherUser;

	private WindowAdapter windowAdapter;
	
	
    public GetFileWindow(String sender, FileTransfer fProvider, MessageManagement messageManager) {
    	System.out.println("GetFileWindow constructor reached");
    	this.otherUser = sender;
    	frame.setTitle("Fichier en provenance de " + this.otherUser);
    	this.messageManager = messageManager;
    	ignoreButton = new JButton("Ignorer");
    	downloadButton = new JButton("Télécharger le fichier");
    	this.fProvider = fProvider;
    	frame.setLayout(null);
    	setPosition();
    	setClosingBehaviour();
    	addActionEvent();
    	frame.setVisible(true);
    }
    
    private void setPosition() {
    	
    	frame.setBounds(10, 10, 500, 300);
    	ignoreButton.setBounds(100, 70, 300, 30);
    	downloadButton.setBounds(100, 200, 300, 30);
    	frame.add(ignoreButton);
    	frame.add(downloadButton);
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

    
    /*
     * Add the action listener 
     */
    private void addActionEvent() {
    	ignoreButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("frame should dispose");
				frame.dispose();	
			}
    		
    	});
    	
    	ignoreButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("Getting file");
					fProvider.receiveFile();
				} catch (FileNotFoundException e1) {
					System.out.println("Could not create the file");
				}
			}
    		
    	});
	}
}
