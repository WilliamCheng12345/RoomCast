package com.williamcheng.roomcast;

public class User {
    private String email;
    private String token;
    private String roommatesName;

    public User(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public User() { }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRoommatesName() {
        return roommatesName;
    }

    public void setRoommatesName(String roommatesName) {
        this.roommatesName = roommatesName;
    }
}
