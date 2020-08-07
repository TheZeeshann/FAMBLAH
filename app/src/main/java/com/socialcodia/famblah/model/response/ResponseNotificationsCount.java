package com.socialcodia.famblah.model.response;

public class ResponseNotificationsCount {
    private Boolean error;
    private String message;
    private int notificationsCount;

    public ResponseNotificationsCount(Boolean error, String message, int notificationsCount) {
        this.error = error;
        this.message = message;
        this.notificationsCount = notificationsCount;
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

    public int getNotificationsCount() {
        return notificationsCount;
    }

    public void setNotificationsCount(int notificationsCount) {
        this.notificationsCount = notificationsCount;
    }
}
