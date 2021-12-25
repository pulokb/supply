package com.samulitfirstproject.supply365.model;

public class ChatMsg {
    private String sender, receiver, sms;

    public ChatMsg(){

    }

    public ChatMsg(String sender, String receiver, String sms) {
        this.sender = sender;
        this.receiver = receiver;
        this.sms = sms;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }
}
