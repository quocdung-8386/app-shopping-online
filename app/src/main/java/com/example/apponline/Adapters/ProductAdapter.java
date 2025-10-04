package com.example.apponline.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar; // Import RatingBar
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


    private final int layoutResId;


    public ProductAdapter(Context context, List<Product> productList, int layoutResId) {
        this.context = context;
        this.productList = productList;
        this.layoutResId = layoutResId;
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this(context, productList, R.layout.item_product_small);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🟢 SỬA: Dùng layoutResId để inflate layout đã chọn
        View view = LayoutInflater.from(context).inflate(layoutResId, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // 🟢 SỬA ID: product_name trong layout small
        holder.tvProductName.setText(product.getName());

        // 🟢 SỬA ID: product_image trong layout small
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.product_placeholder)
                .into(holder.ivProductImage);

        double originalPrice = product.getPrice();
        double discountPrice = product.getDiscountPrice();
        float rating = (float) product.getRating();

        if (discountPrice > 0 && discountPrice < originalPrice) {
            holder.tvCurrentPrice.setText(String.format("%,.0f VNĐ", discountPrice));
            holder.tvOriginalPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            holder.tvOriginalPrice.setVisibility(View.VISIBLE);
            holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        } else {
            holder.tvCurrentPrice.setText(String.format("%,.0f VNĐ", originalPrice));
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }

        if (holder.ratingBar != null) {
            holder.ratingBar.setRating(rating);
        }
        if (holder.tvRating != null) {
            if (product.getRating() > 0) {
                holder.tvRating.setText(String.valueOf(product.getRating()));
                holder.tvRating.setVisibility(View.VISIBLE);
            } else {
                holder.tvRating.setVisibility(View.GONE);
            }
        }

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

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvOriginalPrice;
        TextView tvCurrentPrice;

        RatingBar ratingBar;

        TextView tvRating;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProductImage = itemView.findViewById(R.id.product_image);
            tvProductName = itemView.findViewById(R.id.product_name);
            tvOriginalPrice = itemView.findViewById(R.id.product_price_original);
            tvCurrentPrice = itemView.findViewById(R.id.product_price_sale);

            ratingBar = itemView.findViewById(R.id.product_rating_bar);

        }
    }
}