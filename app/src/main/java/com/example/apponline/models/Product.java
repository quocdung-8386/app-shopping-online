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
    private String category_id;
    private boolean dailyDeal;
    private boolean featured;
    private int totalSoldQuantity = 0;


    public Product() {
    }
    public Product(String id, String name, double price, String imageUrl,
                   String description, double discountPrice, double rating,
                   List<String> sizes,
                   String category_id,
                   boolean dailyDeal, boolean featured,
                   int totalSoldQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.discountPrice = discountPrice;
        this.rating = rating;
        this.sizes = sizes;
        this.category_id = category_id;
        this.dailyDeal = dailyDeal;
        this.featured = featured;
        this.totalSoldQuantity = totalSoldQuantity;
    }


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getDiscountPrice() { return discountPrice; }
    public void setDiscountPrice(double discountPrice) { this.discountPrice = discountPrice; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public boolean isDailyDeal() {
        return dailyDeal;
    }
    public void setDailyDeal(boolean dailyDeal) {
        this.dailyDeal = dailyDeal;
    }

    public boolean isFeatured() {
        return featured;
    }
    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    public int getTotalSoldQuantity() {
        return totalSoldQuantity;
    }

    public void setTotalSoldQuantity(int totalSoldQuantity) {
        this.totalSoldQuantity = totalSoldQuantity;
    }
}