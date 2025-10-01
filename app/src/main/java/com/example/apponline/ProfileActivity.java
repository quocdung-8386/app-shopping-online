package com.example.apponline;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton; // 👈 CẦN IMPORT ImageButton
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.apponline.firebase.FirebaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvMyOrders, tvShippingAddress;
    // 1. Khai báo biến cho nút Back
    private ImageButton btnBack;
    // Thêm TextView cho Tên và Email người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Sử dụng XML đã cung cấp

        // Ánh xạ Views
        btnLogout = findViewById(R.id.btnLogout);
        tvMyOrders = findViewById(R.id.tvMyOrders);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);

        // 2. Ánh xạ nút Back (ID từ layout đã sửa là btnBack)
        btnBack = findViewById(R.id.btnBack);

        // 3. Xử lý sự kiện click cho nút Back
        btnBack.setOnClickListener(v -> {
            finish(); // Đóng Activity hiện tại và quay lại màn hình trước
        });

        // ... Ánh xạ các TextView thông tin người dùng

        loadUserProfile();
        setupClickListeners();
    }

    private void loadUserProfile() {
        String userName = FirebaseHelper.getCurrentUserName(); // Giả định có helper này
        String userEmail = FirebaseHelper.getCurrentUserEmail(); // Giả định có helper này

        // TODO: Cập nhật TextView với tên và email
        // tvUserName.setText(userName);
        // Ví dụ: tvUserEmail.setText(userEmail);

    }

    private void setupClickListeners() {
        tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrdersHistoryActivity.class));
        });

        tvShippingAddress.setOnClickListener(v -> {

            Toast.makeText(this, "Mở màn hình quản lý địa chỉ...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, SelectAddressActivity.class));
            // TODO: start ShippingAddressActivity
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseHelper.getFirebaseAuth().signOut();
            Toast.makeText(ProfileActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

            // Chuyển về màn hình Đăng nhập/Đăng ký
            Intent intent = new Intent(ProfileActivity.this, DangNhapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}