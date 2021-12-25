package com.samulitfirstproject.supply365.model;

public class locationInfo2 {
    private Double latitude, longitude;
    private String names, email;

    public locationInfo2(){

    }

    public locationInfo2(Double latitude, Double longitude, String names, String email) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.names = names;
        this.email = email;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
