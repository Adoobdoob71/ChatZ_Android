package com.example.chatz.Classes;

public class Message {
    private String user;
    private String userUID;
    private String text;
    private String imageUrl;

    public Message(){}

    public Message(String user, String userUID, String text, String imageUrl) {
        this.user = user;
        this.userUID = userUID;
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
