package com.example.apponline.firebase;

import android.util.Log;
import com.example.apponline.models.Order;
import com.example.apponline.models.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.FieldValue; // Import cần thiết cho FieldValue.increment
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManager {

    private static final String TAG = "OrderManager";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_PRODUCTS = "products";

    private static OrderManager instance;
    private FirebaseFirestore db;

    // Interface cũ
    public interface OrderLoadCallback {
        void onOrdersLoaded(List<Order> orders);
        void onFailure(String errorMessage);
    }
    public interface OrderPlaceCallback {
        void onSuccess(String orderId);
        void onFailure(String errorMessage);
    }

    private OrderManager() {
        db = FirebaseHelper.getFirestoreInstance();
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }
    public void fetchUserOrders(OrderLoadCallback callback) {
        String userId = FirebaseHelper.getCurrentUserId();

        if (userId == null) {
            String errorMsg = "User not logged in, cannot fetch orders.";
            Log.w(TAG, errorMsg);
            callback.onFailure(errorMsg);
            return;
        }

        db.collection(COLLECTION_ORDERS)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Order> loadedOrders = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        try {
                            Order order = document.toObject(Order.class);

                            if (order != null) {
                                order.setOrderId(document.getId());
                                if (order.getItems() == null) {
                                    order.setItems(new ArrayList<>());
                                }
                                loadedOrders.add(order);
                                Log.d(TAG, "Order loaded: " + order.getOrderId() + ", Items: " + order.getItems().size());
                            } else {
                                Log.e(TAG, "MAPPING FAILED for Order Document ID: " + document.getId() + ". Check Order and OrderItem models.");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error processing order document " + document.getId() + ": " + e.getMessage());
                        }
                    }

                    callback.onOrdersLoaded(loadedOrders);
                })
                .addOnFailureListener(e -> {
                    String errorMsg = "Error fetching orders: " + e.getMessage();
                    Log.e(TAG, errorMsg);
                    callback.onFailure("Lỗi tải đơn hàng. Vui lòng kiểm tra kết nối mạng hoặc quy tắc bảo mật.");
                });
    }
    public void updateProductSales(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            Log.w(TAG, "Không có sản phẩm nào để cập nhật doanh số.");
            return;
        }

        for (OrderItem item : items) {
            if (item.getProductId() != null && item.getQuantity() > 0) {
                db.collection(COLLECTION_PRODUCTS).document(item.getProductId())
                        .update("totalSoldQuantity", FieldValue.increment(item.getQuantity()))
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Updated sales quantity for product: " + item.getProductId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to update sales quantity for product " + item.getProductId() + ": " + e.getMessage());
                        });
            }
        }
    }
    public void placeOrder(Order newOrder, OrderPlaceCallback callback) {

        if (newOrder.getTotalAmount() <= 0) {
            Log.e(TAG, "Total amount is zero or negative. Order placement aborted.");
            callback.onFailure("Giá trị đơn hàng không hợp lệ.");
            return;
        }


        newOrder.setTimestamp(System.currentTimeMillis());

        db.collection(COLLECTION_ORDERS).add(newOrder)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Order placed successfully with ID: " + documentReference.getId());

                    updateProductSales(newOrder.getItems());

                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error placing order: " + e.getMessage());
                    callback.onFailure("Lỗi khi tạo đơn hàng. Vui lòng thử lại.");
                });
    }
}