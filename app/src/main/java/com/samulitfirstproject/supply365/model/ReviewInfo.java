package com.samulitfirstproject.supply365.model;

public class ReviewInfo {
    String reviewer,rating,review;
    public ReviewInfo(){

    }

    public ReviewInfo(String reviewer, String rating, String review) {
        this.reviewer = reviewer;
        this.rating = rating;
        this.review = review;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
