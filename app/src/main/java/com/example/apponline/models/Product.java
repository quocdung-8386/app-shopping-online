package com.example.apponline.models;

import java.util.List;

public class Product {
    private String id;
    private String name;
    private double price;
    private String imageUrl;


    private String description;
    private double discountPrice;
    private double rating;
    private List<String> sizes;

    public Product() {
    }
    public Product(String id, String name, double price, String imageUrl, String description, double discountPrice, double rating, List<String> sizes) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.discountPrice = discountPrice;
        this.rating = rating;
        this.sizes = sizes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }




    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }
    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getSizes() {
        return sizes;
    }
    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }
}