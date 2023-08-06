package com.example.chatsx.Model;

public class UserModel {
    String profileImg, userName, mail, userId, password, lastMessage, About;

    public UserModel(String profileImg, String userName, String mail, String userId, String password, String lastMessage) {
        this.profileImg = profileImg;
        this.userName = userName;
        this.mail = mail;
        this.userId = userId;
        this.password = password;
        this.lastMessage = lastMessage;
    }


    public UserModel() {
    }

    //Sign Up Constructor
    public UserModel(String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String About) {
        this.About = About;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
