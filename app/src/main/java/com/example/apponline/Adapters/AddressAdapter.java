package com.example.apponline.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // 👈 CẦN IMPORT ImageButton
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apponline.R;
import com.example.apponline.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    // === 1. ĐỊNH NGHĨA INTERFACE CALLBACK MỚI ===
    public interface OnAddressSelectedListener {
        void onAddressSelected(Address address);

        // 🚨 THÊM PHƯƠNG THỨC XỬ LÝ SỰ KIỆN XÓA
        void onAddressDeleted(Address address);
    }

    private final Context context;
    private final List<Address> addressList;
    private final OnAddressSelectedListener listener;
    private int selectedPosition = 0;

    // === 2. CONSTRUCTOR (GIỮ NGUYÊN) ===
    public AddressAdapter(Context context, List<Address> addressList, OnAddressSelectedListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Address address = addressList.get(position);


        holder.namePhoneText.setText(address.getName() + " | " + address.getPhoneNumber());
        // Giả định getCityState() là Phường/Quận/TP
        holder.detailAddressText.setText(address.getDetailAddress() + ", " + address.getCityState());


        holder.radioButton.setChecked(position == selectedPosition);
        holder.defaultTag.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);
        holder.shippingTag.setVisibility(address.isShippingAddress() ? View.VISIBLE : View.GONE);

        // 4. Xử lý sự kiện click để CHỌN địa chỉ
        holder.itemView.setOnClickListener(v -> {
            int previousSelectedPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onAddressSelected(address);
            }
        });

        // 🚨 5. XỬ LÝ SỰ KIỆN CLICK để XOÁ địa chỉ
        holder.btnDeleteAddress.setOnClickListener(v -> {
            // Gọi callback để thông báo cho Activity/Fragment rằng địa chỉ này cần được xóa
            if (listener != null) {
                listener.onAddressDeleted(address);
            }
            // Lưu ý: Logic xóa khỏi danh sách (addressList.remove) và notifyDataSetChanged()
            // nên được thực hiện TRONG Activity/Fragment sau khi xác nhận xóa thành công.
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    // === 6. PHƯƠNG THỨC CẬP NHẬT VỊ TRÍ ĐƯỢC CHỌN (Hữu ích khi xóa) ===
    public void setSelectedPosition(int position) {
        if (position >= 0 && position < addressList.size()) {
            this.selectedPosition = position;
        } else {
            // Xử lý trường hợp danh sách rỗng hoặc position không hợp lệ
            this.selectedPosition = -1;
        }
    }


    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView namePhoneText, detailAddressText, defaultTag, shippingTag;
        RadioButton radioButton;
        // 🚨 THÊM ImageButton
        ImageButton btnDeleteAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            namePhoneText = itemView.findViewById(R.id.name_phone_text);
            detailAddressText = itemView.findViewById(R.id.detail_address_text);
            defaultTag = itemView.findViewById(R.id.default_tag);
            shippingTag = itemView.findViewById(R.id.shipping_tag);
            radioButton = itemView.findViewById(R.id.address_radio_button);

            // 🚨 ÁNH XẠ NÚT XOÁ
            btnDeleteAddress = itemView.findViewById(R.id.btnDeleteAddress);

            radioButton.setClickable(false);
            radioButton.setFocusable(false);
        }
    }
}