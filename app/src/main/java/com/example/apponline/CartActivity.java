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

    private ImageButton btnBack;

    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvCartTotal = findViewById(R.id.tvCartTotal);
        btnProceedToCheckout = findViewById(R.id.btnProceedToCheckout);
        tvEmptyCartMessage = findViewById(R.id.tvEmptyCartMessage);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        setupCartList();
        updateSummary();

        btnProceedToCheckout.setOnClickListener(v -> {
            if (CartManager.getInstance().getTotalItems() > 0) {
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
        if (totalItems == 0) {
            rvCartItems.setVisibility(View.GONE);
            tvEmptyCartMessage.setVisibility(View.VISIBLE);
            tvCartTotal.setText("Giỏ hàng trống");
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