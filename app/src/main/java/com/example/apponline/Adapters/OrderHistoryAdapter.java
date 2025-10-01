package com.example.apponline.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.apponline.R;
import com.example.apponline.models.Order;
import com.example.apponline.models.OrderItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    // 🚨 BƯỚC 1: KHAI BÁO INTERFACE
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final Context context;
    private final List<Order> orderList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    // 🚨 BƯỚC 2: KHAI BÁO THUỘC TÍNH LISTENER
    private final OnOrderClickListener listener;

    // 🚨 BƯỚC 3: SỬA CONSTRUCTOR ĐỂ NHẬN LISTENER
    public OrderHistoryAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener; // Gán listener
    }

    // Lưu ý: Nếu bạn vẫn còn code cũ gọi constructor 2 tham số, bạn nên loại bỏ nó hoặc thêm constructor sau:
    // public OrderHistoryAdapter(Context context, List<Order> orderList) {
    //     this(context, orderList, null);
    // }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // 1. Order ID
        holder.tvOrderId.setText("Mã: #" + (order.getOrderId() != null ? order.getOrderId() : "N/A"));
        // 2. Order Date (giữ nguyên)
        try {
            String date = dateFormat.format(new Date(order.getTimestamp()));
            holder.tvOrderDate.setText("Ngày đặt: " + date);
        } catch (Exception e) {
            holder.tvOrderDate.setText("Ngày đặt: Lỗi định dạng");
        }
        // 3. Order Total (giữ nguyên)
        String totalText = String.format("Tổng: %,.0f VNĐ", order.getTotalAmount());
        holder.tvOrderTotal.setText(totalText);
        // 4. Order Status (giữ nguyên)
        holder.tvOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Không xác định");

        // 5. Order Items & Thumbnail (giữ nguyên)
        if (!order.getItems().isEmpty()) {
            holder.tvOrderItemsCount.setText(order.getItems().size() + " Sản phẩm");
            OrderItem firstItem = order.getItems().get(0);
            String imageUrl = firstItem.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context).load(imageUrl).placeholder(R.drawable.product_placeholder).error(R.drawable.product_placeholder).into(holder.ivOrderProductThumbnail);
            } else {
                holder.ivOrderProductThumbnail.setImageResource(R.drawable.product_placeholder);
            }
        } else {
            holder.tvOrderItemsCount.setText("0 Sản phẩm");
            holder.ivOrderProductThumbnail.setImageResource(R.drawable.product_placeholder);
        }

        // 🚨 BƯỚC 4: THÊM LISTENER CHO ITEM VIEW
        holder.itemView.setOnClickListener(v -> {
            // Kiểm tra và gọi phương thức click
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    /**
     * Hàm tiện ích để cập nhật dữ liệu từ Activity
     * @param newOrders Danh sách đơn hàng mới
     */
    public void updateData(List<Order> newOrders) {
        this.orderList.clear();
        this.orderList.addAll(newOrders);
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvOrderStatus, tvOrderItemsCount;
        ImageView ivOrderProductThumbnail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderHistoryId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderHistoryDate);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderHistoryTotal);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderHistoryStatus);
            tvOrderItemsCount = itemView.findViewById(R.id.tvOrderHistoryItemsCount);
            ivOrderProductThumbnail = itemView.findViewById(R.id.ivOrderProductThumbnail);
        }
    }
}