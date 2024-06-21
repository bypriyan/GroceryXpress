package com.bypriyan.togocart.models;

public class ModelCoupons {

    private String couponId, couponCode, couponDescription, couponDiscount, couponMinimumOrder, couponTimeUsage;

    public ModelCoupons() {
    }

    public ModelCoupons(String couponId, String couponCode, String couponDescription, String couponDiscount, String couponMinimumOrder, String couponTimeUsage) {
        this.couponId = couponId;
        this.couponCode = couponCode;
        this.couponDescription = couponDescription;
        this.couponDiscount = couponDiscount;
        this.couponMinimumOrder = couponMinimumOrder;
        this.couponTimeUsage = couponTimeUsage;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponDescription() {
        return couponDescription;
    }

    public void setCouponDescription(String couponDescription) {
        this.couponDescription = couponDescription;
    }

    public String getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(String couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public String getCouponMinimumOrder() {
        return couponMinimumOrder;
    }

    public void setCouponMinimumOrder(String couponMinimumOrder) {
        this.couponMinimumOrder = couponMinimumOrder;
    }

    public String getCouponTimeUsage() {
        return couponTimeUsage;
    }

    public void setCouponTimeUsage(String couponTimeUsage) {
        this.couponTimeUsage = couponTimeUsage;
    }


}

