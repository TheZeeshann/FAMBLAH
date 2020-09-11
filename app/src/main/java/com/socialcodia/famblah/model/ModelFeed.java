package com.socialcodia.famblah.model;

public class ModelFeed {
    String  userName,userUsername,userImage,feedImage,feedContent,feedVideo,feedThumbnail,feedType,feedTimestamp;
    Integer userId,feedId,feedLikes,feedComments,userVerified;
    Boolean liked;

    public ModelFeed(String userName, String userUsername, String userImage, String feedImage, String feedContent, String feedVideo, String feedThumbnail, String feedType, String feedTimestamp, Integer userId, Integer feedId, Integer feedLikes, Integer feedComments, Integer userVerified, Boolean liked) {
        this.userName = userName;
        this.userUsername = userUsername;
        this.userImage = userImage;
        this.feedImage = feedImage;
        this.feedContent = feedContent;
        this.feedVideo = feedVideo;
        this.feedThumbnail = feedThumbnail;
        this.feedType = feedType;
        this.feedTimestamp = feedTimestamp;
        this.userId = userId;
        this.feedId = feedId;
        this.feedLikes = feedLikes;
        this.feedComments = feedComments;
        this.userVerified = userVerified;
        this.liked = liked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUsername() {
        return userUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getFeedImage() {
        return feedImage;
    }

    public void setFeedImage(String feedImage) {
        this.feedImage = feedImage;
    }

    public String getFeedContent() {
        return feedContent;
    }

    public void setFeedContent(String feedContent) {
        this.feedContent = feedContent;
    }

    public String getFeedVideo() {
        return feedVideo;
    }

    public void setFeedVideo(String feedVideo) {
        this.feedVideo = feedVideo;
    }

    public String getFeedThumbnail() {
        return feedThumbnail;
    }

    public void setFeedThumbnail(String feedThumbnail) {
        this.feedThumbnail = feedThumbnail;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedTimestamp() {
        return feedTimestamp;
    }

    public void setFeedTimestamp(String feedTimestamp) {
        this.feedTimestamp = feedTimestamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFeedId() {
        return feedId;
    }

    public void setFeedId(Integer feedId) {
        this.feedId = feedId;
    }

    public Integer getFeedLikes() {
        return feedLikes;
    }

    public void setFeedLikes(Integer feedLikes) {
        this.feedLikes = feedLikes;
    }

    public Integer getFeedComments() {
        return feedComments;
    }

    public void setFeedComments(Integer feedComments) {
        this.feedComments = feedComments;
    }

    public Integer getUserVerified() {
        return userVerified;
    }

    public void setUserVerified(Integer userVerified) {
        this.userVerified = userVerified;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }
}
