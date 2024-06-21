package com.bypriyan.togocart.models;

public class ModelProducts {

    private String productId, productImg, productName, productMRP, productSellingPrise, productDiscount,
            InStock, productBrand, productCategory, productDescription, productQuantity, productType, unitOfMeasurement;

//        productQuantity, productType, unitOfMeasurement;

    public ModelProducts() {
    }

    public ModelProducts(String productId, String productImg, String productName, String productMRP, String productSellingPrise, String productDiscount, String inStock, String productBrand, String productCategory, String productDescription, String productQuantity, String productType, String unitOfMeasurement) {
        this.productId = productId;
        this.productImg = productImg;
        this.productName = productName;
        this.productMRP = productMRP;
        this.productSellingPrise = productSellingPrise;
        this.productDiscount = productDiscount;
        this.InStock = inStock;
        this.productBrand = productBrand;
        this.productCategory = productCategory;
        this.productDescription = productDescription;
        this.productQuantity = productQuantity;
        this.productType = productType;
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductMRP() {
        return productMRP;
    }

    public void setProductMRP(String productMRP) {
        this.productMRP = productMRP;
    }

    public String getProductSellingPrise() {
        return productSellingPrise;
    }

    public void setProductSellingPrise(String productSellingPrise) {
        this.productSellingPrise = productSellingPrise;
    }

    public String getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(String productDiscount) {
        this.productDiscount = productDiscount;
    }

    public String getInStock() {
        return InStock;
    }

    public void setInStock(String inStock) {
        InStock = inStock;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }
}
