package com.socialcodia.famblah.pojo;

import com.socialcodia.famblah.model.ModelUser;

import java.util.List;

public class ResponseFriends {
    private boolean error;
    private String message;
    private List<ModelUser> friends;

    public ResponseFriends(boolean error, String message, List<ModelUser> friends) {
        this.error = error;
        this.message = message;
        this.friends = friends;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ModelUser> getFriends() {
        return friends;
    }

    public void setFriends(List<ModelUser> friends) {
        this.friends = friends;
    }
}
