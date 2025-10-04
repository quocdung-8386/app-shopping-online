package com.example.apponline.firebase;

import android.util.Log;
import com.example.apponline.models.Product;
import com.example.apponline.models.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {

    private static final String TAG = "CartManager";
    private static CartManager instance;
    private final List<OrderItem> cartItems;
    private final FirebaseFirestore db;

    private CartManager() {
        cartItems = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void saveCartToFirestore(String userId) {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Không thể lưu giỏ hàng: User ID rỗng.");
            return;
        }

        Map<String, Object> cartMap = new HashMap<>();
        List<Map<String, Object>> itemsToSave = new ArrayList<>();
        for (OrderItem item : cartItems) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", item.getProductId());
            itemMap.put("name", item.getName());
            itemMap.put("price", item.getPrice());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("selectedSize", item.getselectedSize());
            itemMap.put("imageUrl", item.getImageUrl());
            itemMap.put("itemStatus", item.getItemStatus());
            itemsToSave.add(itemMap);
        }

        cartMap.put("items", itemsToSave);
        cartMap.put("lastUpdated", System.currentTimeMillis());

        db.collection("carts").document(userId)
                .set(cartMap)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Giỏ hàng đã được lưu vào Firestore thành công."))
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi lưu giỏ hàng vào Firestore", e));
    }

    public void fetchCartFromFirestore(String userId, FirestoreFetchCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onComplete(false);
            return;
        }

        db.collection("carts").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    cartItems.clear();

                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> items = (List<Map<String, Object>>) documentSnapshot.get("items");

                        if (items != null) {
                            for (Map<String, Object> itemMap : items) {
                                try {
                                    String productId = (String) itemMap.get("productId");
                                    String name = (String) itemMap.get("name");
                                    double price = 0.0;
                                    Object priceObj = itemMap.get("price");
                                    if (priceObj instanceof Long) {
                                        price = ((Long) priceObj).doubleValue();
                                    } else if (priceObj instanceof Double) {
                                        price = (Double) priceObj;
                                    }

                                    int quantity = ((Long) itemMap.get("quantity")).intValue();
                                    String size = (String) itemMap.get("selectedSize");
                                    String imageUrl = (String) itemMap.get("imageUrl");
                                    String itemStatus = (String) itemMap.get("itemStatus");

                                    OrderItem item = new OrderItem(productId, name, price, quantity, size, imageUrl, itemStatus);
                                    cartItems.add(item);
                                } catch (Exception e) {
                                    Log.e(TAG, "Lỗi khi chuyển đổi OrderItem từ Firestore", e);
                                }
                            }
                        }
                    }
                    callback.onComplete(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải giỏ hàng từ Firestore", e);
                    callback.onComplete(false);
                });
    }

    public interface FirestoreFetchCallback {
        void onComplete(boolean success);
    }

    public void addItem(Product product, int quantity, String selectedSize) {
        if (selectedSize == null || selectedSize.trim().isEmpty()) {
            selectedSize = "M";
        }

        String productId = product.getId();
        final String finalSize = selectedSize;

        for (OrderItem item : cartItems) {
            if (item.getProductId().equals(productId) && item.getselectedSize().equals(finalSize)) { // <-- ĐÃ SỬA LỖI Ở ĐÂY (getSelectedSize())
                item.setQuantity(item.getQuantity() + quantity);

                return;
            }
        }
        OrderItem newItem = new OrderItem(
                productId,
                product.getName(),
                product.getPrice(),
                quantity,
                finalSize,
                product.getImageUrl(),
                "Thành công"
        );
        cartItems.add(newItem);
    }


    public void removeItem(OrderItem item) {
        cartItems.remove(item);
    }

    public void updateQuantity(OrderItem item, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(item);
        } else {
            item.setQuantity(newQuantity);
        }
    }

    public List<OrderItem> getCartItems() {
        return cartItems;
    }

    public double calculateTotal() {
        double total = 0;
        for (OrderItem item : cartItems) {
            total += item.getSubtotal();
        }
        return total;
    }

    public int getTotalItems() {
        int count = 0;
        for (OrderItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }

    public void clearCart() {
        cartItems.clear();
    }
}