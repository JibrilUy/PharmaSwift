package com.example.pharmaswift.navigation_bar;

import android.graphics.Bitmap;

public class Product {
    private String name;
    private double price;
    private Bitmap image;  // Image path or URL

    public Product(String name, double price, Bitmap image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Bitmap getImage() {
        return image;
    }
}


