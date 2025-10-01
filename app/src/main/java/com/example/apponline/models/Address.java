package com.example.apponline.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class Address implements Parcelable {
    // 🚨 TRƯỜNG MỚI: Dùng để lưu trữ Document ID từ Firestore
    private String addressId;

    private String name;
    private String phoneNumber;
    private String detailAddress; // Tên đường, số nhà, tòa nhà
    private String cityState;     // Tỉnh/Thành phố, Quận/Huyện, Phường/Xã
    private boolean isDefault;
    private boolean isShippingAddress;
    private String addressType;

    // 1. CONSTRUCTOR KHÔNG THAM SỐ (BẮT BUỘC CHO FIRESTORE)
    public Address() {
        // Constructor rỗng cần thiết cho việc deserialization của Firebase Firestore
    }

    // 2. CONSTRUCTOR ĐẦY ĐỦ (Dùng để tạo đối tượng mới)
    public Address(String name, String phoneNumber, String detailAddress, String cityState, boolean isDefault, boolean isShippingAddress, String addressType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.detailAddress = detailAddress;
        this.cityState = cityState;
        this.isDefault = isDefault;
        this.isShippingAddress = isShippingAddress;
        this.addressType = addressType;
    }

    // =========================================================================
    // PARCELABLE IMPLEMENTATION
    // =========================================================================

    // 3. CONSTRUCTOR ĐỌC TỪ PARCEL
    protected Address(Parcel in) {
        // 🚨 ĐỌC addressId ĐẦU TIÊN
        addressId = in.readString();

        name = in.readString();
        phoneNumber = in.readString();
        detailAddress = in.readString();
        cityState = in.readString();
        isDefault = in.readByte() != 0;
        isShippingAddress = in.readByte() != 0;
        addressType = in.readString();
    }

    // 4. CREATOR (Bắt buộc)
    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    // 5. GHI DỮ LIỆU VÀO PARCEL
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 🚨 GHI addressId ĐẦU TIÊN
        dest.writeString(addressId);

        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(detailAddress);
        dest.writeString(cityState);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeByte((byte) (isShippingAddress ? 1 : 0));
        dest.writeString(addressType);
    }

    // 6. DESCRIBE CONTENTS (Thường trả về 0)
    @Override
    public int describeContents() {
        return 0;
    }

    // =========================================================================
    // GETTERS VÀ SETTERS CHO addressId (Cần thiết cho logic xóa)
    // =========================================================================

    // 🚨 Thêm @Exclude vào getter để Firestore bỏ qua trường này khi ghi
    @Exclude
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }


    // =========================================================================
    // GETTERS VÀ SETTERS CŨ
    // =========================================================================

    // --- GETTERS ---
    public String getDetailAddress() { return detailAddress; }
    public boolean isDefault() { return isDefault; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCityState() { return cityState; }
    public boolean isShippingAddress() { return isShippingAddress; }
    public String getAddressType() { return addressType; }


    // --- SETTERS ---
    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    public void setCityState(String cityState) { this.cityState = cityState; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
    public void setShippingAddress(boolean shippingAddress) { isShippingAddress = shippingAddress; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
}