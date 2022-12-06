package com.foodable_android_java;

public class User {
    String firstName;
    String lastName;
    String bio;
    String profile;

    User(){}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public User(String firstName, String lastName, String bio, String profile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.profile = profile;
    }


}
