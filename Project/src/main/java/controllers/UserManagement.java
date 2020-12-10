package controllers;

import java.sql.SQLException;
import exceptions.UserCreationFailure;
import exceptions.UserAuthentificationFailure;

import database.DatabaseUserInterface;
import models.User;

public class UserManagement {

    // create a database interface instance that allows us to use it's methods
    private DatabaseUserInterface databaseUserInterface = new DatabaseUserInterface();

    /*
     *   Add a new user to the database and save it into the local app instance.
     *
     *   @param id
     *          given user id.
     *
     *   @param password
     *          given user password.
     *
     *   @return user.
     *
     *   @throws UserCreationFailure if the id already exists.
     */
    public User createNewUser(int id, String password) throws UserCreationFailure {

        User user = null;

        try {
            if(databaseUserInterface.createUser(id, password)) {
                user = new User(id, null, password);
            } else {
                throw new UserCreationFailure(id);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return user;
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
                user = new User(id, null, password);
            } else {
                throw new UserAuthentificationFailure();
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return user;
    }
}
