package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import models.User;

public class SendFileWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	JButton selectButton;
	JButton sendButton;
	FileTransfert fProvider;
	File selected = null;
    
    public SendFileWindow(User user) {
    	selectButton = new JButton("Sélectionner un fichier");
    	sendButton = new JButton("Envoyer");
    	fProvider = new FileTransfert();
    	setPosition();
    }
    
    
    private void setPosition() {
    	this.setBounds(10, 10, 300, 300);
    	selectButton.setBounds(100, 60, 100, 60);
    	sendButton.setBounds(100, 240, 100, 60);
    }	


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == selectButton) {
			JFileChooser jFile = new JFileChooser();	
			selected = jFile.getSelectedFile();		
		} else if(selected != null) {
			fProvider.sendFile(selected);	
		}
	}
}