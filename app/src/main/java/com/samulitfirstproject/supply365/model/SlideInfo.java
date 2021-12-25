package com.samulitfirstproject.supply365.model;

public class SlideInfo {

    private String description;
    private String imageUrl;
    private String imageKey;

    public SlideInfo (){
        //Empty Constructor
    }

    public SlideInfo(String description, String imageUrl, String imageKey) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageKey = imageKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageKey() {
        return imageKey;
    }

    public void setImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
