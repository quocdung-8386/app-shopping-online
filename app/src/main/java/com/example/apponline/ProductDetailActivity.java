package com.example.apponline;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.apponline.firebase.CartManager;
import com.example.apponline.firebase.WishlistManager;
import com.example.apponline.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetailActivity extends AppCompatActivity {

    // Khai báo Views
    private ImageView ivProductImage;
    private TextView tvProductName, tvProductPrice, tvProductOriginalPrice, tvProductDescription, tvProductRating;
    private TextView tvSizeLabel;
    private LinearLayout llSizesContainer;
    private Button btnAddToCart;
    private ImageButton btnBack;

    // Biến cho nút Yêu thích
    private ImageButton btnFavorite;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private WishlistManager wishlistManager;

    // Dữ liệu
    private String productId;
    private Product currentProduct;
    private String selectedSize = null;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        wishlistManager = WishlistManager.getInstance();

        // Ánh xạ Views
        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvProductOriginalPrice = findViewById(R.id.tvProductOriginalPrice);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductRating = findViewById(R.id.tvProductRating);
        tvSizeLabel = findViewById(R.id.tvSizeLabel);
        llSizesContainer = findViewById(R.id.llSizesContainer);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBack = findViewById(R.id.btnBack);

        // ÁNH XẠ: Nút Yêu thích
        btnFavorite = findViewById(R.id.btnFavorite);

        // Lấy ID sản phẩm
        productId = getIntent().getStringExtra("PRODUCT_ID");
        if (productId == null) {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Tải dữ liệu sản phẩm
        fetchProductDetails(productId);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút Thêm vào Giỏ hàng
        btnAddToCart.setOnClickListener(v -> addToCart());

        // Xử lý nút Yêu thích
        setupFavoriteClickListener();
    }

    private void setupFavoriteClickListener() {
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> toggleFavorite());
        }
    }

    private void checkFavoriteStatus() {
        if (currentProduct == null || mAuth.getCurrentUser() == null) return;

        // Kiểm tra trạng thái yêu thích từ WishlistManager
        isFavorite = wishlistManager.isProductInWishlist(currentProduct);
        updateFavoriteIcon(isFavorite);
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        if (btnFavorite == null) return;

        if (isFavorite) {
            // 💡 Đã yêu thích: Đặt icon trái tim đầy và màu Đỏ/Hồng
            // (Giả sử bạn có drawable ic_heart_filled và màu R.color.red)
            // Nếu không có, dùng ic_heart và màu đỏ.
            btnFavorite.setImageResource(R.drawable.ic_heart);

            btnFavorite.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_dark));
        } else {
            // 💡 Chưa yêu thích: Đặt icon trái tim rỗng và màu Đen/Xám
            btnFavorite.setImageResource(R.drawable.ic_heart);
            btnFavorite.setColorFilter(ContextCompat.getColor(this, android.R.color.black));
        }
    }

    private void toggleFavorite() {
        if (currentProduct == null) return;

        // Bắt buộc đăng nhập
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào yêu thích.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorite) {
            // Bỏ yêu thích
            wishlistManager.removeProductFromWishlist(currentProduct);
            Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích.", Toast.LENGTH_SHORT).show();
        } else {
            // Thêm vào yêu thích
            wishlistManager.addProductToWishlist(currentProduct);
            Toast.makeText(this, "Đã thêm vào danh sách yêu thích! ❤️", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật trạng thái và UI
        isFavorite = !isFavorite;
        updateFavoriteIcon(isFavorite);

        // 💡 Đã XÓA logic lưu trữ trùng lặp. WishlistManager chịu trách nhiệm tự động lưu.
    }

    private void fetchProductDetails(String id) {
        db.collection("products").document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentProduct = documentSnapshot.toObject(Product.class);

                        if (currentProduct != null) {

                            // 🚨 SỬA LỖI QUAN TRỌNG: GÁN DOCUMENT ID CHO SẢN PHẨM HIỆN TẠI
                            currentProduct.setId(documentSnapshot.getId());

                            displayProductDetails(currentProduct);
                            setupSizeSelection(currentProduct);
                            // KIỂM TRA TRẠNG THÁI YÊU THÍCH SAU KHI LOAD SẢN PHẨM
                            checkFavoriteStatus();
                        } else {
                            Toast.makeText(this, "Lỗi: Dữ liệu sản phẩm không hợp lệ.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Sản phẩm không tồn tại.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải chi tiết sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void displayProductDetails(Product product) {
        if (product == null) return;

        Glide.with(this)
                .load(product.getImageUrl() != null ? product.getImageUrl() : "")
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .into(ivProductImage);

        tvProductName.setText(product.getName() != null ? product.getName() : "Sản phẩm không tên");
        tvProductDescription.setText(product.getDescription() != null ? product.getDescription() : "Không có mô tả chi tiết.");

        if (product.getRating() > 0) {
            tvProductRating.setText(String.format("%.1f", product.getRating()));
            tvProductRating.setVisibility(View.VISIBLE);
        } else {
            tvProductRating.setVisibility(View.GONE);
        }

        double originalPrice = product.getPrice();
        double discountPrice = product.getDiscountPrice();

        if (discountPrice > 0 && discountPrice < originalPrice) {
            // Có giảm giá
            tvProductPrice.setText(String.format("%,.0f VNĐ", discountPrice));
            tvProductOriginalPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            tvProductOriginalPrice.setPaintFlags(tvProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvProductOriginalPrice.setVisibility(View.VISIBLE);
        } else {
            // Không giảm giá
            tvProductPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            tvProductOriginalPrice.setVisibility(View.GONE);
        }
    }

    private void setupSizeSelection(Product product) {
        if (product.getSizes() == null || product.getSizes().isEmpty()) {
            tvSizeLabel.setVisibility(View.GONE);
            llSizesContainer.setVisibility(View.GONE);
            return;
        }

        tvSizeLabel.setVisibility(View.VISIBLE);
        llSizesContainer.setVisibility(View.VISIBLE);
        llSizesContainer.removeAllViews();

        for (String size : product.getSizes()) {
            Button sizeButton = new Button(this);
            sizeButton.setText(size);

            sizeButton.setTextSize(14f);

            // Giả định R.color.size_button_text_color và R.drawable.size_button_selector đã tồn tại
            sizeButton.setTextColor(getResources().getColorStateList(R.color.size_button_text_color));
            sizeButton.setBackgroundResource(R.drawable.size_button_selector);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 16, 0);
            sizeButton.setLayoutParams(params);

            sizeButton.setOnClickListener(v -> handleSizeClick(sizeButton, size));
            llSizesContainer.addView(sizeButton);
        }

        // Mặc định chọn size đầu tiên nếu có size
        if (!product.getSizes().isEmpty()) {
            handleSizeClick((Button) llSizesContainer.getChildAt(0), product.getSizes().get(0));
        }
    }

    private void handleSizeClick(Button clickedButton, String size) {
        for (int i = 0; i < llSizesContainer.getChildCount(); i++) {
            View child = llSizesContainer.getChildAt(i);
            if (child instanceof Button) {
                child.setSelected(false);
            }
        }
        clickedButton.setSelected(true);
        selectedSize = size;
    }


    private void addToCart() {
        if (currentProduct == null) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (llSizesContainer.getVisibility() == View.VISIBLE && selectedSize == null) {
            Toast.makeText(this, "Vui lòng chọn kích cỡ.", Toast.LENGTH_SHORT).show();
            return;
        }


        String finalSize = selectedSize != null ? selectedSize : "N/A";

        // SỬ DỤNG CARTMANAGER
        try {
            CartManager.getInstance().addItem(currentProduct, 1, finalSize);

            Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng: " + finalSize, Toast.LENGTH_SHORT).show();

            // Tùy chọn: Chuyển sang CartActivity sau khi thêm thành công (để người dùng thấy)
            startActivity(new Intent(this, CartActivity.class));

        } catch (Exception e) {
            // Đây là một catch chung, bạn nên kiểm tra logcat để biết chi tiết lỗi
            Toast.makeText(ProductDetailActivity.this, "Lỗi thêm vào giỏ hàng!", Toast.LENGTH_LONG).show();
        }
    }
}