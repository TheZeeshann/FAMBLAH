package com.socialcodia.famblah.pojo;

import com.socialcodia.famblah.model.ModelComment;

public class ResponseComment {
    private Boolean error;
    private String message;
    private ModelComment comments;

    public ResponseComment(Boolean error, String message, ModelComment comments) {
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

    public ModelComment getComments() {
        return comments;
    }

    public void setComments(ModelComment comments) {
        this.comments = comments;
    }
}
