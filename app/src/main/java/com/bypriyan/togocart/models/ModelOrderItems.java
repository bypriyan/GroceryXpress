package com.bypriyan.togocart.models;

public class ModelOrderItems {

    private String cost, name, pId, pImg, pQuantity, price, quantity;

    public ModelOrderItems() {
    }

    public ModelOrderItems(String cost, String name, String pId, String pImg, String pQuantity, String price, String quantity) {
        this.cost = cost;
        this.name = name;
        this.pId = pId;
        this.pImg = pImg;
        this.pQuantity = pQuantity;
        this.price = price;
        this.quantity = quantity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpImg() {
        return pImg;
    }

    public void setpImg(String pImg) {
        this.pImg = pImg;
    }

    public String getpQuantity() {
        return pQuantity;
    }

    public void setpQuantity(String pQuantity) {
        this.pQuantity = pQuantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
