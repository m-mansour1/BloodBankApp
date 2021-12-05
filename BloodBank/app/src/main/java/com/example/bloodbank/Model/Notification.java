package com.example.bloodbank.Model;

public class Notification {
    String receiverid, senderid, text, date;

    public Notification(){
    }

    public Notification(String receiverid, String senderid, String text, String date) {
        this.receiverid = receiverid;
        this.senderid = senderid;
        this.text = text;
        this.date = date;
    }

    public String getReceiverid() {
        return receiverid;
    }

    public void setReceiverid(String receiverid) {
        this.receiverid = receiverid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
