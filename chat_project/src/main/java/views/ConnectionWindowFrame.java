package views;

import controllers.UserManagement;
import exceptions.UserAuthentificationFailure;
import exceptions.UserCreationFailure;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectionWindowFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	// Graphic interface
    Container container = getContentPane();
    JLabel userLabel = new JLabel("ID :");
    JLabel passwordLabel = new JLabel("Mot de passe :");
    JLabel messageLabel = new JLabel("ID ou mot de passe incorrect");
    JTextField idField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JButton loginButton = new JButton("Se connecter");
    JButton createAccountButton = new JButton("Créer un compte");
    
    // Database interaction
    UserManagement userManagement = new UserManagement();
    
    // Nickname Frame
    NicknameWindowFrame nicknameFrame;
    
    public ConnectionWindowFrame() {
        setLayoutManager();
        setStyle();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }

    public void setStyle() {
        messageLabel.setForeground(Color.RED);
        messageLabel.setVisible(false);
    }

    public void setLocationAndSize() {
        userLabel.setBounds(50, 50, 100, 30);
        passwordLabel.setBounds(50, 100, 100, 30);
        idField.setBounds(150, 50, 150, 30);
        passwordField.setBounds(150, 100, 150, 30);
        messageLabel.setBounds(50, 150, 250, 30);
        loginButton.setBounds(50, 200, 250, 30);
        createAccountButton.setBounds(50, 250, 250, 30);
    }

    public void addComponentsToContainer() {
        container.add(userLabel);
        container.add(passwordLabel);
        container.add(idField);
        container.add(passwordField);
        container.add(messageLabel);
        container.add(loginButton);
        container.add(createAccountButton);
    }

    public void addActionEvent() {
        loginButton.addActionListener(this);
        createAccountButton.addActionListener(this);
    }

    private void showError(String content) {
        // Show error message and reset the form
        messageLabel.setText(content);
        messageLabel.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        // reset the error message
        messageLabel.setVisible(false);

        String id = idField.getText();
        String password = passwordField.getText();

        if(!id.equals("")) { // id can't be empty

            if(!password.equals("")) { // password can't be empty

                // Sign in button is clicked
                if (e.getSource() == loginButton) {

                    try { // verifie user info with the database

                        User user = userManagement.signInUser(Integer.parseInt(id), password);
                        nicknameFrame = new NicknameWindowFrame(user);
                        nicknameFrame.setNicknameFrameVisible();
                        this.dispose();

                    } catch (UserAuthentificationFailure ex) { // user doesn't exist in the database

                        showError("Id ou mot de passe incorrect.");
                        idField.setText("");
                        passwordField.setText("");

                    } catch(NumberFormatException ex) {

                        showError("Id incorrect (chiffres uniquement).");

                    }

                } else if(e.getSource() == createAccountButton) {

                    try {

                        userManagement.createNewUser(Integer.parseInt(id), password);

                    } catch (UserCreationFailure ex) {

                        showError("Id déjà utilisé.");
                        idField.setText("");

                    }  catch(NumberFormatException ex) {

                        showError("Id incorrect (chiffres uniquement).");

                    }
                }

            } else { // password empty

                showError("veuillez entrer un mot de passe");

            }
        } else { // login empty

            showError("veuillez entrer un id");

        }
    }
}