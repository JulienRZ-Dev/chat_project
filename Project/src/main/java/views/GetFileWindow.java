package views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import models.User;

public class GetFileWindow extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	JButton ignoreButton;
	JButton downloadButton;
	FileTransfert fProvider;
    File file = null;
	
    public GetFileWindow(String sender) {
    	selectButton = new JButton("Télécharger le fichier");
    	sendButton = new JButton("Ignorer");
    	fProvider = new FileTransfert();
    	setPosition();
    }
    
    
    private void setPosition() {
    	this.setBounds(10, 10, 300, 300);
    	ignoreButton.setBounds(100, 60, 100, 60);
    	downloadButton.setBounds(100, 240, 100, 60);
    }	


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == ignoreButton) {
			this.dispose();	
		} else {
			file = fProvider.getFile();	
		}
	}
}
