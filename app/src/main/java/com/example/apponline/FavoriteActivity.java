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
        rvWishlist = findViewById(R.id.rvWishlist);
        tvEmptyWishlist = findViewById(R.id.tvEmptyWishlist);
        btnBack = findViewById(R.id.btnBack);
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWishlist();
    }

    private void loadWishlist() {
        WishlistManager.getInstance().loadWishlistFromFirestore(new WishlistLoadCallback() {
            @Override
            public void onWishlistLoaded() {
                List<Product> items = WishlistManager.getInstance().getWishlistItems();

                if (items.isEmpty()) {
                    rvWishlist.setVisibility(View.GONE);
                    tvEmptyWishlist.setVisibility(View.VISIBLE);
                } else {

                    rvWishlist.setVisibility(View.VISIBLE);
                    tvEmptyWishlist.setVisibility(View.GONE);

                    if (adapter == null) {
                        adapter = new WishlistAdapter(FavoriteActivity.this, items);
                        rvWishlist.setAdapter(adapter);
                    } else {
                        adapter.updateData(items);
                    }
                }
            }
        });
    }
    @Override
    public void onWishlistChanged(int newCount) {
        if (newCount == 0) {
            rvWishlist.setVisibility(View.GONE);
            tvEmptyWishlist.setVisibility(View.VISIBLE);
        }
    }
}