package views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import communication.FileTransfer;
import controllers.MessageManagement;
import models.User;

public class SendFileWindow {
	
	JFrame frame = new JFrame();
	JButton selectButton;
	JButton sendButton;
	JLabel message = new JLabel();
	
	private FileTransfer fSender;
	private File selected = null;
	final JFileChooser fc = new JFileChooser();
	private MessageManagement messageManager;
	private String otherUser;

	private WindowAdapter windowAdapter;
    
    public SendFileWindow(String receiver, FileTransfer fSender, MessageManagement messageManager) {
    	this.otherUser = receiver;
    	frame.setTitle("Sending file to " + this.otherUser);
    	this.messageManager = messageManager;
    	selectButton = new JButton("Sélectionner un fichier");
    	sendButton = new JButton("Envoyer");
    	this.fSender = fSender;
    	frame.setLayout(null);
    	message.setText("Sélectionnez un fichier");
    	setPosition();
    	setClosingBehaviour();
    	addActionEvent();
    	frame.setVisible(true);
    }
    
    
    private void setPosition() {
    	frame.setBounds(10, 10, 500, 300);
    	selectButton.setBounds(100, 70, 300, 30);
    	message.setBounds(100, 120, 300, 30);
    	sendButton.setBounds(100, 200, 300, 30);
    	frame.add(selectButton);
    	frame.add(message);
    	frame.add(sendButton);
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
    	selectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("should display file selection");
				int val = fc.showOpenDialog(frame);
				if(val == JFileChooser.APPROVE_OPTION) {
					selected = fc.getSelectedFile();
					System.out.println("File approuved");
					message.setText(selected.getName());
				} else {
					System.out.println("Selection cancelled");
					message.setBackground(Color.red);
					message.setText("Ce type de fichier n'est pas supporté");
				}
			}
    	});
    	
    	sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("Should send file");
					fSender.sendFile(selected);
					frame.dispose();
				} catch (FileNotFoundException e1) {
					System.out.println("file is null");
				}
			}
    	});
    }
}