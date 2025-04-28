package com.example.pharmaswift.navigation_bar;

public class Model {
    String productName, timestamp;
    Double productPrice;

    public Model(String productName, double productPrice, String timestamp) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.timestamp = timestamp;
    }
    public String getProductName() {
        return productName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getProductPrice() {
        return productPrice;
    }

}
