package com.example.apponline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Cần tạo layout này

        // Chuyển Activity sau thời gian chờ
        new Handler().postDelayed(this::checkAndNavigate, SPLASH_TIME_OUT);
    }

    private void checkAndNavigate() {
        // Giả sử bạn sử dụng SharedPreferences để kiểm tra xem người dùng đã xem Onboarding chưa
        boolean hasSeenOnboarding = getSharedPreferences("AppPrefs", MODE_PRIVATE)
                .getBoolean("ONBOARDING_COMPLETED", false);

        Intent intent;
        if (hasSeenOnboarding) {
            // Nếu đã xem giới thiệu, chuyển đến màn hình đăng nhập hoặc màn hình chính
            intent = new Intent(SplashActivity.this, DangNhapActivity.class);
        } else {
            // Nếu chưa xem, chuyển đến màn hình giới thiệu
            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        }

        startActivity(intent);
        finish(); // Đóng SplashActivity
    }
}