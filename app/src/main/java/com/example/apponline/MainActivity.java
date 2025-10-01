package com.example.apponline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2; // 🚨 Import ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout; // 🚨 Import TabLayout
import com.google.android.material.tabs.TabLayoutMediator; // 🚨 Import TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.apponline.Adapters.CategoryAdapter;
import com.example.apponline.Adapters.ProductAdapter;
import com.example.apponline.Adapters.BannerAdapter; // 🚨 Import BannerAdapter
import com.example.apponline.models.Product;
import com.example.apponline.firebase.FirebaseHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvCategories, rvDailyDeals, rvFeaturedProducts;
    private BottomNavigationView bottomNav;
    private EditText searchBar;

    // 🚨 KHAI BÁO VIEWS CHO BANNER
    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;

    private FirebaseFirestore db;
    private final List<String> sampleCategories = Arrays.asList("Áo Nam", "Quần Jeans", "Giày Thể thao", "Phụ kiện");
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseHelper.getFirestoreInstance();

        initViews();
        setupRecyclerViews();
        setupClickListeners();

        // 🚨 GỌI HÀM TẢI BANNER
        loadBanners();

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

        // 🚨 ÁNH XẠ VIEWS CHO BANNER
        bannerViewPager = findViewById(R.id.banner_view_pager);
        bannerIndicator = findViewById(R.id.banner_indicator);

        searchBar.setFocusable(false);
    }

    private void setupRecyclerViews() {
        // Cài đặt Adapter cho Danh mục
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(new CategoryAdapter(this, sampleCategories));

        // Cài đặt LayoutManager cho Deals và Featured
        rvDailyDeals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Tải sản phẩm
        fetchProductsByField("isDailyDeal", rvDailyDeals);
        fetchProductsByField("isFeatured", rvFeaturedProducts);
    }

    // 🚨 HÀM MỚI: Tải Banner từ Firebase
    private void loadBanners() {
        db.collection("promotions").document("home_banners") // Document ID trong Firestore
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("images")) {
                        // Lấy danh sách URL ảnh (cần đảm bảo tên trường là 'images' trong Firestore)
                        List<String> imageUrls = (List<String>) documentSnapshot.get("images");

                        if (imageUrls != null && !imageUrls.isEmpty()) {

                            // 1. Khởi tạo và gán Adapter
                            BannerAdapter bannerAdapter = new BannerAdapter(this, imageUrls);
                            bannerViewPager.setAdapter(bannerAdapter);

                            // 2. Liên kết ViewPager2 với TabLayout Indicator
                            new TabLayoutMediator(bannerIndicator, bannerViewPager,
                                    (tab, position) -> { /* Không cần thiết lập text */ }
                            ).attach();

                            Log.i(TAG, "Tải banner thành công: " + imageUrls.size() + " ảnh.");

                        } else {
                            Log.w(TAG, "Danh sách URL banner rỗng.");
                        }
                    } else {
                        Log.e(TAG, "Document 'home_banners' không tồn tại hoặc thiếu trường 'images'.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải Banners:", e);
                });
    }

    private void fetchProductsByField(String fieldName, RecyclerView recyclerView) {
        // ... (phương thức fetchProductsByField giữ nguyên)
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
                                // Gán Document ID nếu cần, ví dụ: product.setProductId(document.getId());
                                fetchedProducts.add(product);
                            } catch (Exception e) {
                                Log.e(TAG, "Lỗi mapping Product: " + e.getMessage());
                            }
                        }
                        recyclerView.setAdapter(new ProductAdapter(this, fetchedProducts));
                    } else {
                        Log.w(TAG, "Lỗi tải tài liệu cho " + fieldName + ": ", task.getException());
                    }
                });
    }

    private void setupClickListeners() {
        // ... (phương thức setupClickListeners giữ nguyên)
        searchBar.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
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
}