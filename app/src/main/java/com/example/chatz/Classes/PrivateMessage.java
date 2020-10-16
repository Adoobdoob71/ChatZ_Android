package com.example.chatz.Classes;

public class PrivateMessage {
    private String userUID;
    private String text;
    private String imageUrl;
    private boolean edited;

    public PrivateMessage(){}

    public PrivateMessage(String userUID, String text, String imageUrl, boolean edited) {
        this.userUID = userUID;
        this.text = text;
        this.imageUrl = imageUrl;
        this.edited = edited;
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

    public boolean getEdited(){
        return edited;
    }

    public void setEdited(boolean edited){
        this.edited = edited;
    }
}
