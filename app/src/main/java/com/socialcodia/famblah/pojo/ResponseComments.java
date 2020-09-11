package com.socialcodia.famblah.pojo;

import com.socialcodia.famblah.model.ModelComment;

import org.w3c.dom.Comment;

import java.util.List;

public class ResponseComments {
    private Boolean error;
    private String message;
    private List<ModelComment> comments;

    public ResponseComments(Boolean error, String message, List<ModelComment> comments) {
        this.error = error;
        this.message = message;
        this.comments = comments;
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

    public List<ModelComment> getComments() {
        return comments;
    }

    public void setComments(List<ModelComment> comments) {
        this.comments = comments;
    }
}
