package com.example.luventsuser.Models;

import com.example.luventsuser.Classes.ImageHeightWidth;

public class PostModel {

    String UserName,caption,imageUri,userId,postId,profileUri,eventId;
    ImageHeightWidth imageHeightWidth;
    long likes,comments,create_at;

    public PostModel() {
    }

    public PostModel(String userName, String caption, String imageUri, String userId, String postId, String profileUri, String eventId, long likes, long comments, long create_at,ImageHeightWidth imageHeightWidth) {
        UserName = userName;
        this.caption = caption;
        this.imageUri = imageUri;
        this.userId = userId;
        this.postId = postId;
        this.profileUri = profileUri;
        this.eventId = eventId;
        this.likes = likes;
        this.comments = comments;
        this.create_at = create_at;
        this.imageHeightWidth=imageHeightWidth;
    }

//    public PostModel(String userName, String caption, String imageUri, String userId, String postId, String profileUri, long likes, long comments, long create_at) {
//        UserName = userName;
//        this.caption = caption;
//        this.imageUri = imageUri;
//        this.userId = userId;
//        this.postId = postId;
//        this.profileUri = profileUri;
//        this.likes = likes;
//        this.comments = comments;
//        this.create_at = create_at;
//    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getProfileUri() {
        return profileUri;
    }

    public void setProfileUri(String profileUri) {
        this.profileUri = profileUri;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at)
    {
        this.create_at = create_at;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public ImageHeightWidth getImageHeightWidth() {
        return imageHeightWidth;
    }

    public void setImageHeightWidth(ImageHeightWidth imageHeightWidth) {
        this.imageHeightWidth = imageHeightWidth;
    }
}
