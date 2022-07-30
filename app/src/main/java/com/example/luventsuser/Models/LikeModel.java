package com.example.luventsuser.Models;

public class LikeModel {
    String userName,postId,Uid;
    long create_at;

    public LikeModel() {
    }

    public LikeModel(String userName, String postId, String uid, long create_at) {
        this.userName = userName;
        this.postId = postId;
        Uid = uid;
        this.create_at = create_at;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }
}
