package com.samulitfirstproject.supply365.model;

public class NewsInfo {
    public String news,time,date,image,video;
    public NewsInfo(){

    }

    public NewsInfo(String news, String time, String date, String image, String video) {
        this.news = news;
        this.time = time;
        this.date = date;
        this.image = image;
        this.video = video;
    }

    public String getNews() {
        return news;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
