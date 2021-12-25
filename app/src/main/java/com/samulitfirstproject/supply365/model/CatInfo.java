package com.samulitfirstproject.supply365.model;

public class CatInfo {

    public  String key,url,item,price,quantity,discount,offer;
    public boolean isSelected = false;

    public CatInfo(){

    }

    public CatInfo(String key, String url, String item, String price, String quantity, String discount, String offer, boolean isSelected) {
        this.key = key;
        this.url = url;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.offer = offer;
        this.isSelected = isSelected;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
