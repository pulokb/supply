package com.samulitfirstproject.supply365.model;

import java.io.Serializable;

public class BrandInfo implements Serializable {

    String brandKey,userName,userImage,userPhone,isBrandShop;

    public BrandInfo(){

    }

    public BrandInfo(String brandKey, String userName, String userImage, String userPhone, String isBrandShop) {
        this.brandKey = brandKey;
        this.userName = userName;
        this.userImage = userImage;
        this.userPhone = userPhone;
        this.isBrandShop = isBrandShop;
    }

    public String getBrandKey() {
        return brandKey;
    }

    public void setBrandKey(String brandKey) {
        this.brandKey = brandKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getIsBrandShop() {
        return isBrandShop;
    }

    public void setIsBrandShop(String isBrandShop) {
        this.isBrandShop = isBrandShop;
    }
}
