package com.williamcheng.roomcast;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String token;
    private String roommatesName;
    private List<SavedNotification> savedNotifications;

    public User(String email, String token) {
        this.email = email;
        this.token = token;
        roommatesName = "EMPTY";
        savedNotifications = new ArrayList<>();
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

    public List<SavedNotification> getSavedNotifications() {
        return savedNotifications;
    }

    public void setSavedNotifications(List<SavedNotification> savedNotifications) {
        this.savedNotifications = savedNotifications;
    }
}
