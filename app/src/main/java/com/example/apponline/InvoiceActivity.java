package com.example.apponline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apponline.models.Order;

public class InvoiceActivity extends AppCompatActivity {

    private static final String TAG = "InvoiceActivity";

    private TextView tvOrderId;
    private TextView tvTotalAmount;
    private TextView tvPaymentMethod;

    private Button btnViewOrders;
    private Button btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        loadInvoiceDetails();

        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceActivity.this, OrdersHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadInvoiceDetails() {
        Intent intent = getIntent();
        Order order = null;
        String orderId = null;
        double total = 0.0;
        String paymentMethod = null;

        try {
            order = (Order) intent.getSerializableExtra("order_detail");
        } catch (Exception e) {
            Log.w(TAG, "Không thể lấy đối tượng Order từ Intent.", e);
        }

        if (order != null) {
            orderId = order.getOrderId();
            total = order.getTotalAmount();
            paymentMethod = order.getPaymentMethod();
            Log.i(TAG, "Dữ liệu được tải từ đối tượng Order (Lịch sử).");

        } else {
            orderId = intent.getStringExtra("ORDER_ID");
            total = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0);
            paymentMethod = intent.getStringExtra("PAYMENT_METHOD");
            Log.i(TAG, "Dữ liệu được tải từ 3 Extras riêng biệt (Checkout).");
        }

        if (orderId != null && !orderId.isEmpty() && total > 0) {
            tvOrderId.setText("Mã đơn hàng: #" + orderId);
            tvTotalAmount.setText(String.format("Tổng thanh toán: %,.0f VNĐ", total));
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                tvPaymentMethod.setText("Hình thức: " + paymentMethod);
            } else {
                tvPaymentMethod.setText("Hình thức: Không xác định");
            }

        } else {
            Log.e(TAG, "Lỗi: Không đủ dữ liệu để hiển thị hóa đơn.");
            tvOrderId.setText("Mã đơn hàng: #LỖI_DỮ_LIỆU");
            tvTotalAmount.setText("Tổng thanh toán: 0 VNĐ");
            tvPaymentMethod.setText("Hình thức: Không thể tải");
            Toast.makeText(this, "Không thể tải chi tiết hóa đơn. Dữ liệu bị thiếu.", Toast.LENGTH_LONG).show();
        }
    }
}