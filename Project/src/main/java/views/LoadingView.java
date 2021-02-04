package views;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class LoadingView {
	
	JFrame frame;
	JLabel title;
	JLabel subTitle;
	ImageIcon loading;
	
	public LoadingView() {
		frame = new JFrame();
		title = new JLabel("Bienvenue dans l'application de clavardage", JLabel.CENTER);
		loading = new ImageIcon(getClass().getResource("/ajax-loader.gif"));
		subTitle = new JLabel("Veuillez patienter", loading, JLabel.CENTER);
		
		setStyle();
        setLocationAndSize();
        addComponentsToContainer();
        
        frame.setVisible(true);
	}
	
	public void setStyle() {
		title.setFont(new Font("Monaco", Font.PLAIN, 18));
		subTitle.setFont(new Font("Monaco", Font.PLAIN, 14));
	}

    public void setLocationAndSize() {
    	title.setBounds(50, 100, 500, 50);
    	subTitle.setBounds(50, 250, 500, 30);
    	frame.setTitle("Chargement des données de l'application");
        frame.setBounds(10, 10, 600, 400);
        frame.setResizable(false);
    }

    public void addComponentsToContainer() {
    	frame.add(title);
    	frame.add(subTitle);
    }
    
    public void disposeLoadingView() {
    	frame.dispose();
    }
}
