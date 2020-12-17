package main;

import java.sql.SQLException;

import database.DatabaseConfig;
import database.DatabaseUserInterface;

public class Main {
    public static void main(String[] args) {
    	
    	// Create the database and tables if doesn't exists
    	DatabaseConfig config = new DatabaseConfig();
    	config.configureDatabase();
    	
    	// get a db interface instance 
        DatabaseUserInterface db = new DatabaseUserInterface();
        
        // create a new user
        try {
			db.createUser(1234, "1234");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
    }
}
