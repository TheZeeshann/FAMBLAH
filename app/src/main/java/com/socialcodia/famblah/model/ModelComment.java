package com.socialcodia.famblah.model;

public class ModelComment {
    private Boolean liked;
    private Integer userId, commentLikesCount, commentId;
    private String userName, userUsername, commentComment, commentTimestamp, userImage ;

    public ModelComment(Boolean liked, Integer userId, Integer commentLikesCount, Integer commentId, String userName, String userUsername, String commentComment, String commentTimestamp, String userImage) {
        this.liked = liked;
        this.userId = userId;
        this.commentLikesCount = commentLikesCount;
        this.commentId = commentId;
        this.userName = userName;
        this.userUsername = userUsername;
        this.commentComment = commentComment;
        this.commentTimestamp = commentTimestamp;
        this.userImage = userImage;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCommentLikesCount() {
        return commentLikesCount;
    }

    public void setCommentLikesCount(Integer commentLikesCount) {
        this.commentLikesCount = commentLikesCount;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
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

    public String getCommentComment() {
        return commentComment;
    }

    public void setCommentComment(String commentComment) {
        this.commentComment = commentComment;
    }

    public String getCommentTimestamp() {
        return commentTimestamp;
    }

    public void setCommentTimestamp(String commentTimestamp) {
        this.commentTimestamp = commentTimestamp;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
