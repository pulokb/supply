package com.samulitfirstproject.supply365.model;

public class vendorName {
    String nameVendor,proName,productID,quantity,address,phone,price, Weight;
   boolean isSelected = false;

    public vendorName() {
    }

    public vendorName(String nameVendor, String proName, String productID, String quantity, String address, String phone, String price, String weight, boolean isSelected) {
        this.nameVendor = nameVendor;
        this.proName = proName;
        this.productID = productID;
        this.quantity = quantity;
        this.address = address;
        this.phone = phone;
        this.price = price;
        Weight = weight;
        this.isSelected = isSelected;
    }

    public String getNameVendor() {
        return nameVendor;
    }

    public void setNameVendor(String nameVendor) {
        this.nameVendor = nameVendor;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
