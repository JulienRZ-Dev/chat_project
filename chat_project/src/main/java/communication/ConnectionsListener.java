package communication;

import controllers.MessageManagement;
import models.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionsListener extends Thread {

	private boolean running;
	
    private MessageManagement messageManager;

    public ConnectionsListener(MessageManagement messageManager) {
        this.messageManager = messageManager;
    }

    public void stopListening() {
    	running = false;
    }
    
    
    public void run() {
    	running = true;
        UdpCommunication communication = new UdpCommunication();
        try {
			if (!communication.openSocket(5000, InetAddress.getByName("0.0.0.0"))) {
			    System.out.println("ConnectionsListener : error while opening the socket");
			    running = false;
			}
			else {
				System.out.println("Listening for messages on port " + communication.getSocket().getLocalPort());
			}
		} catch (UnknownHostException e1) {
			System.out.println("Error with getByName");
		}
        String response = null;
        while(running) {
            try {
                //Block until a message is received
                String message = communication.receiveMessage(5000);
                if (message == null)
                	continue;
                String[] infos = message.split(":");
                //Location in "infos"      0           1      2       3                4               5
                //Messages format : login_request:<nickname>:<id>:<tcp_port>:<Sender's IP Address>:<udp_port>
                if (!infos[0].equals("login_request")) {
                    continue;
                }
                String nickname = infos[1];
                int id = Integer.parseInt(infos[2]);
                int tcp_port = Integer.parseInt(infos[3]);
                InetAddress address = InetAddress.getByName(infos[4]);
                int udp_port = Integer.parseInt(infos[5]);
                if (id == messageManager.getCurrentUser().getId()) {
                    continue;
                }
                else if (nickname.equals(messageManager.getCurrentUser().getNickname())) {
                	response = "login_response:0:" + messageManager.getCurrentUser().getNickname() + ":" + Integer.toString(messageManager.getCurrentUser().getId()) + ":" + Integer.toString(messageManager.getCurrentUser().getPort());
                }
                else {
                    response = "login_response:1:" + messageManager.getCurrentUser().getNickname() + ":" + Integer.toString(messageManager.getCurrentUser().getId()) + ":" + Integer.toString(messageManager.getCurrentUser().getPort());
                    messageManager.addUser(new User(id, nickname, address, tcp_port));
                }
                if (!communication.unicastMessage(response, address, udp_port)) {
                    System.out.println("ConnectionsListener : error while sending response");
                }
            }
            catch (IOException e) {
                System.out.println("ConnectionsListener : error while receiving a message");
                running = false;
            }
        }
        communication.closeSocket();
    }



}
