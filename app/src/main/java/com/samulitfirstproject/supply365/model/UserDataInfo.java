package com.samulitfirstproject.supply365.model;

public class UserDataInfo {
    String userImage, userType, userPhone, userName, receiveID;
    public UserDataInfo() {
    }

    public UserDataInfo(String userImage, String userType, String userPhone, String userName, String receiveID) {
        this.userImage = userImage;
        this.userType = userType;
        this.userPhone = userPhone;
        this.userName = userName;
        this.receiveID = receiveID;
    }

    @Override
    public String toString() {
        return "UserDataInfo{" +
                "userImage='" + userImage + '\'' +
                ", userType='" + userType + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userName='" + userName + '\'' +
                ", receiveID='" + receiveID + '\'' +
                '}';
    }

    public String getReceiveID() {
        return receiveID;
    }

    public void setReceiveID(String receiveID) {
        this.receiveID = receiveID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
