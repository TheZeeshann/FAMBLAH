package com.socialcodia.famblah.model;

public class ModelUser {
    private int id,feedsCount,friendsCount,friendshipStatus,status;
    String name,username,email,bio,image,token;

    public ModelUser() {
    }

    public ModelUser(int id, int feedsCount, int friendsCount, int friendshipStatus, int status, String name, String username, String email, String bio, String image, String token) {
        this.id = id;
        this.feedsCount = feedsCount;
        this.friendsCount = friendsCount;
        this.friendshipStatus = friendshipStatus;
        this.status = status;
        this.name = name;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.image = image;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedsCount() {
        return feedsCount;
    }

    public void setFeedsCount(int feedsCount) {
        this.feedsCount = feedsCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(int friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
