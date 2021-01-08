package communication;

import controllers.MessageManagement;
import exceptions.UserNotFound;
import models.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NotificationsListener extends Thread {

	private boolean running;
	
    private MessageManagement messageManager;

    public NotificationsListener(MessageManagement messageManager) {
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
                System.out.println("NotificationListener - message re�u : " + message);
                String[] infos = message.split(":");
                //Location in "infos"      0           1      2       3                4               5
                //Messages format : login_request:<nickname>:<id>:<tcp_port>:<Sender's IP Address>:<udp_port>
                if (infos[0].equals("login_request")) {
                	String nickname = infos[1];
                    int id = Integer.parseInt(infos[2]);
                    int tcp_port = Integer.parseInt(infos[3]);
                    InetAddress address = InetAddress.getByName(infos[4]);
                    int udp_port = Integer.parseInt(infos[5]);
//                    if () {
//                        continue;
//                    }
                    if ((id == messageManager.getCurrentUser().getId()) || (nickname.equals(messageManager.getCurrentUser().getNickname()))) {
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
                //Location in "infos"      0               1       2           3                4
                //Messages format : disconnect_message:<nickname>:<id>:<Sender's IP Address>:<udp_port>
                else if (infos[0].equals("disconnect_message")) {
                	try {
						messageManager.removeUser(new User(Integer.parseInt(infos[2]), infos[1]));
					} catch (UserNotFound e) {
						System.out.println("The user was somehow already removed");
					}
                }
                //Location in "infos"      0              1             2        3        4               5               6
                //Messages format : nickname_change:<oldNickname>:<newNickname>:<id>:<tcp_port>:<Sender's IP Address>:<udp_port>
                else if (infos[0].equals("nickname_change")) {
                	try {
						messageManager.changeOtherUserNickname(infos[1], infos[2]);
					} catch (UserNotFound e) {
						messageManager.addUser(new User(Integer.parseInt(infos[3]), infos[2], InetAddress.getByName(infos[5]), Integer.parseInt(infos[4])));
					}
                }
                else {
                	continue;
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