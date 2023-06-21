package com.codelink.flipchat.new_chat;

public class UsersModelClass {
    private String profileUrl, userName, userBio;

    public UsersModelClass(String profileUrl, String userName, String userBio) {
        this.profileUrl = profileUrl;
        this.userName = userName;
        this.userBio = userBio;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
