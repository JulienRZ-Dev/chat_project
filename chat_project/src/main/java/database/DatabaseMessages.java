package database;

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
    	
    	Message message;
    	ArrayList<Message> history = new ArrayList<Message>();
    	
    	doConnect();
    	
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
    	
    	doConnect();
    	
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
    
    public void clearMessages() throws ClassNotFoundException, SQLException {
    	doConnect();
    	
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.
        
        statement.executeUpdate(
        	"DROP TABLE MESSAGES"
        );
        
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	id INTEGER PRIMARY KEY NOT NULL,\n"
                + "	pwd TEXT NOT NULL,\n"
                + " capacity real \n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        statement.close();
    }
 }
