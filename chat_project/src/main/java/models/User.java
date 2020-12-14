package models;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.net.InetAddress.getLocalHost;

public class User {

    // Attributes
    private int id;
    private InetAddress ipAddress;
    private String nickname;
    private int port;


    // Constructor
    public User(int id, InetAddress ipAddress, int port) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.port = port;
    }
    
    public User(int id, String nickname, InetAddress ipAddress, int port) {
        this.id = id;
        this.nickname = nickname;
        this.ipAddress = ipAddress;
        this.port = port;
    }


    // Getters and setters

    // Getters
    public int getId() { return this.id; }

    public InetAddress getIpAddress() { return this.ipAddress; }

    public String getNickname() { return this.nickname; }
    
    public int getPort() { return this.port; }

    // Setters
    public void setNickname(String nickname) { this.nickname = nickname; }
}
