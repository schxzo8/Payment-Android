package com.paymentapp.android.api;

import com.paymentapp.android.model.LoginRequest;
import com.paymentapp.android.model.LoginResponse;
import com.paymentapp.android.model.Merchant;
import com.paymentapp.android.model.PaymentRequest;
import com.paymentapp.android.model.PaymentResponse;
import com.paymentapp.android.model.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiInterface {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("merchants")
    Call<List<Merchant>> getMerchants(@Header("Authorization") String token);

    @POST("merchants")
    Call<Merchant> createMerchant(
            @Header("Authorization") String token,
            @Body Merchant merchant);

    @POST("payments")
    Call<PaymentResponse> processPayment(
            @Header("Authorization") String token,
            @Body PaymentRequest paymentRequest);

    @GET("transactions")
    Call<List<Transaction>> getTransactions(@Header("Authorization") String token);

    @GET("transactions/merchant/{merchantId}")
    Call<List<Transaction>> getTransactionsByMerchant(
            @Header("Authorization") String token,
            @Path("merchantId") String merchantId);
}