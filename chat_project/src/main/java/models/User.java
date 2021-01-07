package models;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import static java.net.InetAddress.getLocalHost;

public class User {

    // Attributes
    private int id;
    private InetAddress ipAddress;
    private String nickname;
    private int port;


    // Constructors
    public User(int id) throws UnknownHostException {
    	this.id = id;
    	this.ipAddress = InetAddress.getLocalHost();
    	Random random = new Random();
    	this.port = random.nextInt(65535 + 1 - 6000) + 6000;
    }
    
    public User(int id, String nickname) {
    	this.id = id;
    	this.nickname = nickname;
    }
    
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
