package com.paymentapp.android;

import android.app.Application;

public class PaymentApp extends Application {
    private static PaymentApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static PaymentApp getInstance() {
        return instance;
    }
}