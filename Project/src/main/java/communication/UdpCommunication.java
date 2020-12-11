package communication;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;


public class UdpCommunication {

    DatagramSocket socket;

    /*
     *   Call this method before sending or receiving any message to open a udp socket
     *
     *   @param port
     *          port number you want to open the socket at
     *
     *   @return false if the socket could not be open (if the port was already used for example)
     *           else return true
     */
    public boolean openSocket(int port) {
        try {
            socket = new DatagramSocket(port);
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     *   Sends a message to the selected ip address
     *
     *   @param message
     *          the message you want to broadcast
     *
     *   @param address
     *          the ip address you want to send the message to
     *
     *   @param port
     *          the port you want to broadcast on
     *
     *   @return false if the message could not be sent (by activating broadcast or while sending the message)
     *           else return true
     */
    public boolean unicastMessage(String message, InetAddress address, int port) {
        try {
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), address, port);
            socket.send(packet);

            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /*
     *   Finds the local network's broadcast address and sends a message to every machine on the network
     *
     *   @param message
     *          the message you want to broadcast
     *
     *   @param port
     *          the port you want to broadcast on
     *
     *   @return false if the message could not be sent (by activating broadcast or while sending the message)
     *           else return true
     */
    public boolean broadcastMessage(String message, int port) {
        try {
            socket.setBroadcast(true);
            InetAddress broadcastAddress = getBroadcastAddress();
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), broadcastAddress, port);
            socket.send(packet);

            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    /*
     *   Receive one single message
     *
     *   @return the message received
     */
    public String receiveMessage() throws IOException {
        String message = null;
        try {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            message = packet.getAddress().toString() + ":" + packet.getData().toString();
        }
        catch (SocketTimeoutException te) {
            System.out.println("Timeout expiré.");
        }
        return message;
    }


    /*
     *   Receive one single message, but stops if the timeout has been reached (and return a null message)
     *
     *   @param max_timeout
     *          the maximum time (in milliseconds) you want to wait after starting to wait for a message
     *
     *   @return the message received
     */
    public String receiveMessage(int max_timeout) throws IOException {
        String message = null;
        try {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(max_timeout);
            socket.receive(packet);
            message = packet.getAddress().toString() + ":" + packet.getData().toString();
        }
        catch (SocketTimeoutException te) {
            System.out.println("Timeout expiré.");
        }
        return message;
    }


    /*
     *   Receive an undefined number of messages, and stop receiving if the timeout has been reached
     *
     *   @param max_timeout
     *          the maximum time (in milliseconds) you want to wait after starting to wait for a message
     *
     *   @return the messages received
     */
    public ArrayList<String> receiveMessages(int max_timeout) throws IOException {
        ArrayList<String> messageList = new ArrayList<String>();
        try {
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(max_timeout);

            while (true) {
                socket.receive(packet);
                String message = packet.getData().toString();
                messageList.add(packet.getAddress().toString() + ":" + message) ;
            }
        }
        catch (SocketTimeoutException te) {
            System.out.println("Timeout atteint. On suppose que tous les autres utilisateurs ont répondu.");
        }
        return messageList;
    }


    /*
     *   Use this method to get the local network's broadcast address
     *
     *   @return the local network's broadcast address
     */
    private InetAddress getBroadcastAddress() throws SocketException {
        InetAddress broadcastAddress = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements())
        {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback())
                continue;    // Do not want to use the loopback interface.
            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
            {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null)
                    continue;

                broadcastAddress = broadcast;
            }
        }
        return broadcastAddress;
    }


    /*
     *   Use this method to close the socket once you're done using it
     */
    public void closeSocket() {
        socket.close();
    }

}
