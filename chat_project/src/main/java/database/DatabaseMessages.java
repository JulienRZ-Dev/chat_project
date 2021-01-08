package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Message;

public class DatabaseMessages {

    private Connection connection;
    private final static String url = "jdbc:sqlite:chat_app.db";

    
    /*
    *   Create connection with the distant database,
    *   Save connection in class attributes.
    *
    *   @throws ClassNotFoundException if driver haven't been loaded successfully.
    *
    *   @throws SQLException if SQL error.
     */
    private void doConnect() throws ClassNotFoundException, SQLException {

        try {
            connection = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            System.out.println("Erreur lors de la connexion à  la base de données");
            e.printStackTrace();
        }
    }
    
    
    /*
    *   Get the message history from the two given users
    *
    *   @throws ClassNotFoundException if driver haven't been loaded successfully.
    *   
    *   @throws SQLException if SQL error.
     */
    public ArrayList<Message> getHistory(int idLocal, int idDistant) throws SQLException, ClassNotFoundException {
    	
    	doConnect();
    	
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.

        ResultSet rs = statement.executeQuery("select * from messages where (recipient = '" + Integer.toString(idLocal) + "' and transmitter = '" + Integer.toString(idDistant) + "')" +
        		"or where (recipient = '" + Integer.toString(idDistant) + "' and transmitter = '" + Integer.toString(idLocal) + "')");

    	
    	ArrayList<Message> history = new ArrayList<Message>();
        
    	return history;
    }
    
    
    public void appendHistory(Message message) throws SQLException, ClassNotFoundException {
    	
    	doConnect();
    	
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.
        
        statement.executeUpdate(
        	"insert into messages (recipient, transmitter, content, message_date) values ('" + 
        	message.getRecipient() + "', '" + 
        	message.getTransmitter() + "', '" +
        	message.getContent() + "', '" +
        	"NOW()" +
        	"')"
        );
        statement.close();
    }
}
