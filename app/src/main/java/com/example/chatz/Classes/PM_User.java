package com.example.chatz.Classes;

public class PM_User {
    private String userUID;
    private String private_messages_ID;

    public PM_User(){}

    public PM_User(String userUID, String private_messages_ID) {
        this.userUID = userUID;
        this.private_messages_ID = private_messages_ID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getPrivate_messages_ID() {
        return private_messages_ID;
    }

    public void setPrivate_messages_ID(String private_message_ID) {
        this.private_messages_ID = private_message_ID;
    }
}
