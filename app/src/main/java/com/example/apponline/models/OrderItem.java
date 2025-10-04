package com.example.apponline.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String productId;
    private String name;
    private double price;
    private int quantity;
    private String selectedSize;
    private String imageUrl;
    private String itemStatus;

    public OrderItem() {
    }
    public OrderItem(String productId, String name, double price, int quantity, String selectedSize, String imageUrl, String itemStatus) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
        this.imageUrl = imageUrl;
        this.itemStatus = itemStatus;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getselectedSize() { return selectedSize; }
    public void setselectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getItemStatus() {
        return itemStatus != null ? itemStatus : "Pending";
    }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }

    public double getSubtotal() { return price * quantity; }
}