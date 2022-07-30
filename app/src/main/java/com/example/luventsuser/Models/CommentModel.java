package com.example.luventsuser.Models;

public class CommentModel {

    private String profileUri,userName,comment,commentId,userId;
    private long create_at;

    public CommentModel() {
    }

    public CommentModel(String profileUri, String userName, String comment, String commentId, String userId, long create_at) {
        this.profileUri = profileUri;
        this.userName = userName;
        this.comment = comment;
        this.commentId = commentId;
        this.userId = userId;
        this.create_at = create_at;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }
}
