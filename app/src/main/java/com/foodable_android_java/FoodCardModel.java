package com.foodable_android_java;

public class FoodCardModel {
    String ItemImg;
    String ItemName;
    String ItemDistance;
    String Donate_Name;
    String Donate_Img;
    FoodCardModel(){
    }
    FoodCardModel(String ItemImg, String ItemName, String ItemDistance, String Donate_Name, String Donate_Img){
        this.ItemImg = ItemImg;
        this.ItemName = ItemName;
        this.ItemDistance = ItemDistance;
        this.Donate_Name = Donate_Name;
        this.Donate_Img = Donate_Img;
    }

    public String getItemImg() {
        return ItemImg;
    }

    public void setItemImg(String itemImg) {
        ItemImg = itemImg;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemDistance() {
        return ItemDistance;
    }

    public void setItemDistance(String itemDistance) {
        ItemDistance = itemDistance;
    }

    public String getDonate_Name() {
        return Donate_Name;
    }

    public void setDonate_Name(String donate_Name) {
        Donate_Name = donate_Name;
    }

    public String getDonate_Img() {
        return Donate_Img;
    }

    public void setDonate_Img(String donate_Img) {
        Donate_Img = donate_Img;
    }

}
