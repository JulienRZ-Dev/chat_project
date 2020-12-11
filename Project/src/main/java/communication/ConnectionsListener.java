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
            System.out.println("ConnectionsListener : Erreur lors de l'ouverture du socket");
        String response = null;
        while(true) {
            try {
                String message = communication.receiveMessage();
                String[] infos = message.split(":");
                String nickname = infos[1];
                int id = Integer.parseInt(infos[2]);
                InetAddress address = InetAddress.getByName(infos[0]);
                if (nickname.compareTo(messageManager.getCurrentUser().getNickname()) == 0) {
                    response = "0:" + messageManager.getCurrentUser().getNickname() + Integer.toString(messageManager.getCurrentUser().getId());
                }
                else {
                    response = "1:" + messageManager.getCurrentUser().getNickname() + Integer.toString(messageManager.getCurrentUser().getId());
                    messageManager.addUser(new User(id, nickname, address));
                }
                if (!communication.unicastMessage(response, address, 1025)) {
                    System.out.println("ConnectionsListener : Erreur lors de l'envoi du message réponse");
                }
            }
            catch (IOException e) {
                System.out.println("ConnectionsListener : Erreur lors de la réception d'un message");
            }
        }
    }



}
