package com.example.chatz.Classes;

import java.io.Serializable;

public class Group implements Serializable {
    private String name;
    private int users;
    private String description;
    private String imageUrl;

    public Group(){}

    public Group(String name, int users, String description, String imageUrl) {
        this.name = name;
        this.users = users;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
