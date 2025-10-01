package com.example.apponline;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apponline.Adapters.WishlistAdapter;
import com.example.apponline.firebase.WishlistManager;
import com.example.apponline.firebase.WishlistManager.WishlistLoadCallback; // IMPORT CALLBACK NÀY
import com.example.apponline.models.Product;
import java.util.List;

/**
 * Activity hiển thị danh sách sản phẩm yêu thích.
 * Triển khai OnWishlistChangeListener để nhận thông báo khi danh sách trống/thay đổi.
 */
public class FavoriteActivity extends AppCompatActivity
        implements WishlistAdapter.OnWishlistChangeListener {

    private RecyclerView rvWishlist;
    private TextView tvEmptyWishlist;
    private ImageButton btnBack;
    private WishlistAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        // Ánh xạ Views
        rvWishlist = findViewById(R.id.rvWishlist);
        tvEmptyWishlist = findViewById(R.id.tvEmptyWishlist);
        btnBack = findViewById(R.id.btnBack);

        // Thiết lập RecyclerView
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải và hiển thị danh sách mỗi khi Activity trở lại foreground
        loadWishlist(); // Bây giờ sẽ sử dụng phương thức tải bất đồng bộ
    }

    private void loadWishlist() {
        // Gọi hàm loadWishlistFromFirestore với callback
        WishlistManager.getInstance().loadWishlistFromFirestore(new WishlistLoadCallback() {
            @Override
            public void onWishlistLoaded() {
                // HÀM NÀY CHẠY SAU KHI DỮ LIỆU MỚI NHẤT TỪ FIRESTORE ĐÃ TẢI XONG
                List<Product> items = WishlistManager.getInstance().getWishlistItems();

                if (items.isEmpty()) {
                    // Danh sách trống
                    rvWishlist.setVisibility(View.GONE);
                    tvEmptyWishlist.setVisibility(View.VISIBLE);
                } else {
                    // Có sản phẩm
                    rvWishlist.setVisibility(View.VISIBLE);
                    tvEmptyWishlist.setVisibility(View.GONE);

                    if (adapter == null) {
                        // Khởi tạo Adapter lần đầu, truyền 'this' làm listener
                        adapter = new WishlistAdapter(FavoriteActivity.this, items);
                        rvWishlist.setAdapter(adapter);
                    } else {
                        // Cập nhật dữ liệu cho Adapter đã tồn tại
                        adapter.updateData(items);
                    }
                }
            }
        });
    }

    // Xử lý khi Adapter thông báo danh sách trống/thay đổi
    @Override
    public void onWishlistChanged(int newCount) {
        if (newCount == 0) {
            rvWishlist.setVisibility(View.GONE);
            tvEmptyWishlist.setVisibility(View.VISIBLE);
        }
    }
}