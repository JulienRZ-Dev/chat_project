package models;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.net.InetAddress.getLocalHost;

public class User {

    // Attributes
    private int id;
    private InetAddress ipAddress;
    private String nickname;
    private String password;


    // Constructor
    public User(int id, String nickname, String password) {
        this.id = id;
        try {
            this.ipAddress = getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.nickname = nickname;
        this.password = password;
    }


    // Getters and setters

    // Getters
    public int getId() { return this.id; }

    public InetAddress getIpAddress() { return this.ipAddress; }

    public String getNickname() {
        return this.nickname;
    }

    public String getPassword() {
        return this.password;
    }


    // Setters
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
