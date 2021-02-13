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
    private int chat_port;
    private int file_port;


    // Constructors
    public User(int id) throws UnknownHostException {
    	this.id = id;
    	this.ipAddress = InetAddress.getLocalHost();
    	Random random = new Random();
    	this.chat_port = random.nextInt(65535 + 1 - 6000) + 6000;
    	random = new Random();
    	this.file_port = random.nextInt(65535 + 1 - 6000) + 6000;
    }
    
    public User(int id, String nickname, InetAddress ipAddress, int chat_port, int file_port) {
        this.id = id;
        this.nickname = nickname;
        this.ipAddress = ipAddress;
        this.chat_port = chat_port;
        this.file_port = file_port;
    }


    // Getters and setters

    // Getters
    public int getId() { return this.id; }

    public InetAddress getIpAddress() { return this.ipAddress; }

    public String getNickname() { return this.nickname; }
    
    public int getChatPort() { return this.chat_port; }
    
    public int getFilePort() { return this.file_port; }

    // Setters
    public void setNickname(String nickname) { this.nickname = nickname; }
}
