package com.example.chatz.Classes;

public class User {

    private String username;
    private String profilePictureUrl;
    private String description;
    private String userUID;
    private String email;

    public User(){}

    public User(String username, String email, String profilePictureUrl, String description, String userUID) {
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.description = description;
        this.userUID = userUID;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
