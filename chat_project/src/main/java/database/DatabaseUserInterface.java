package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DatabaseUserInterface {

    private Connection connection;
    private final static String url = "jdbc:sqlite:chat_app.db";
    
    
    /*
    *   Check if a user with the given id exists in the database
    *
    *   @param id, user id
    *
    *   @return true if the user exists, false otherwise
     */
    private boolean doesUserExist(int id) throws SQLException {

        // check if ID already exists in the database
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.

        ResultSet rs = statement.executeQuery("select * from users where id = '" + Integer.toString(id) + "'");

        if(rs.next()) { // Result set is not empty
        	statement.close();
            return true; // Notify that user id already exist
        } else {
        	statement.close();
            return false; // Notify that the user id does not exist
        }
    }


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
    *   Create a new user with the given id ans password.
    *   If a user with this id already exist, does not create the user.
    *
    *   @ return true if user id is available and user have been created into the database,
    *            false if user id is already used.
    *
    *   @throws SQLException if SQL error.
     */
    public boolean createUser(int id, String password) throws SQLException, ClassNotFoundException {

        doConnect();

        // check if ID already exists in the database
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.

        if(!doesUserExist(id)){
            statement.executeUpdate("insert into users (id, pwd) values ('" + Integer.toString(id) + "', '" + password + "')");
            statement.close();
            return true;
        } else {
        	statement.close();
            return false;
        }
    }


    /*
    *   Authentifies user with id and password.
    *
    *   @param id, user id.
    *
    *   @param password, user password.
    *
    *   @return true if user with id and password exist, false otherwise.
    *
    *   @throws SQLException if SQL error.
     */
    public boolean signIn(int id, String password) throws SQLException, ClassNotFoundException {

        doConnect();

        // check if ID already exists in the database
        Statement statement = connection.createStatement(); // create the statement object
        statement.setQueryTimeout(10);  // set timeout to 10 sec.

        ResultSet rs = statement.executeQuery("select * from users where id = '" + Integer.toString(id) + "' and pwd = '" + password +"'");

        if(rs.next()) {
        	statement.close();
            return true; // user exists
        } else {
        	statement.close();
            return false; // user does not exists
        }
    }
}
