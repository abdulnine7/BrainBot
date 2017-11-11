package com.project.brainbot;

public class User {
    String nickname;
    String profileUrl;


    User() {
        nickname = "";
        profileUrl = "";
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}