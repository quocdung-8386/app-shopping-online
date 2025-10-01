package com.example.apponline.firebase;

import android.util.Log;
import com.example.apponline.models.Order;
import com.example.apponline.models.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderManager quản lý các thao tác liên quan đến Order (Lịch sử đơn hàng và tạo đơn hàng).
 * Sử dụng mẫu Singleton.
 */
public class OrderManager {

    private static final String TAG = "OrderManager";
    private static final String COLLECTION_ORDERS = "orders";

    private static OrderManager instance;
    private FirebaseFirestore db;

    // =========================================================================
    // INTERFACE CALLBACK
    // =========================================================================
    public interface OrderLoadCallback {
        void onOrdersLoaded(List<Order> orders);
        void onFailure(String errorMessage);
    }
    // =========================================================================

    private OrderManager() {
        db = FirebaseHelper.getFirestoreInstance();
    }

    public static synchronized OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    // =========================================================================
    // TẢI LỊCH SỬ ĐƠN HÀNG
    // =========================================================================

    /**
     * Tải tất cả các đơn hàng của người dùng hiện tại từ Firestore.
     */
    public void fetchUserOrders(OrderLoadCallback callback) {
        String userId = FirebaseHelper.getCurrentUserId();

        if (userId == null) {
            String errorMsg = "User not logged in, cannot fetch orders.";
            Log.w(TAG, errorMsg);
            callback.onFailure(errorMsg);
            return;
        }

        db.collection(COLLECTION_ORDERS)
                .whereEqualTo("userId", userId) // LỌC theo userId
                .orderBy("timestamp", Query.Direction.DESCENDING) // Hiển thị đơn hàng mới nhất trước
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Order> loadedOrders = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        try {
                            Order order = document.toObject(Order.class);

                            if (order != null) {
                                // 🚨 SỬA LỖI: Gán Document ID cho trường orderId (nếu bạn dùng nó như ID chính thức)
                                // Nếu trường orderId đã có trong Firestore, dòng này chỉ xác nhận ID.
                                // Nếu bạn đang dùng Document ID làm ID chính:
                                order.setOrderId(document.getId());

                                // 🚨 SỬA LỖI: Đảm bảo trường items không NULL (phòng ngừa lỗi trong Firestore)
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
                            // Bỏ qua document bị lỗi và tiếp tục với các document khác
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

    // =========================================================================
    // TẠO ĐƠN HÀNG (Mẫu)
    // =========================================================================

    /**
     * Hàm mẫu để tạo và lưu đơn hàng mới vào Firestore.
     */
    public void placeOrder(Order newOrder, OrderPlaceCallback callback) {
        // Tạo một document reference mới để Firestore tự động tạo ID
        // Nếu newOrder đã có orderId, dùng set() thay vì add()

        db.collection(COLLECTION_ORDERS).add(newOrder)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Order placed successfully with ID: " + documentReference.getId());
                    callback.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error placing order: " + e.getMessage());
                    callback.onFailure("Lỗi khi tạo đơn hàng. Vui lòng thử lại.");
                });
    }

    public interface OrderPlaceCallback {
        void onSuccess(String orderId);
        void onFailure(String errorMessage);
    }
}