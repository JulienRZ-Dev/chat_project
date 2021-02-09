package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import models.Message;

public class DatabaseMessages {

    private Connection connection;
	
	
	public DatabaseMessages() {
		this.connection = DatabaseConfig.conn;
	}

    
    
    /*
    *   Get the message history from the two given users
    *
    *   @throws ClassNotFoundException if driver haven't been loaded successfully.
    *   
    *   @throws SQLException if SQL error.
     */
    public ArrayList<Message> getHistory(int idLocal, int idDistant) throws SQLException, ClassNotFoundException {
    	
    	Message message;
    	ArrayList<Message> history = new ArrayList<Message>();
    	
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.

        ResultSet rs = statement.executeQuery("select * from messages where (recipient = " + Integer.toString(idLocal) + " and transmitter = " + Integer.toString(idDistant) + ")" +
        		"or (recipient = " + Integer.toString(idDistant) + " and transmitter = " + Integer.toString(idLocal) + ")");

        while(rs.next()) {
        	message = new Message(rs.getInt("recipient"), rs.getInt("transmitter"), rs.getString("content"), rs.getTimestamp("message_date")); 
        	System.out.println(message);
        	history.add(message);
        }
    	
        statement.close();
        
    	return history;
    }
    
    
    public void appendHistory(int recipient, int transmitter, String content) throws SQLException, ClassNotFoundException {
    	
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.
        
        Timestamp time = new Timestamp(System.currentTimeMillis());
        
        statement.executeUpdate(
        	"insert into messages (recipient, transmitter, content, message_date) values ('" + 
        	recipient + "', '" + 
        	transmitter + "', '" +
        	content + "', '" +
        	time +
        	"')"
        );
        statement.close();
    }
 }
