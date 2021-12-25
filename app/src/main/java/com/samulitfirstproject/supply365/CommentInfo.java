package com.samulitfirstproject.supply365;

import com.google.firebase.database.Exclude;

public class CommentInfo {

    private String comment,uid,coomentKey,date,commentPostKey;

    public CommentInfo(){

    }

    public CommentInfo(String comment, String uid, String coomentKey, String date, String commentPostKey) {
        this.comment = comment;
        this.uid = uid;
        this.coomentKey = coomentKey;
        this.date = date;
        this.commentPostKey = commentPostKey;
    }

    public String getComment() {
        return comment;
    }

    public String getUid() {
        return uid;
    }

    public String getCoomentKey() {
        return coomentKey;
    }

    public String getDate() {
        return date;
    }

    public String getCommentPostKey() {
        return commentPostKey;
    }
    @Exclude
    public void setKey(String key) {
        coomentKey = key;
    }
}
