package communication;

import controllers.MessageManagement;
import models.User;

import java.io.IOException;
import java.net.InetAddress;

public class ConnectionsListener extends Thread {

    private MessageManagement messageManager;

    public ConnectionsListener(MessageManagement messageManager) {
        this.messageManager = messageManager;
    }

    public void run() {
        UdpCommunication communication = new UdpCommunication();
        if (!communication.openSocket(1024))
            System.out.println("ConnectionsListener : error while opening the socket");
        String response = null;
        while(true) {
            try {
                //Block until a message is received
                String message = communication.receiveMessage();
                String[] infos = message.split(":");
                //Location in "infos"      0           1      2             3                  4
                //Messages format : login_request:<nickname>:<id>:<Sender's IP Address>:<Sender's port>
                if (infos[0].compareTo("login_request") != 0) {
                    continue;
                }
                String nickname = infos[1];
                int id = Integer.parseInt(infos[2]);
                InetAddress address = InetAddress.getByName(infos[3]);
                int port = Integer.parseInt(infos[4]);
                if (nickname.compareTo(messageManager.getCurrentUser().getNickname()) == 0) {
                    response = "login_response:0:" + messageManager.getCurrentUser().getNickname() + Integer.toString(messageManager.getCurrentUser().getId());
                }
                else {
                    response = "login_response:1:" + messageManager.getCurrentUser().getNickname() + Integer.toString(messageManager.getCurrentUser().getId());
                    messageManager.addUser(new User(id, nickname, address, port));
                }
                if (!communication.unicastMessage(response, address, port)) {
                    System.out.println("ConnectionsListener : error while sending response");
                }
            }
            catch (IOException e) {
                System.out.println("ConnectionsListener : error while receiving a message");
            }
        }
    }



}
