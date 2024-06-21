package com.bypriyan.togocart.models;

public class ModelCategories {

    private String imageUrl, categTitle;

    public ModelCategories(String imageUrl, String categTitle) {
        this.imageUrl = imageUrl;
        this.categTitle = categTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategTitle() {
        return categTitle;
    }

    public void setCategTitle(String categTitle) {
        this.categTitle = categTitle;
    }
}
