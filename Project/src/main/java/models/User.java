package models;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.net.InetAddress.getLocalHost;

public class User {

    // Attributes
    private int id;
    private InetAddress ipAddress;
    private String nickname;


    // Constructor
    public User(int id, String nickname, InetAddress ipAddress) {
        this.id = id;
        this.nickname = nickname;
        this.ipAddress = ipAddress;
    }


    // Getters and setters

    // Getters
    public int getId() { return this.id; }

    public InetAddress getIpAddress() { return this.ipAddress; }

    public String getNickname() {
        return this.nickname;
    }

    // Setters
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
