package com.example.apponline.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint; // Import Paint để gạch ngang giá
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.apponline.ProductDetailActivity;
import com.example.apponline.R;
import com.example.apponline.models.Product;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CHỈNH SỬA: Đảm bảo ánh xạ đúng layout bạn muốn (item_product_grid.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // 1. Tên sản phẩm
        holder.tvProductName.setText(product.getName());

        // 2. Tải hình ảnh
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.product_placeholder) // Đảm bảo resource này tồn tại
                .into(holder.ivProductImage);

        // 3. Xử lý giá và giảm giá
        double originalPrice = product.getPrice();
        double discountPrice = product.getDiscountPrice();

        if (discountPrice > 0 && discountPrice < originalPrice) {
            // Có giảm giá
            holder.tvCurrentPrice.setText(String.format("%,.0f VNĐ", discountPrice));

            // Hiển thị giá gốc và gạch ngang
            holder.tvOriginalPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            holder.tvOriginalPrice.setVisibility(View.VISIBLE);
            // Thêm hiệu ứng gạch ngang (Strikethrough)
            holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            // Không giảm giá
            holder.tvCurrentPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }

        // 4. Hiển thị Rating (Nếu có)
        if (product.getRating() > 0) {
            holder.tvRating.setText(String.valueOf(product.getRating()));
            holder.tvRating.setVisibility(View.VISIBLE);
        } else {
            holder.tvRating.setVisibility(View.GONE);
        }

        // 5. Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder đã cập nhật để khớp với item_product_grid.xml
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvOriginalPrice;
        TextView tvCurrentPrice;
        TextView tvRating;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ item_product_grid.xml
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvCurrentPrice = itemView.findViewById(R.id.tvCurrentPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}