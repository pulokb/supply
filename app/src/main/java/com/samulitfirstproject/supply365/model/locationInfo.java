package com.samulitfirstproject.supply365.model;

public class locationInfo {
    private Double latitude, longitude;
    private String names;

    public locationInfo(){

    }

    public locationInfo(Double latitude, Double longitude, String names) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.names = names;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
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
}
