package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
	
	private final static String url = "jdbc:sqlite:chat_app.db";
	
	
	public void configureDatabase() {
		createDatabase();
		createUserTable();
		createMessageTable();
	}
	
	
    /*
    *   Create a new database ( first connection to the app )
    *
    *   @param filename, name of the database
     */
    public void createDatabase() {
  	
        try (Connection conn = DriverManager.getConnection(url)) {      
        	
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");   
            }
            
        } catch (SQLException e) {    	
            System.out.println(e.getMessage()); 
        }
    }
    
    
    /*
    * Create the user table in the local database
    */
    public static void createUserTable() {

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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    
    /*
    * Create the message table in the local database
    */
    public static void createMessageTable() {

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	recipient INTEGER NOT NULL,\n"
                + "	transmitter INTEGER NOT NULL,\n"
                + "	content TEXT NOT NULL,\n"
                + "	message_date DATETIME NOT NULL,\n"
                + " capacity real \n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
