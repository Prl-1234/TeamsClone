package com.example.teamsclone;

public class MainChatModel {
    private String talk;
    private String user;

    public MainChatModel(String talk, String user) {
        this.talk = talk;
        this.user = user;
    }
    public MainChatModel() {

    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
