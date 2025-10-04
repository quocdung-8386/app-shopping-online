package com.example.apponline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.apponline.firebase.FirebaseHelper;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout;
    private TextView tvMyOrders, tvShippingAddress, tvUserEmail;
    private ImageButton btnBack, btnChoosePhoto;
    private ImageView imgProfilePicture;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        initImagePickerLauncher();
        setupClickListeners();
        loadUserProfile();
    }

    private void initViews() {
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        tvMyOrders = findViewById(R.id.tvMyOrders);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
    }

    private void initImagePickerLauncher() {
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imgProfilePicture.setImageURI(uri);
                        Toast.makeText(this, "Đã chọn ảnh: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadUserProfile() {
        FirebaseUser user = FirebaseHelper.getFirebaseAuth().getCurrentUser();

        if (user != null) {
            String userEmail = user.getEmail() != null ? user.getEmail() : "Email không khả dụng";
            if (tvUserEmail != null) {
                tvUserEmail.setText(userEmail);
            }

            Uri photoUrl = user.getPhotoUrl();
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(imgProfilePicture);
            } else {
                imgProfilePicture.setImageResource(R.drawable.ic_profile);
            }
        } else {
            if (tvUserEmail != null) {
                tvUserEmail.setText("Chưa đăng nhập");
            }
            Toast.makeText(this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, DangNhapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnChoosePhoto.setOnClickListener(v -> mGetContent.launch("image/*"));

        tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, OrdersHistoryActivity.class));
        });
        tvShippingAddress.setOnClickListener(v -> {
            Toast.makeText(this, "Mở màn hình quản lý địa chỉ...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, SelectAddressActivity.class));
        });
        btnLogout.setOnClickListener(v -> {
            FirebaseHelper.getFirebaseAuth().signOut();
            Toast.makeText(ProfileActivity.this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, DangNhapActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}