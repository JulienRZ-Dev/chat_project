package controllers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import exceptions.UserCreationFailure;
import exceptions.UserAuthentificationFailure;

import database.DatabaseUserInterface;
import models.User;

public class UserManagement {

    // create a database interface instance that allows us to use it's methods
    private DatabaseUserInterface databaseUserInterface = new DatabaseUserInterface();

    /*
     *   Add a new user to the database and notifies the success or failure.
     *
     *   @param id
     *          given user id.
     *
     *   @param password
     *          given user password.
     *
     *   @return boolean.
     *
     *   @throws UserCreationFailure, SQLException, ClassNotFoundException.
     */
    public boolean createNewUser(int id, String password) throws UserCreationFailure, SQLException, ClassNotFoundException {
    	return databaseUserInterface.createUser(id, password);
    }


    /*
     *   Authenticates a user.
     *
     *   @param id
     *          given id.
     *
     *   @param password
     *          given password.
     *
     *   @return user.
     *
     *   @throws UserAuthentificationFailure if username or password is incorrect.
     */
    public User signInUser(int id, String password) throws UserAuthentificationFailure {

        User user = null;

        try {
            if(databaseUserInterface.signIn(id, password)){
                user = new User(id);
            } else {
                throw new UserAuthentificationFailure();
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return user;
    }
}
