package com.example.apponline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apponline.Adapters.ProductAdapter;
import com.example.apponline.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private static final String TAG = "ProductListActivity";

    private TextView tvCategoryTitle;
    private RecyclerView rvProducts;
    private TextView tvNoProducts;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();

        // 1. Ánh xạ View
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProducts = findViewById(R.id.rvProducts);
        tvNoProducts = findViewById(R.id.tvNoProducts);
        toolbar = findViewById(R.id.toolbar);

        // Thiết lập Toolbar (có nút Back)
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Xử lý nút Back (khi người dùng nhấn icon HomeAsUp)
        toolbar.setNavigationOnClickListener(v -> finish());

        // 2. Nhận dữ liệu từ Intent (tên danh mục)
        if (getIntent() != null) {
            categoryName = getIntent().getStringExtra("CATEGORY_NAME");
            if (categoryName != null) {
                tvCategoryTitle.setText(categoryName);
                fetchProductsByCategory(categoryName); // Bắt đầu tải sản phẩm
            } else {
                tvCategoryTitle.setText("Tất cả Sản phẩm");
                // Tải tất cả sản phẩm nếu cần
            }
        }

        // 3. Thiết lập RecyclerView
        productList = new ArrayList<>();
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setAdapter(productAdapter);
    }

    // Hàm mới: Ánh xạ Tên danh mục hiển thị thành ID Firestore
    private String getCategoryId(String categoryName) {
        if (categoryName == null) return null;
        switch (categoryName) {
            case "Áo Nam":
                return "ao_nam";
            case "Quần Jeans":
                return "quan_jeans";
            case "Giày Thể thao":
                return "giay_the_thao";
            case "Phụ kiện":
                return "phu_kien";
            default:
                return null;
        }
    }

    /**
     * Tải sản phẩm từ Firestore dựa trên tên danh mục.
     * @param categoryName Tên danh mục hiển thị.
     */
    private void fetchProductsByCategory(String categoryName) {

        String categoryId = getCategoryId(categoryName); // Lấy ID Firestore

        if (categoryId == null) {
            Toast.makeText(this, "Không tìm thấy ID danh mục cho: " + categoryName, Toast.LENGTH_LONG).show();
            tvNoProducts.setVisibility(View.VISIBLE);
            return;
        }

        db.collection("products")
                // ĐÃ SỬA LỖI: Truy vấn theo trường "category_id" trong Firestore
                .whereEqualTo("category_id", categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    productList.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Product product = document.toObject(Product.class);
                                // SỬA LỖI: Gán ID của Document vào đối tượng Product
                                if (product != null) {
                                    product.setId(document.getId());
                                    productList.add(product);
                                }
                            } catch (Exception e) {
                                // SỬA LỖI: Bắt và Log lỗi ánh xạ (ClassCastException)
                                Log.e(TAG, "Lỗi ánh xạ Document ID: " + document.getId(), e);
                                Toast.makeText(this, "Lỗi dữ liệu sản phẩm! Xem Logcat.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // Lỗi kết nối hoặc quyền hạn
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }


                    productAdapter.notifyDataSetChanged();

                    // Hiển thị thông báo không có sản phẩm
                    if (productList.isEmpty()) {
                        tvNoProducts.setVisibility(View.VISIBLE);
                        rvProducts.setVisibility(View.GONE);
                    } else {
                        tvNoProducts.setVisibility(View.GONE);
                        rvProducts.setVisibility(View.VISIBLE);
                    }
                });
    }
}