package com.example.chatz.Classes;

public class Comment {
    private String text;
    private String userUID;
    private String email;

    public Comment(){}

    public Comment(String text, String userUID, String email) {
        this.text = text;
        this.userUID = userUID;
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
