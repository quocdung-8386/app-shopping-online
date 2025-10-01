package com.example.apponline.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String userId;
    private long timestamp;
    private double totalAmount;
    private String status;
    private String shippingAddress;
    private String paymentMethod;
    private List<OrderItem> items;

    // Constructor rỗng bắt buộc cho Firebase/Firestore
    public Order() {
        // Khởi tạo để đảm bảo trường không null trước khi Firestore ánh xạ
        this.items = new ArrayList<>();
    }

    // Constructor cũ (giữ lại nếu có code legacy sử dụng)
    public Order(String orderId, String userId, double totalAmount, String shippingAddress, List<OrderItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
        this.totalAmount = totalAmount;
        this.status = "Thành công";
        this.shippingAddress = shippingAddress;
        this.items = items;
    }

    // Constructor MỚI (Sử dụng trong CheckoutActivity)
    public Order(String orderId, String userId, double totalAmount, String shippingAddress, List<OrderItem> items, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.timestamp = System.currentTimeMillis();
        this.totalAmount = totalAmount;
        this.status = "Thành công";
        this.shippingAddress = shippingAddress;
        this.items = items;
        this.paymentMethod = paymentMethod;
    }


    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    /**
     * 🚨 SỬA LỖI QUAN TRỌNG: Đảm bảo Getter không bao giờ trả về NULL
     */
    public List<OrderItem> getItems() {
        if (items == null) {
            // Khởi tạo nếu Firestore cố gắng gán NULL (như dữ liệu bạn đã thấy)
            items = new ArrayList<>();
        }
        return items;
    }
    public void setItems(List<OrderItem> items) { this.items = items; }
}