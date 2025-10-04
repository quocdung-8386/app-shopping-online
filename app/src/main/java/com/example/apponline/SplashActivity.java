package com.example.apponline;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;
    private static final String APP_PREFS = "AppPrefs";

    private static final String ONBOARDING_KEY = "ONBOARDING_COMPLETED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(this::checkAndNavigate, SPLASH_TIME_OUT);
    }

    private void checkAndNavigate() {
        SharedPreferences prefs = getSharedPreferences(APP_PREFS, MODE_PRIVATE);

        boolean hasSeenOnboarding = prefs.getBoolean(ONBOARDING_KEY, false);

        Intent intent;
        if (hasSeenOnboarding) {
            intent = new Intent(SplashActivity.this, DangNhapActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        }

        startActivity(intent);
        finish();
    }
}