package com.example.apponline.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apponline.R; // Đảm bảo đúng package để truy cập layout và drawable

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private static final String TAG = "BannerAdapter";
    private final Context context;
    private final List<String> imageUrls; // Danh sách các URL ảnh từ Firebase

    public BannerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Sử dụng layout item_banner_slide.xml
        // Bạn cần tạo file item_banner_slide.xml chứa ImageView có ID banner_image
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner_slide, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Debug: Kiểm tra URL có được truyền vào Adapter không
        Log.d(TAG, "Binding banner URL: " + imageUrl);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Tải ảnh từ URL vào ImageView bằng Glide
            Glide.with(context)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.banner_placeholder) // Ảnh hiển thị khi đang tải
                    .error(R.drawable.banner_placeholder)      // Ảnh hiển thị nếu tải thất bại
                    .into(holder.imageView);
        } else {
            // Hiển thị ảnh placeholder nếu URL không hợp lệ
            holder.imageView.setImageResource(R.drawable.banner_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            // TODO: Xử lý sự kiện click trên banner
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    // ViewHolder class
    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            // 🚨 Ánh xạ ID của ImageView. ID này phải tồn tại trong item_banner_slide.xml
            imageView = itemView.findViewById(R.id.banner_image);
        }
    }
}