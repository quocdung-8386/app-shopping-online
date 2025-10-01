package com.example.apponline.Adapters;

import android.content.Context;
import android.content.Intent; // Import Intent
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Vẫn có thể dùng cho mục đích debugging

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apponline.R;
import com.example.apponline.ProductListActivity; // Import Activity đích của bạn
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<String> categoryList;
    // KHÔNG CẦN private final CategoryClickListener listener; NỮA

    // Constructor đã sửa đổi: Loại bỏ listener
    public CategoryAdapter(Context context, List<String> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ với layout item_category_icon.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_icon, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryName = categoryList.get(position);
        holder.categoryName.setText(categoryName);

        // TODO: Thiết lập ImageView (categoryIcon) dựa trên tên danh mục 

        // Xử lý sự kiện click TRỰC TIẾP
        holder.itemView.setOnClickListener(v -> {
            // Hiển thị Toast (Tùy chọn, chỉ để kiểm tra)
            Toast.makeText(context, "Mở danh sách cho: " + categoryName, Toast.LENGTH_SHORT).show();

            // Bắt đầu Activity mới và truyền dữ liệu categoryName
            Intent intent = new Intent(context, ProductListActivity.class); // Thay ProductListActivity bằng Activity đích của bạn
            intent.putExtra("CATEGORY_NAME", categoryName);

            // Thêm FLAG_ACTIVITY_NEW_TASK nếu context là Application Context 
            // Nếu context là Activity Context (như trong trường hợp này), thì không bắt buộc
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 

            context.startActivity(intent); // Mở Activity
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryIcon;
        TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.category_icon);
            categoryName = itemView.findViewById(R.id.category_name);
        }
    }
}