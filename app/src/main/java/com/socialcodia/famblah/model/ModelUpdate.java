package com.socialcodia.famblah.model;

public class ModelUpdate {
    private String updateTitle,updateDescription,updateUrl,updateTimestamp;
    private float updateVersion;

    public ModelUpdate(String updateTitle, String updateDescription, String updateUrl, String updateTimestamp, float updateVersion) {
        this.updateTitle = updateTitle;
        this.updateDescription = updateDescription;
        this.updateUrl = updateUrl;
        this.updateTimestamp = updateTimestamp;
        this.updateVersion = updateVersion;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getUpdateDescription() {
        return updateDescription;
    }

    public void setUpdateDescription(String updateDescription) {
        this.updateDescription = updateDescription;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(String updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public float getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(float updateVersion) {
        this.updateVersion = updateVersion;
    }
}
