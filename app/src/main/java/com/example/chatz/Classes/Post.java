package com.example.chatz.Classes;

import java.io.Serializable;

public class Post implements Serializable {

    private String userUID;
    private String user;
    private String title;
    private String body;
    private String imageUrl;
    private String groupName;
    private String groupImage;
    private long time;
    private String key;

    public Post(){}

    public Post(String userUID, String user, String title, String body, String imageUrl, String groupName, String groupImage, long time) {
        this.userUID = userUID;
        this.user = user;
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.groupName = groupName;
        this.groupImage = groupImage;
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
