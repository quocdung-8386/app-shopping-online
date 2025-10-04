package com.example.apponline.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

public class Address implements Parcelable {
    private String addressId;

    private String name;
    private String phoneNumber;
    private String detailAddress;
    private String cityState;
    private boolean isDefault;
    private boolean isShippingAddress;
    private String addressType;

    public Address() {
    }

    public Address(String name, String phoneNumber, String detailAddress, String cityState, boolean isDefault, boolean isShippingAddress, String addressType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.detailAddress = detailAddress;
        this.cityState = cityState;
        this.isDefault = isDefault;
        this.isShippingAddress = isShippingAddress;
        this.addressType = addressType;
    }

    protected Address(Parcel in) {
        addressId = in.readString();

        name = in.readString();
        phoneNumber = in.readString();
        detailAddress = in.readString();
        cityState = in.readString();
        isDefault = in.readByte() != 0;
        isShippingAddress = in.readByte() != 0;
        addressType = in.readString();
    }

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
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressId);

        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeString(detailAddress);
        dest.writeString(cityState);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeByte((byte) (isShippingAddress ? 1 : 0));
        dest.writeString(addressType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Exclude
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getDetailAddress() { return detailAddress; }
    public boolean isDefault() { return isDefault; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCityState() { return cityState; }
    public boolean isShippingAddress() { return isShippingAddress; }
    public String getAddressType() { return addressType; }

    public void setName(String name) { this.name = name; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    public void setCityState(String cityState) { this.cityState = cityState; }
    public void setDefault(boolean aDefault) { isDefault = aDefault; }
    public void setShippingAddress(boolean shippingAddress) { isShippingAddress = shippingAddress; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
}