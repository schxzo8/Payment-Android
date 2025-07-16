package com.paymentapp.android.utils;

public class Constants {
    public static final String BASE_URL = "http://10.0.2.2:8080/api/";
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER = "Bearer ";

    // SharedPreferences keys
    public static final String PREF_TOKEN = "jwt_token";

    // Intent extras
    public static final String EXTRA_MERCHANT_ID = "merchant_id";
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";
}