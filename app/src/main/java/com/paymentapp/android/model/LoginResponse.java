package com.paymentapp.android.model;

public class LoginResponse {
    private String accessToken;
    private String tokenType;

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType != null ? tokenType : "Bearer";
    }
}