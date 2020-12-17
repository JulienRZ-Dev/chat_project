package views;

import javax.swing.*;

public class ConnectionWindow {
    public static void main(String[] a) {
        ConnectionWindowFrame frame = new ConnectionWindowFrame();
        frame.setTitle("Se connecter à Chat App");
        frame.setVisible(true);
        frame.setBounds(10, 10, 370, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }
}
