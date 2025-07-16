package com.paymentapp.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPrefManager {
    private static final String PREFS_NAME = "payment_app_prefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";

    private SharedPreferences sharedPreferences;

    public SharedPrefManager(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    PREFS_NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Fallback to regular SharedPreferences (less secure)
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_JWT_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }
}