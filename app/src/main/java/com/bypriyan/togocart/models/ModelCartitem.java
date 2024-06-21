package com.bypriyan.togocart.models;

public class ModelCartitem {

    private String id, pId, name, price,  cost, quentity, pImg, pQuentity;

    public ModelCartitem() {
    }

    public ModelCartitem(String id, String pId, String name, String price, String cost, String quentity, String pImg, String pQuentity) {
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.price = price;
        this.cost = cost;
        this.quentity = quentity;
        this.pImg = pImg;
        this.pQuentity = pQuentity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getQuentity() {
        return quentity;
    }

    public void setQuentity(String quentity) {
        this.quentity = quentity;
    }

    public String getpImg() {
        return pImg;
    }

    public void setpImg(String pImg) {
        this.pImg = pImg;
    }

    public String getpQuentity() {
        return pQuentity;
    }

    public void setpQuentity(String pQuentity) {
        this.pQuentity = pQuentity;
    }
}
