package com.example.zkc.travelsearch.entity;

public class ResultData {

    private String icon;
    private String name;
    private String vicinity;
    private String placeId;
    private boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }
    @Override
    public String toString(){
        return "ResultData{icon='" + icon + '\'' +
                ", name='" + name + "\'" + ", vicinity=" +
                vicinity + '\'' + '}';
    }
}
