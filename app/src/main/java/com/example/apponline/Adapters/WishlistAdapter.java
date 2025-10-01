package com.example.apponline.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.apponline.ProductDetailActivity; // Cần thiết để chuyển sang trang chi tiết
import com.example.apponline.R;
import com.example.apponline.firebase.WishlistManager;
import com.example.apponline.models.Product;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final Context context;
    private List<Product> wishlistItems;

    public WishlistAdapter(Context context, List<Product> wishlistItems) {
        this.context = context;
        this.wishlistItems = wishlistItems;
    }

    /**
     * Cập nhật dữ liệu và thông báo cho RecyclerView làm mới.
     */
    public void updateData(List<Product> newItems) {
        this.wishlistItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist_product, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Product product = wishlistItems.get(position);

        // Load ảnh
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.product_placeholder)
                .error(R.drawable.product_placeholder)
                .into(holder.ivProductImage);

        // Hiển thị tên
        holder.tvProductName.setText(product.getName());

        // Hiển thị giá và giá gốc
        double originalPrice = product.getPrice();
        double discountPrice = product.getDiscountPrice();

        if (discountPrice > 0 && discountPrice < originalPrice) {
            // Có giảm giá
            holder.tvProductPrice.setText(String.format("%,.0f VNĐ", discountPrice));
            holder.tvProductOriginalPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            holder.tvProductOriginalPrice.setPaintFlags(holder.tvProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvProductOriginalPrice.setVisibility(View.VISIBLE);
        } else {
            // Không giảm giá
            holder.tvProductPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            holder.tvProductOriginalPrice.setVisibility(View.GONE);
        }

        // Xử lý sự kiện click vào item (Chuyển đến trang chi tiết)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });

        // Xử lý sự kiện click nút XÓA khỏi danh sách yêu thích
        holder.btnRemoveFromWishlist.setOnClickListener(v -> {
            removeItem(product);
        });

        // Xử lý nút Thêm vào Giỏ hàng
        holder.btnAddToCart.setOnClickListener(v -> {
            // Thường chuyển sang trang chi tiết để chọn size/số lượng
            Toast.makeText(context, "Chuyển đến trang chi tiết để chọn size/số lượng...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });
    }

    private void removeItem(Product productToRemove) {
        // 1. Gọi WishlistManager để xử lý việc xóa dữ liệu (đồng bộ với Firestore)
        WishlistManager.getInstance().removeProductFromWishlist(productToRemove);

        // 2. Cập nhật dữ liệu cục bộ và thông báo cho Adapter
        wishlistItems = WishlistManager.getInstance().getWishlistItems();
        notifyDataSetChanged();

        Toast.makeText(context, "Đã xóa " + productToRemove.getName() + " khỏi yêu thích.", Toast.LENGTH_SHORT).show();

        // Tùy chọn: Nếu danh sách trống, bạn có thể gửi sự kiện đến Activity để hiển thị màn hình trống
        if (wishlistItems.isEmpty() && context instanceof OnWishlistChangeListener) {
            ((OnWishlistChangeListener) context).onWishlistChanged(0);
        }
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    // ViewHolder ánh xạ các View từ item_wishlist_product.xml
    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvProductOriginalPrice;
        ImageButton btnRemoveFromWishlist;
        Button btnAddToCart;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductOriginalPrice = itemView.findViewById(R.id.tvProductOriginalPrice);
            btnRemoveFromWishlist = itemView.findViewById(R.id.btnRemoveFromWishlist);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCard);
        }
    }

    /**
     * Interface để Activity có thể nhận thông báo khi có thay đổi quan trọng
     * Ví dụ: Khi danh sách trống
     */
    public interface OnWishlistChangeListener {
        void onWishlistChanged(int newCount);
    }
}