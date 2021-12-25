package com.samulitfirstproject.supply365.model;

public class ProductInfo {

    public String productKey,productTitle,productPrice,productImage,productDiscount,deleteKey;

    public ProductInfo (){
        //Empty Constructor
    }

    public ProductInfo(String productKey, String productTitle, String productPrice, String productImage, String productDiscount, String deleteKey) {
        this.productKey = productKey;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productDiscount = productDiscount;
        this.deleteKey = deleteKey;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getDeleteKey() {
        return deleteKey;
    }

    public void setDeleteKey(String deleteKey) {
        this.deleteKey = deleteKey;
    }
}
