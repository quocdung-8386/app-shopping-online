package com.example.apponline;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.apponline.models.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewAddressActivity extends AppCompatActivity {

    private EditText etName, etPhone, etAddressCity, etAddressDetail;
    private Button btnComplete, btnOffice, btnHome;
    private SwitchCompat switchDefault, switchPickup, switchReturn;
    private ImageButton btnBack;

    private String selectedAddressType = "Văn phòng";

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupListeners();

        updateAddressTypeUI(btnOffice, btnHome);
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddressCity = findViewById(R.id.et_address_city);
        etAddressDetail = findViewById(R.id.et_address_detail);

        btnComplete = findViewById(R.id.complete_button);
        btnOffice = findViewById(R.id.btn_office);
        btnHome = findViewById(R.id.btn_home);
        btnBack = findViewById(R.id.back_button);

        switchDefault = findViewById(R.id.switch_default);
        switchPickup = findViewById(R.id.switch_pickup);
        switchReturn = findViewById(R.id.switch_return);
    }
    private void setupListeners() {
        btnComplete.setOnClickListener(v -> saveAddress());
        btnBack.setOnClickListener(v -> finish());

        btnOffice.setOnClickListener(v -> selectAddressType("Văn phòng", btnOffice, btnHome));
        btnHome.setOnClickListener(v -> selectAddressType("Nhà riêng", btnHome, btnOffice));
    }


    private void updateAddressTypeUI(Button selectedButton, Button unselectedButton) {
        selectedButton.setBackgroundResource(R.drawable.rounded_button_primary);
        selectedButton.setTextColor(Color.WHITE);
        unselectedButton.setBackgroundResource(R.drawable.rounded_button_border_grey);
        unselectedButton.setTextColor(Color.parseColor("#555555"));
    }

    private void selectAddressType(String type, Button selectedButton, Button unselectedButton) {
        selectedAddressType = type;
        updateAddressTypeUI(selectedButton, unselectedButton);
        Toast.makeText(this, "Đã chọn: " + selectedAddressType, Toast.LENGTH_SHORT).show();
    }
    private void saveAddress() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Lỗi: Người dùng chưa đăng nhập.", Toast.LENGTH_LONG).show();
            return;
        }

        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = etAddressCity.getText().toString().trim();
        String detail = etAddressDetail.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || city.isEmpty() || detail.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin địa chỉ.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean isDefault = switchDefault.isChecked();
        boolean isPickup = switchPickup.isChecked();

        boolean isReturn = switchReturn.isChecked();

        Address newAddress = new Address(
                name,
                phone,
                detail,
                city,
                isDefault,
                isPickup,
                selectedAddressType
        );

        db.collection("users")
                .document(user.getUid())
                .collection("addresses")
                .add(newAddress)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(NewAddressActivity.this, "Lưu địa chỉ thành công!", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(NewAddressActivity.this, "Lỗi: Không thể lưu địa chỉ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}