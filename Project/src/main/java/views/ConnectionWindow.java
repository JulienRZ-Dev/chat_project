package views;

import java.sql.SQLException;

import javax.swing.*;

import database.DatabaseConfig;
import database.DatabaseMessages;

public class ConnectionWindow {
	
    public static void main(String[] a) throws ClassNotFoundException, SQLException {
    	LoadingView loadingView = new LoadingView();
    	DatabaseConfig config = new DatabaseConfig(); 
    	config.configureDatabase();
    	loadingView.disposeLoadingView();
        ConnectionWindowFrame frame = new ConnectionWindowFrame();
        frame.setTitle("Se connecter à Chat App");
        frame.setVisible(true);
        frame.setBounds(10, 10, 370, 400);
        frame.setResizable(false);
    }
}
