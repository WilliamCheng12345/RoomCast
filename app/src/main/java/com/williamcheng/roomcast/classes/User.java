package com.williamcheng.roomcast.classes;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String email;
    private String deviceToken;
    private String roommatesName;
    private List<UpcomingNotification> upcomingNotifications = new ArrayList<>();

    public User(String email, String deviceToken) {
        this.email = email;
        this.deviceToken = deviceToken;
        roommatesName = "EMPTY";
    }

    public User() { }

    public String getEmail() {
        return email;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getRoommatesName() {
        return roommatesName;
    }

    public List<UpcomingNotification> getUpcomingNotifications() {
        return upcomingNotifications;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setRoommatesName(String roommatesName) {
        this.roommatesName = roommatesName;
    }

    public void setUpcomingNotifications(List<UpcomingNotification> upcomingNotifications) {
        this.upcomingNotifications = upcomingNotifications;
    }
}
