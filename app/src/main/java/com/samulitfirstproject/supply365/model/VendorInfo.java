package com.samulitfirstproject.supply365.model;

import java.io.Serializable;

public class VendorInfo implements Serializable {

    public String userName,userImage,vendorKey,vendorDkey,userUID,userPhone,userLocation,isTopVendor,isBrandShop,isApprove,vPanelAccess;
    public VendorInfo(){

    }

    public VendorInfo(String userName, String userImage, String vendorKey, String vendorDkey, String userUID, String userPhone, String userLocation, String isTopVendor, String isBrandShop, String isApprove, String vPanelAccess) {
        this.userName = userName;
        this.userImage = userImage;
        this.vendorKey = vendorKey;
        this.vendorDkey = vendorDkey;
        this.userUID = userUID;
        this.userPhone = userPhone;
        this.userLocation = userLocation;
        this.isTopVendor = isTopVendor;
        this.isBrandShop = isBrandShop;
        this.isApprove = isApprove;
        this.vPanelAccess = vPanelAccess;
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

    public String getVendorKey() {
        return vendorKey;
    }

    public void setVendorKey(String vendorKey) {
        this.vendorKey = vendorKey;
    }

    public String getVendorDkey() {
        return vendorDkey;
    }

    public void setVendorDkey(String vendorDkey) {
        this.vendorDkey = vendorDkey;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getIsTopVendor() {
        return isTopVendor;
    }

    public void setIsTopVendor(String isTopVendor) {
        this.isTopVendor = isTopVendor;
    }

    public String getIsBrandShop() {
        return isBrandShop;
    }

    public void setIsBrandShop(String isBrandShop) {
        this.isBrandShop = isBrandShop;
    }

    public String getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(String isApprove) {
        this.isApprove = isApprove;
    }

    public String getvPanelAccess() {
        return vPanelAccess;
    }

    public void setvPanelAccess(String vPanelAccess) {
        this.vPanelAccess = vPanelAccess;
    }
}
