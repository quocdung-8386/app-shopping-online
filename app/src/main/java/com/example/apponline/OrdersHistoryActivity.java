package com.example.apponline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button; // 🚨 Cần import Button
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apponline.Adapters.OrderHistoryAdapter;
import com.example.apponline.models.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class OrdersHistoryActivity extends AppCompatActivity
        implements OrderHistoryAdapter.OnOrderClickListener {

    private static final String TAG = "OrdersHistoryActivity";
    private RecyclerView rvOrders;
    private TextView tvNoOrders;
    private ImageButton btnBack;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ Views
        rvOrders = findViewById(R.id.rvOrdersHistory);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        btnBack = findViewById(R.id.btnBack); // 2. ÁNH XẠ btnBack

        // Thiết lập RecyclerView
        orderList = new ArrayList<>();
        adapter = new OrderHistoryAdapter(this, orderList, this);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        // 3. XỬ LÝ SỰ KIỆN NÚT BACK
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                onBackPressed();
            });
        } else {
            Log.w(TAG, "Cảnh báo: Button btnBack không tìm thấy trong layout.");
        }

        loadOrders();
    }

    private void loadOrders() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử đơn hàng.", Toast.LENGTH_LONG).show();
            tvNoOrders.setText("Bạn chưa đăng nhập.");
            tvNoOrders.setVisibility(View.VISIBLE);
            return;
        }

        db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Order order = document.toObject(Order.class);

                                // Gán Document ID
                                order.setOrderId(document.getId());

                                orderList.add(order);

                            } catch (Exception e) {
                                Log.e(TAG, "MAPPING ERROR for Document ID: " + document.getId() + ". Lỗi: " + e.getMessage());
                            }
                        }

                        adapter.notifyDataSetChanged();

                        if (orderList.isEmpty()) {
                            tvNoOrders.setText("Bạn chưa có đơn hàng nào.");
                            tvNoOrders.setVisibility(View.VISIBLE);
                            rvOrders.setVisibility(View.GONE);
                        } else {
                            tvNoOrders.setVisibility(View.GONE);
                            rvOrders.setVisibility(View.VISIBLE);
                            Log.i(TAG, "Tải thành công " + orderList.size() + " đơn hàng.");
                        }

                    } else {
                        Log.e(TAG, "Lỗi tải đơn hàng: ", task.getException());
                        Toast.makeText(OrdersHistoryActivity.this, "Lỗi tải lịch sử đơn hàng.", Toast.LENGTH_SHORT).show();
                        tvNoOrders.setVisibility(View.VISIBLE);
                        tvNoOrders.setText("Không thể tải đơn hàng. Vui lòng kiểm tra kết nối.");
                        rvOrders.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onOrderClick(Order order) {
        Intent intent = new Intent(this, InvoiceActivity.class);
        intent.putExtra("order_detail", order);
        startActivity(intent);
    }
}