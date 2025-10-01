package com.example.apponline.models;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private String productId;
    private String name;
    private double price;
    private int quantity;
    private String selectedSize;
    private String imageUrl;
    private String itemStatus; // Đã thêm trường itemStatus

    public OrderItem() {
    }

    // Constructor đầy đủ (Đã thêm itemStatus)
    public OrderItem(String productId, String name, double price, int quantity, String selectedSize, String imageUrl, String itemStatus) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.selectedSize = selectedSize;
        this.imageUrl = imageUrl;
        this.itemStatus = itemStatus;
    }

    // --- Getters and Setters ---
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // SỬA LỖI: Đổi lại thành getSelectedSize() với 'S' hoa
    public String getselectedSize() { return selectedSize; }
    public void setSelectedSize(String selectedSize) { this.selectedSize = selectedSize; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Getter và Setter cho itemStatus
    public String getItemStatus() {
        return itemStatus != null ? itemStatus : "Pending"; // Trả về "Pending" nếu null
    }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }

    // Tính toán Subtotal
    public double getSubtotal() { return price * quantity; }
}