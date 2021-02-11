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

    /*
     * Use this method to stop the current thread. It will also properly close the socket before exiting the application
     */
    public void stopListening() {
    	running = false;
    }
    
    /*
     * This thread will listen for every broadcast message
     * 
     * It will refresh the active user list whenever another user connected, disconnected, or changed his nickname
     */
    public void run() {
    	running = true;
        UdpCommunication communication = new UdpCommunication();
        try {
			if (!communication.openSocket(5000)) {
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
                System.out.println("NotificationListener - message reçu : " + message);
                String[] infos = message.split(":");
                //Location in "infos"      0           1      2       3                4               5
                //Messages format : login_request:<nickname>:<id>:<chat_port>:<file_port>:<Sender's IP Address>:<udp_port>
                if (infos[0].equals("login_request")) {
                	String nickname = infos[1];
                    int id = Integer.parseInt(infos[2]);
                    int chat_port = Integer.parseInt(infos[3]);
                    int file_port = Integer.parseInt(infos[4]);
                    InetAddress address = InetAddress.getByName(infos[5]);
                    int udp_port = Integer.parseInt(infos[6]);
                    if ((id == messageManager.getCurrentUser().getId()) || (nickname.equals(messageManager.getCurrentUser().getNickname()))) {
                    	response = "login_response:0:" + messageManager.getCurrentUser().getNickname() + ":" + Integer.toString(messageManager.getCurrentUser().getId()) + ":" + Integer.toString(messageManager.getCurrentUser().getChatPort()) + ":" + Integer.toString(messageManager.getCurrentUser().getFilePort());
                    }
                    else {
                        response = "login_response:1:" + messageManager.getCurrentUser().getNickname() + ":" + Integer.toString(messageManager.getCurrentUser().getId()) + ":" + Integer.toString(messageManager.getCurrentUser().getChatPort()) + ":" + Integer.toString(messageManager.getCurrentUser().getFilePort());
                        messageManager.addUser(new User(id, nickname, address, chat_port, file_port));
                    }
                    if (!communication.unicastMessage(response, address, udp_port)) {
                        System.out.println("ConnectionsListener : error while sending connection response");
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
                //Messages format : nickname_request:<oldNickname>:<newNickname>:<id>:<tcp_port>:<Sender's IP Address>:<udp_port>
                else if (infos[0].equals("nickname_request")) {
                	InetAddress address = InetAddress.getByName(infos[5]);
                    int udp_port = Integer.parseInt(infos[6]);
                	if (this.messageManager.getCurrentUser().getNickname().equals(infos[2])) {
                		response = "nickname_response:0:" + this.messageManager.getCurrentUser().getNickname() + ":" + this.messageManager.getCurrentUser().getChatPort(); 
                	}
                	else {
                		response = "nickname_response:1:" + this.messageManager.getCurrentUser().getNickname() + ":" + this.messageManager.getCurrentUser().getChatPort();
                	}
                	if (!communication.unicastMessage(response, address, udp_port)) {
                        System.out.println("ConnectionsListener : error while sending nickname response");
                    }
                }
                //Location in "infos"      0              1             2        3        4           5               6                 7
                //Messages format : nickname_change:<oldNickname>:<newNickname>:<id>:<chat_port>:<file_port>:<Sender's IP Address>:<udp_port>
                else if (infos[0].equals("nickname_change")) {
                	if ((this.messageManager.getCurrentUser().getChatPort() == Integer.parseInt(infos[4])) || infos[6].equals(this.messageManager.getCurrentUser().getIpAddress().toString()))
                		continue;
                	else {
	                	try {
							messageManager.changeOtherUserNickname(infos[1], infos[2]);
						} catch (UserNotFound e) {
							messageManager.addUser(new User(Integer.parseInt(infos[3]), infos[2], InetAddress.getByName(infos[6]), Integer.parseInt(infos[4]), Integer.parseInt(infos[5])));
						}
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
