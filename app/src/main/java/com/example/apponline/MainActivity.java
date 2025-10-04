package com.example.apponline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.apponline.Adapters.CategoryAdapter;
import com.example.apponline.Adapters.ProductAdapter;
import com.example.apponline.models.Product;
import com.example.apponline.models.Category;
import com.example.apponline.firebase.FirebaseHelper;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvCategories, rvDailyDeals, rvFeaturedProducts;
    private BottomNavigationView bottomNav;
    private EditText searchBar;
    private ImageButton btnDailyDealPrev, btnDailyDealNext;
    private ImageButton btnFeaturedPrev, btnFeaturedNext;
    private FirebaseFirestore db;

    private final List<Category> sampleCategories = new ArrayList<>();
    private static final String TAG = "MainActivity";
    private static final int ITEM_WIDTH_DP = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseHelper.getFirestoreInstance();
        initViews();
        loadCategories();
        setupRecyclerViews();
        setupClickListeners();
        setupScrollListeners();

        if (!FirebaseHelper.isUserLoggedIn()) {
            startActivity(new Intent(this, DangNhapActivity.class));
            finish();
        }
    }

    private void initViews() {
        rvCategories = findViewById(R.id.rv_categories);
        rvDailyDeals = findViewById(R.id.rv_daily_deals);
        rvFeaturedProducts = findViewById(R.id.rv_featured_products);
        bottomNav = findViewById(R.id.bottom_navigation_bar);
        searchBar = findViewById(R.id.search_bar);

        btnDailyDealPrev = findViewById(R.id.btn_daily_deal_prev);
        btnDailyDealNext = findViewById(R.id.btn_daily_deal_next);
        btnFeaturedPrev = findViewById(R.id.btn_featured_prev);
        btnFeaturedNext = findViewById(R.id.btn_featured_next);

        searchBar.setFocusable(false);
    }

    private void loadCategories() {
        sampleCategories.add(new Category("Áo Nam", R.drawable.category_ao));
        sampleCategories.add(new Category("Quần Jeans", R.drawable.category_quan));
        sampleCategories.add(new Category("Giày Thể thao", R.drawable.category_giay));
        sampleCategories.add(new Category("Phụ kiện", R.drawable.category_phukien));
    }

    private void setupRecyclerViews() {
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(new CategoryAdapter(this, sampleCategories));

        rvDailyDeals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fetchProductsByField("dailyDeal", rvDailyDeals);
        fetchProductsByField("featured", rvFeaturedProducts);
    }

    private void fetchProductsByField(String fieldName, RecyclerView recyclerView) {

        db.collection("products")
                .whereEqualTo(fieldName, true)
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> fetchedProducts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Product product = document.toObject(Product.class);
                                product.setId(document.getId());
                                fetchedProducts.add(product);
                            } catch (Exception e) {
                                Log.e(TAG, "LỖI MAPPING FireStore cho tài liệu " + document.getId() + ": " + e.getMessage());
                            }
                        }
                        Log.d(TAG, "Tải thành công " + fetchedProducts.size() + " sản phẩm cho: " + fieldName);

                        if (!fetchedProducts.isEmpty()) {
                            recyclerView.setAdapter(new ProductAdapter(this, fetchedProducts));
                            // Kiểm tra lại trạng thái nút sau khi adapter được đặt
                            checkScrollLimits(recyclerView, fieldName.equals("dailyDeal") ? btnDailyDealPrev : btnFeaturedPrev, fieldName.equals("dailyDeal") ? btnDailyDealNext : btnFeaturedNext);
                        } else {
                            Log.w(TAG, "Không tìm thấy sản phẩm nào cho: " + fieldName + ". Kiểm tra lại dữ liệu Firestore và Index.");
                        }
                    } else {
                        Log.w(TAG, "Lỗi tải tài liệu cho " + fieldName + ": ", task.getException());
                    }
                });
    }

    private void setupClickListeners() {
        searchBar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        float density = getResources().getDisplayMetrics().density;
        final int scrollDistancePx = (int) (2 * ITEM_WIDTH_DP * density);

        btnDailyDealNext.setOnClickListener(v -> {
            rvDailyDeals.smoothScrollBy(scrollDistancePx, 0);
        });

        btnDailyDealPrev.setOnClickListener(v -> {
            rvDailyDeals.smoothScrollBy(-scrollDistancePx, 0);
        });


        btnFeaturedNext.setOnClickListener(v -> {
            rvFeaturedProducts.smoothScrollBy(scrollDistancePx, 0);
        });

        btnFeaturedPrev.setOnClickListener(v -> {
            rvFeaturedProducts.smoothScrollBy(-scrollDistancePx, 0);
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
    }


    private void setupScrollListeners() {


        rvDailyDeals.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkScrollLimits(recyclerView, btnDailyDealPrev, btnDailyDealNext);
            }
        });

        rvFeaturedProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkScrollLimits(recyclerView, btnFeaturedPrev, btnFeaturedNext);
            }
        });
    }

    private void checkScrollLimits(RecyclerView recyclerView, ImageButton btnPrev, ImageButton btnNext) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager == null || recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            btnPrev.setVisibility(View.GONE);
            btnNext.setVisibility(View.GONE);
            return;
        }

        int totalItemCount = layoutManager.getItemCount();

        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition(); // Dùng LastCompletelyVisible để kiểm tra chính xác

        if (firstVisibleItemPosition == 0) {
            btnPrev.setVisibility(View.GONE);
        } else {
            btnPrev.setVisibility(View.VISIBLE);
        }
        if (lastVisibleItemPosition == totalItemCount - 1) {
            btnNext.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
        }
    }
}