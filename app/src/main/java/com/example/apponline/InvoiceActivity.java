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
    // 🚨 btnBack đã bị loại bỏ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        // 1. Ánh xạ các thành phần
        tvOrderId = findViewById(R.id.tvOrderId);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);

        // 2. Tải chi tiết hóa đơn (logic cũ bị lỗi, cần thay bằng hàm mới)
        loadInvoiceDetails();

        // 3. Xử lý sự kiện nút btnViewOrders (Xem đơn hàng của tôi)
        btnViewOrders.setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceActivity.this, OrdersHistoryActivity.class);
            // Thêm cờ để dọn dẹp Activity stack, ngăn quay lại Checkout
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // 4. Xử lý sự kiện nút Continue Shopping
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(InvoiceActivity.this, MainActivity.class);
            // Thêm cờ để dọn dẹp Activity stack, quay về màn hình chính
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Tải và hiển thị chi tiết hóa đơn, xử lý cả luồng Order Object (Lịch sử)
     * và luồng 3 Extras riêng biệt (Checkout)
     */
    private void loadInvoiceDetails() {
        Intent intent = getIntent();
        Order order = null;
        String orderId = null;
        double total = 0.0;
        String paymentMethod = null;

        // BƯỚC 1: CỐ GẮNG LẤY ĐỐI TƯỢNG ORDER (TỪ OrdersHistoryActivity)
        try {
            order = (Order) intent.getSerializableExtra("order_detail");
        } catch (Exception e) {
            Log.w(TAG, "Không thể lấy đối tượng Order từ Intent.", e);
        }

        if (order != null) {
            // Trường hợp 1: Dữ liệu được tải từ đối tượng Order (Lịch sử)
            orderId = order.getOrderId();
            total = order.getTotalAmount();
            paymentMethod = order.getPaymentMethod();
            Log.i(TAG, "Dữ liệu được tải từ đối tượng Order (Lịch sử).");

        } else {
            // Trường hợp 2: Cố gắng lấy 3 Extras riêng biệt (TỪ CheckoutActivity)
            orderId = intent.getStringExtra("ORDER_ID");
            total = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0);
            paymentMethod = intent.getStringExtra("PAYMENT_METHOD");
            Log.i(TAG, "Dữ liệu được tải từ 3 Extras riêng biệt (Checkout).");
        }

        // BƯỚC 2: HIỂN THỊ DỮ LIỆU ĐÃ TẢI
        if (orderId != null && !orderId.isEmpty() && total > 0) {

            // Hiển thị mã đơn hàng
            tvOrderId.setText("Mã đơn hàng: #" + orderId);

            // Hiển thị tổng thanh toán (đã kiểm tra total > 0)
            tvTotalAmount.setText(String.format("Tổng thanh toán: %,.0f VNĐ", total));

            // Hiển thị hình thức thanh toán
            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                tvPaymentMethod.setText("Hình thức: " + paymentMethod);
            } else {
                tvPaymentMethod.setText("Hình thức: Không xác định");
            }

        } else {
            // Trường hợp LỖI DỮ LIỆU (Nếu cả 2 luồng đều không thành công)
            Log.e(TAG, "Lỗi: Không đủ dữ liệu để hiển thị hóa đơn.");
            tvOrderId.setText("Mã đơn hàng: #LỖI_DỮ_LIỆU");
            tvTotalAmount.setText("Tổng thanh toán: 0 VNĐ");
            tvPaymentMethod.setText("Hình thức: Không thể tải");
            Toast.makeText(this, "Không thể tải chi tiết hóa đơn. Dữ liệu bị thiếu.", Toast.LENGTH_LONG).show();
        }
    }
}