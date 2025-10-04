package com.example.apponline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apponline.Adapters.CartAdapter;
import com.example.apponline.firebase.CartManager;
import com.example.apponline.firebase.FirebaseHelper;
import com.example.apponline.firebase.NotificationHelper;
import com.example.apponline.models.Address;
import com.example.apponline.models.Order;
import com.example.apponline.models.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private Button btnPlaceOrder;
    private TextView tvFinalTotal;
    private RadioGroup rgPaymentMethods;
    private TextView tvShippingAddress;
    private RecyclerView rvOrderItems;
    private View addressContainer;

    private String selectedPaymentMethod = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Address currentShippingAddressObject = null;
    private final String DEFAULT_ADDRESS_TEXT = "Chạm để CHỌN hoặc THÊM địa chỉ giao hàng";
    private static final String TAG = "CheckoutActivity";

    private static final String SELECTED_ADDRESS_KEY = "selected_address";
    private final ActivityResultLauncher<Intent> selectAddressLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Address newAddress = data.getParcelableExtra(SELECTED_ADDRESS_KEY);

                    if (newAddress != null) {
                        currentShippingAddressObject = newAddress;
                        updateShippingAddressUI(newAddress);
                        Toast.makeText(this, " Đã cập nhật địa chỉ giao hàng.", Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseHelper.getFirestoreInstance();
        btnPlaceOrder = findViewById(R.id.btnFinalPlaceOrder);
        tvFinalTotal = findViewById(R.id.tvCheckoutTotal);
        rgPaymentMethods = findViewById(R.id.rgPaymentMethods);
        tvShippingAddress = findViewById(R.id.tvShippingAddress);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        addressContainer = findViewById(R.id.clShippingAddressContainer);

        if (addressContainer != null) {
            addressContainer.setOnClickListener(v -> navigateToAddressSelection());
        }

        setupOrderItemsList();

        loadDefaultAddress();

        setupPaymentMethodSelection();

        btnPlaceOrder.setOnClickListener(v -> processOrderPlacement());
    }
    private void loadDefaultAddress() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            updateShippingAddressUI(null);
            updateSummary();
            return;
        }
        db.collection("users")
                .document(user.getUid())
                .collection("addresses")
                .whereEqualTo("default", true)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Address defaultAddress = queryDocumentSnapshots.getDocuments().get(0).toObject(Address.class);
                        currentShippingAddressObject = defaultAddress;
                        updateShippingAddressUI(defaultAddress);
                    } else {
                        loadFirstAddress(user.getUid());
                    }
                    updateSummary();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải địa chỉ mặc định: " + e.getMessage());
                    updateShippingAddressUI(null);
                    updateSummary();
                });
    }
    private void loadFirstAddress(String userId) {
        db.collection("users")
                .document(userId)
                .collection("addresses")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Address firstAddress = queryDocumentSnapshots.getDocuments().get(0).toObject(Address.class);
                        currentShippingAddressObject = firstAddress;
                        updateShippingAddressUI(firstAddress);
                    } else {
                        updateShippingAddressUI(null);
                    }
                });
    }
    private void updateShippingAddressUI(Address address) {
        if (address == null) {
            tvShippingAddress.setText(DEFAULT_ADDRESS_TEXT);
        } else {

            String detailAddress = "Địa chỉ chi tiết không rõ";
            try {
                detailAddress = address.getDetailAddress();
            } catch (Exception e) {
                Log.e(TAG, "Lỗi gọi getDetailAddress(). Vui lòng kiểm tra Address.java.", e);
            }
            String addressLine1 = String.format("%s | %s", address.getName(), address.getPhoneNumber());
            String addressLine2 = String.format("%s, %s", detailAddress, address.getCityState());
            tvShippingAddress.setText(addressLine1 + "\n" + addressLine2);
        }
    }
    private void setupOrderItemsList() {
        List<OrderItem> items = CartManager.getInstance().getCartItems();
        CartAdapter orderAdapter = new CartAdapter(this, items, null);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(orderAdapter);
    }

    private void updateSummary() {
        double finalTotal = CartManager.getInstance().calculateTotal();
        tvFinalTotal.setText(String.format("Tổng cộng: %,.0f VNĐ", finalTotal));
        updateShippingAddressUI(currentShippingAddressObject);
    }

    private void navigateToAddressSelection() {
        Intent intent = new Intent(CheckoutActivity.this, SelectAddressActivity.class);
        selectAddressLauncher.launch(intent);
    }

    private Address getShippingAddressObject() {
        return currentShippingAddressObject;
    }
    private void setupPaymentMethodSelection() {
        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) {
                selectedPaymentMethod = selectedRadioButton.getText().toString();
            }
        });

        RadioButton rbCOD = findViewById(R.id.rbCOD);
        if (rbCOD != null) {
            rbCOD.setChecked(true);
            selectedPaymentMethod = rbCOD.getText().toString();
        }
    }
    private void processOrderPlacement() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }
        List<OrderItem> cartItems = CartManager.getInstance().getCartItems();
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        Address shippingAddressObject = getShippingAddressObject();
        if (shippingAddressObject == null) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng.", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedPaymentMethod == null || selectedPaymentMethod.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn phương thức thanh toán.", Toast.LENGTH_SHORT).show();
            return;
        }

        double finalTotal = CartManager.getInstance().calculateTotal();
        String detailAddressString;
        try {
            detailAddressString = shippingAddressObject.getDetailAddress();
        } catch (Exception e) {
            detailAddressString = "Địa chỉ chi tiết không xác định (Lỗi Address.java)";
        }

        String shippingAddress = String.format(
                "%s, %s | Người nhận: %s - %s",
                detailAddressString,
                shippingAddressObject.getCityState(),
                shippingAddressObject.getName(),
                shippingAddressObject.getPhoneNumber()
        );

        boolean paymentSuccessful = simulatePayment(selectedPaymentMethod);

        if (!paymentSuccessful) {
            Toast.makeText(this, "Thanh toán thất bại. Vui lòng thử phương thức khác.", Toast.LENGTH_LONG).show();
            return;
        }

        String newOrderId = "ORD" + String.valueOf(System.currentTimeMillis()).substring(4);

        Order newOrder = new Order(
                newOrderId,
                userId,
                finalTotal,
                shippingAddress,
                cartItems,
                selectedPaymentMethod
        );
        FirebaseFirestore db = FirebaseHelper.getFirestoreInstance();
        btnPlaceOrder.setEnabled(false);
        db.collection("orders")
                .document(newOrderId)
                .set(newOrder)
                .addOnSuccessListener(aVoid -> {
                    CartManager.getInstance().clearCart();
                    String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                    if (currentUserId != null) {
                        CartManager.getInstance().saveCartToFirestore(currentUserId);
                    }

                    NotificationHelper.showOrderSuccessNotification(
                            CheckoutActivity.this,
                            newOrderId,
                            finalTotal
                    );

                    Intent intent = new Intent(CheckoutActivity.this, InvoiceActivity.class);
                    intent.putExtra("ORDER_ID", newOrderId);
                    intent.putExtra("TOTAL_AMOUNT", finalTotal);
                    intent.putExtra("PAYMENT_METHOD", selectedPaymentMethod);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(CheckoutActivity.this, "Lỗi đặt hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private boolean simulatePayment(String method) {
        return true;
    }
}