package com.foodable_android_java;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Donation {
    private String title;
    private String description;
    private String pickUpTime;
    private String quantity;
    private String listFor;
    private LatLng location;
    ArrayList<String> images;

    public Donation() {
    }

    public Donation(String title, String description, String pickUpTime, String quantity, String listFor, LatLng location) {
        this.title = title;
        this.description = description;
        this.pickUpTime = pickUpTime;
        this.quantity = quantity;
        this.listFor = listFor;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getListFor() {
        return listFor;
    }

    public void setListFor(String listFor) {
        this.listFor = listFor;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
