package com.samulitfirstproject.supply365.model;

public class OrderInfo {

    public String orderTime,orderDate,orderStatus;

    public OrderInfo(){

    }

    public OrderInfo(String orderTime, String orderDate, String orderStatus) {
        this.orderTime = orderTime;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
