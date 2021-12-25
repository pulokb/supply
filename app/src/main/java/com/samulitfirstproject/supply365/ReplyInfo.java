package com.samulitfirstproject.supply365;

import com.google.firebase.database.Exclude;

public class ReplyInfo {
    private String reply,uid,commentKey,date,replyKey,postKey;

    public ReplyInfo(){

    }

    public ReplyInfo(String reply, String uid, String commentKey, String date, String replyKey, String postKey) {
        this.reply = reply;
        this.uid = uid;
        this.commentKey = commentKey;
        this.date = date;
        this.replyKey = replyKey;
        this.postKey = postKey;
    }

    public String getReply() {
        return reply;
    }

    public String getUid() {
        return uid;
    }

    public String getCommentKey() {
        return commentKey;
    }

    public String getDate() {
        return date;
    }

    public String getReplyKey() {
        return replyKey;
    }

    public String getPostKey() {
        return postKey;
    }

    @Exclude
    public void setKey(String key) {
        replyKey = key;
    }
}
