package com.example.apponline;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton; // 👈 CẦN IMPORT ImageButton
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apponline.Adapters.CartAdapter;
import com.example.apponline.models.OrderItem;
import com.example.apponline.firebase.CartManager;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private TextView tvCartTotal;
    private Button btnProceedToCheckout;
    private TextView tvEmptyCartMessage;

    // 1. Khai báo biến cho nút Back
    private ImageButton btnBack;

    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ Views
        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnProceedToCheckout = findViewById(R.id.btnProceedToCheckout);
        tvEmptyCartMessage = findViewById(R.id.tvEmptyCartMessage);

        // 2. Ánh xạ nút Back
        btnBack = findViewById(R.id.btnBack);

        // 3. Xử lý sự kiện click cho nút Back
        btnBack.setOnClickListener(v -> {
            finish(); // Đóng Activity hiện tại và quay lại màn hình trước
        });

        setupCartList();
        updateSummary();

        btnProceedToCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getTotalItems() > 0) {
                // Chuyển sang màn hình Thanh toán
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            } else {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCartList() {
        List<OrderItem> items = CartManager.getInstance().getCartItems();

        cartAdapter = new CartAdapter(this, items, new CartAdapter.CartUpdateListener() {
            @Override
            public void onQuantityChanged() {
                updateSummary();
            }
            @Override
            public void onItemRemoved() {
                updateSummary();
            }
        });

        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void updateSummary() {
        double total = CartManager.getInstance().calculateTotal();
        int totalItems = CartManager.getInstance().getTotalItems();

        tvCartTotal.setText(String.format("Tổng cộng: %,.0f VNĐ", total));
        btnProceedToCheckout.setText(String.format("Tiến hành Thanh toán (%d món)", totalItems));

        // Xử lý giao diện khi giỏ hàng trống/có hàng
        if (totalItems == 0) {
            rvCartItems.setVisibility(View.GONE);
            tvEmptyCartMessage.setVisibility(View.VISIBLE);
            tvCartTotal.setText("Giỏ hàng trống");
            // Tùy chọn: Ẩn luôn nút Checkout
            btnProceedToCheckout.setVisibility(View.GONE);
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            tvEmptyCartMessage.setVisibility(View.GONE);
            btnProceedToCheckout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateSummary();

        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
    }
}