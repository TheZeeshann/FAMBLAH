package com.socialcodia.famblah.pojo;

import com.socialcodia.famblah.model.ModelNotification;

import java.util.List;

public class ResponseNotification {
    private Boolean error;
    private String message;
    private List<ModelNotification> notifications;

    public ResponseNotification(Boolean error, String message, List<ModelNotification> notifications) {
        this.error = error;
        this.message = message;
        this.notifications = notifications;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ModelNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<ModelNotification> notifications) {
        this.notifications = notifications;
    }
}
