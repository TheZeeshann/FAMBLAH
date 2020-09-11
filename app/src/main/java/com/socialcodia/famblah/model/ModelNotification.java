package com.socialcodia.famblah.model;

public class ModelNotification {
    private int notificationId,userId,feedId,notificationType,isSeen,userVerified;
    private String timestamp, notificationText, userUsername, userName,userImage;

    public ModelNotification(int notificationId, int userId, int feedId, int notificationType, int isSeen, int userVerified, String timestamp, String notificationText, String userUsername, String userName, String userImage) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.feedId = feedId;
        this.notificationType = notificationType;
        this.isSeen = isSeen;
        this.userVerified = userVerified;
        this.timestamp = timestamp;
        this.notificationText = notificationText;
        this.userUsername = userUsername;
        this.userName = userName;
        this.userImage = userImage;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFeedId() {
        return feedId;
    }

    public void setFeedId(int feedId) {
        this.feedId = feedId;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public int getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(int isSeen) {
        this.isSeen = isSeen;
    }

    public int getUserVerified() {
        return userVerified;
    }

    public void setUserVerified(int userVerified) {
        this.userVerified = userVerified;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
