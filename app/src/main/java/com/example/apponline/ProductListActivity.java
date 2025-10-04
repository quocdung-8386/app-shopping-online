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
import com.example.apponline.GridSpacingItemDecoration;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private static final String TAG = "ProductListActivity";
    private static final int SPAN_COUNT = 2;
    private static final int SPACING_DP = 8;
    private static final boolean INCLUDE_EDGE = true;

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
        db = FirebaseFirestore.getInstance();

        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProducts = findViewById(R.id.rvProducts);
        tvNoProducts = findViewById(R.id.tvNoProducts);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent() != null) {
            categoryName = getIntent().getStringExtra("CATEGORY_NAME");
            if (categoryName != null) {
                tvCategoryTitle.setText(categoryName);
                fetchProductsByCategory(categoryName);
            } else {
                tvCategoryTitle.setText("Tất cả Sản phẩm");
            }
        }

        productList = new ArrayList<>();

        rvProducts.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));

        int spacingPx = dpToPx(SPACING_DP);
        rvProducts.addItemDecoration(new GridSpacingItemDecoration(SPAN_COUNT, spacingPx, INCLUDE_EDGE));

        productAdapter = new ProductAdapter(this, productList);
        rvProducts.setAdapter(productAdapter);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

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
    private void fetchProductsByCategory(String categoryName) {

        String categoryId = getCategoryId(categoryName);

        if (categoryId == null) {
            Toast.makeText(this, "Không tìm thấy ID danh mục cho: " + categoryName, Toast.LENGTH_LONG).show();
            tvNoProducts.setVisibility(View.VISIBLE);
            return;
        }

        db.collection("products")
                .whereEqualTo("category_id", categoryId)
                .get()
                .addOnCompleteListener(task -> {
                    productList.clear();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Product product = document.toObject(Product.class);
                                if (product != null) {
                                    product.setId(document.getId());
                                    productList.add(product);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi ánh xạ Document ID: " + document.getId(), e);
                                Toast.makeText(this, "Lỗi dữ liệu sản phẩm! Xem Logcat.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Lỗi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }


                    productAdapter.notifyDataSetChanged();
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