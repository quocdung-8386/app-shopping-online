package com.example.apponline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding); // Cần tạo layout này

        Button btnGetStarted = findViewById(R.id.btnGetStarted);

        btnGetStarted.setOnClickListener(v -> {
            // 1. Lưu trạng thái: Người dùng đã xem giới thiệu
            SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("ONBOARDING_COMPLETED", true);
            editor.apply();

            // 2. Chuyển đến màn hình Đăng nhập (hoặc màn hình chính nếu không cần đăng nhập)
            Intent intent = new Intent(OnboardingActivity.this, DangNhapActivity.class);
            startActivity(intent);
            finish();
        });
    }
}