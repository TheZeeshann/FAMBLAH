package com.socialcodia.famblah.pojo;

import com.socialcodia.famblah.model.ModelUpdate;

public class ResponseUpdate {
    private Boolean error;
    private String message;
    private ModelUpdate updates;

    public ResponseUpdate(Boolean error, String message, ModelUpdate updates) {
        this.error = error;
        this.message = message;
        this.updates = updates;
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

    public ModelUpdate getUpdates() {
        return updates;
    }

    public void setUpdates(ModelUpdate updates) {
        this.updates = updates;
    }
}
