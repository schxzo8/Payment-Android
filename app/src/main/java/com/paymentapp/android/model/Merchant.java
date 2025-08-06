package com.paymentapp.android.model;

import com.google.gson.annotations.SerializedName;

public class Merchant {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("business_type")
    private String businessType;

    @SerializedName("registration_date")
    private long registrationDate;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("icon_url")
    private String iconUrl;

    // Default constructor
    public Merchant() {}

    // Constructor
    public Merchant(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.isActive = "active".equalsIgnoreCase(status);
        this.registrationDate = System.currentTimeMillis();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getBusinessType() {
        return businessType;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
        this.isActive = "active".equalsIgnoreCase(status);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.status = active ? "Active" : "Inactive";
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    // Utility methods
    public String getFormattedId() {
        return "ID: " + (id != null ? id : "Unknown");
    }

    public String getDisplayName() {
        return name != null ? name : "Unknown Merchant";
    }

    public boolean isStatusActive() {
        return "active".equalsIgnoreCase(status) || isActive;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Merchant merchant = (Merchant) obj;
        return id != null ? id.equals(merchant.id) : merchant.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    public String getMerchantName() {
        return name;
    }

    public String getMerchantId(){
        return id;
    }
}