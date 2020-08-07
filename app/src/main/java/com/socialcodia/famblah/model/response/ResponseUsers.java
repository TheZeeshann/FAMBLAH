package com.socialcodia.famblah.model.response;

import com.socialcodia.famblah.model.ModelUser;

import java.util.List;

public class ResponseUsers {
    private Boolean error;
    private String message;
    private List<ModelUser> users;

    public ResponseUsers(Boolean error, String message, List<ModelUser> users) {
        this.error = error;
        this.message = message;
        this.users = users;
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

    public List<ModelUser> getUsers() {
        return users;
    }

    public void setUsers(List<ModelUser> users) {
        this.users = users;
    }
}
