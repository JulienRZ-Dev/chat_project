package controllers;

import exceptions.NicknameAlreadyUsed;
import models.Message;
import models.User;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

public class MessageManagement {

    private final int TIMEOUT_RECEPTION_REPONSE = 2000;

    /*
     *   Récupère une liste de messages entre l'utilisateur 1 et l'utilisateur 2
     *   depuis la base de données centralisée
     *
     *   @param user1
     *
     *   @param user2
     *
     *   @return L'historique des messsages de ces deux utilisateurs
     */
    public ArrayList<Message> getHistory(int user1, int user2) {
        ArrayList<Message> messageList = new ArrayList<Message>();

        // TODO

        return messageList;
    }


    /*
     *   Ajoute un message à la base de données
     *
     *   @param recipient
     *          Le destinataire du message
     *
     *   @param transmitter
     *          L'emetteur du message
     *
     */
    public void AddMessage(int recipient, int transmitter, String content) {

        // TODO

    }


    private static InetAddress getBroadcastAddress() throws SocketException {
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

    public ArrayList<User> isNicknameAvailable(String nickname, int id) throws NicknameAlreadyUsed {
        ArrayList<User> activeUsers = new ArrayList();
        int port = 1025 + (int)(Math.random() * ((65535 - 1025) + 1));
        String message = nickname + ":" + Integer.toString(id);

        try {
            DatagramSocket socket = new DatagramSocket(port);
            socket.setBroadcast(true);
            InetAddress broadcastAdress = getBroadcastAddress();
            DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), broadcastAdress, port);
            socket.send(packet);

            byte[] buf = new byte[1024];
            packet = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(TIMEOUT_RECEPTION_REPONSE);

            InetAddress myAddress = InetAddress.getLocalHost();
            socket.receive(packet);
            while (packet.getAddress().getHostAddress().contains(myAddress.toString())) {
                socket.receive(packet);
                //Traitement des packet
            }

            socket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return activeUsers;
    }

    private boolean notifyNicknameChanged(String oldNickname, String newNickname) {
        return false;
    }

    public boolean SignOutUser(User user) {
        return false;
    }

    public static void main(String[] args) throws SocketException {
        System.out.println(getBroadcastAddress().toString());
    }
}
