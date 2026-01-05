package com.internshipfinder.zirah.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Student extends User {
    private String degree;
    private String university;
    private String skills;
    private String resumeFilePath;

    @ElementCollection
    private List<String> notifications = new ArrayList<>();

    // Add this method for notifications
    public List<String> getNotifications() {
        if (this.notifications == null) {
            this.notifications = new ArrayList<>();
        }
        return this.notifications;
    }

    // ADD THIS METHOD - it was missing
    public void addNotification(String notification) {
        if (this.notifications == null) {
            this.notifications = new ArrayList<>();
        }
        this.notifications.add(notification);
    }

    // ADD THIS SETTER METHOD for notifications
    public void setNotifications(List<String> notifications) {
        this.notifications = notifications;
    }
}