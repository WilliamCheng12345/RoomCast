package com.williamcheng.roomcast.classes;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String token;
    private String roommatesName;
    private List<UpcomingNotification> upcomingNotifications = new ArrayList<>();

    public User(String email, String token) {
        this.email = email;
        this.token = token;
        roommatesName = "EMPTY";
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

    public List<UpcomingNotification> getUpcomingNotifications() {
        return upcomingNotifications;
    }

    public void setUpcomingNotifications(List<UpcomingNotification> upcomingNotifications) {
        this.upcomingNotifications = upcomingNotifications;
    }
}
