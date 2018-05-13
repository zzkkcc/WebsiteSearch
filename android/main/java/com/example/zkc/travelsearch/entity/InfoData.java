package com.example.zkc.travelsearch.entity;
public class InfoData {
    String addr;
    String phoneNumber;
    int price;
    float rating;
    String website;
    String url;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = (float)rating;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString(){
        return "{\"addr\":\"" + String.valueOf(addr) +
                "\", \"phone_number\":\"" + String.valueOf(phoneNumber) +
                "\", \"price\":\"" + String.valueOf(price) +
                "\", \"rating\":\"" + String.valueOf(rating) +
                "\", \"website\":\""+ website +
                "\", \"url\":\""+ url +
                "\"}";
    }
}
