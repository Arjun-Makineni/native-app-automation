package com.riverisland.app.automation.domain;

/**
 * Created by Prashant Ramcharan on 23/01/2018
 */
public class ProductDetails {
    private String productName;
    private String productPrice;
    private String productId;

    public ProductDetails(String name, String price, String id) {
        this.productName = name;
        this.productPrice = price;
        this.productId = id;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public String toString() {
        return "{" +
                "productName='" + productName + '\'' +
                ", productPrice='" + productPrice + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}
