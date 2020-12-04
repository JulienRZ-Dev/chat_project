package Models;

public class User {

    // Attributes
    private String nickname;
    private String password;
    private boolean active;


    // Constructor
    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
        this.active = false;
    }


    // Getters and setters

    // Getters
    public String getNickname() {
        return this.nickname;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean isActive() {
        return this.active;
    }


    // Setters
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
