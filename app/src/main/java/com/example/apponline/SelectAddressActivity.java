package com.example.apponline;

import android.app.Activity;
import android.app.AlertDialog; // 👈 Cần cho AlertDialog
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apponline.Adapters.AddressAdapter;
import com.example.apponline.models.Address;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SelectAddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private List<Address> addressList = new ArrayList<>();
    private Button addNewAddressButton;
    private ImageButton btnBack;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private static final String TAG = "SelectAddressActivity";
    public static final String SELECTED_ADDRESS_KEY = "selected_address";

    private final ActivityResultLauncher<Intent> newAddressLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(this, "Đã thêm địa chỉ mới. Đang tải lại...", Toast.LENGTH_SHORT).show();
                    loadAddressesFromFirestore();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        initViews();
        setupRecyclerView();
        setupListeners();
        loadAddressesFromFirestore();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.address_recycler_view);
        addNewAddressButton = findViewById(R.id.add_new_address_button);
        btnBack = findViewById(R.id.back_button);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        addNewAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(SelectAddressActivity.this, NewAddressActivity.class);
            newAddressLauncher.launch(intent);
        });
    }


    private void setupRecyclerView() {
        adapter = new AddressAdapter(this, addressList, new AddressAdapter.OnAddressSelectedListener() {

            @Override
            public void onAddressSelected(Address selectedAddress) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(SELECTED_ADDRESS_KEY, selectedAddress);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

                String detailInfo;
                try {
                    detailInfo = selectedAddress.getDetailAddress();
                } catch (Exception e) {
                    detailInfo = "Địa chỉ: " + selectedAddress.getName() + " - " + selectedAddress.getPhoneNumber();
                }
                Toast.makeText(SelectAddressActivity.this, "Đang gửi địa chỉ đã chọn: " + detailInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddressDeleted(Address addressToDelete) {
                showDeleteConfirmationDialog(addressToDelete);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadAddressesFromFirestore() {
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem địa chỉ.", Toast.LENGTH_LONG).show();
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("addresses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    addressList.clear();
                    int defaultAddressPosition = -1;
                    int positionCounter = 0;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Address address = document.toObject(Address.class);
                            address.setAddressId(document.getId());
                            addressList.add(address);

                            if (address.isDefault()) {
                                defaultAddressPosition = positionCounter;
                            }
                            positionCounter++;
                        } catch (Exception e) {
                            Log.e(TAG, "Lỗi ánh xạ Firestore thành Address: " + e.getMessage());
                        }
                    }

                    if (defaultAddressPosition != -1) {
                        adapter.setSelectedPosition(defaultAddressPosition);
                    } else if (!addressList.isEmpty()) {
                        adapter.setSelectedPosition(0);
                    } else {
                        adapter.setSelectedPosition(-1);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải địa chỉ: " + e.getMessage());
                    Toast.makeText(this, "Lỗi tải danh sách địa chỉ.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmationDialog(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận Xóa Địa chỉ")
                .setMessage("Bạn có chắc chắn muốn xóa địa chỉ của " + address.getName() + " không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteAddressFromFirestore(address);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAddressFromFirestore(Address address) {
        if (currentUser == null || address.getAddressId() == null) {
            Toast.makeText(this, "Lỗi: Không thể xóa địa chỉ.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("addresses")
                .document(address.getAddressId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xóa địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                    loadAddressesFromFirestore();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi xóa địa chỉ: " + e.getMessage());
                    Toast.makeText(this, "Lỗi: Xóa địa chỉ thất bại.", Toast.LENGTH_SHORT).show();
                });
    }
}