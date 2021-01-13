package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
	
	private final static String url = "jdbc:mysql://db4free.net:3306/clavardage";
	private final static String username = "rouzot";
	private final static String password = "12345678";
	public static Connection conn;
	
	public Connection configureDatabase() throws ClassNotFoundException {
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");  
        	conn = DriverManager.getConnection(url, username, password);    
        	
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");   
            }
            
        } catch (SQLException | ClassNotFoundException e) {    	
            System.out.println(e.getMessage()); 
        } 
        
		createUserTable();
		createMessageTable();
		
		return(conn);
	}

    
	/*
	 * Create the user table in the database
	 */
	public static void createUserTable() {

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	id INTEGER PRIMARY KEY NOT NULL,\n"
                + "	pwd TEXT NOT NULL,\n"
                + " capacity real \n"
                + ");";
        
        Statement stmt;
        
		try {
			stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
	/*
	 * Create the message table in the database
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
        
        Statement stmt;
        
        try {
        	stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
