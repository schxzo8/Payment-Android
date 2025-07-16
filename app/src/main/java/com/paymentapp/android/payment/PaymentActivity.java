package com.paymentapp.android.payment;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.paymentapp.android.databinding.ActivityPaymentBinding;
import com.paymentapp.android.model.PaymentRequest;
import com.paymentapp.android.model.PaymentResponse;
import com.paymentapp.android.api.ApiClient;
import com.paymentapp.android.api.ApiInterface;
import com.paymentapp.android.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private ActivityPaymentBinding binding;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = new SharedPrefManager(this);

        binding.btnProcessPayment.setOnClickListener(v -> {
            String merchantId = binding.etMerchantId.getText().toString().trim();
            String amount = binding.etAmount.getText().toString().trim();
            String cardNumber = binding.etCardNumber.getText().toString().trim();
            String expiryDate = binding.etExpiryDate.getText().toString().trim();

            if (validateInputs(merchantId, amount, cardNumber, expiryDate)) {
                processPayment(merchantId, amount, cardNumber, expiryDate);
            }
        });
    }

    private boolean validateInputs(String merchantId, String amount,
                                   String cardNumber, String expiryDate) {
        // Implement validation logic
        return true;
    }

    private void processPayment(String merchantId, String amount,
                                String cardNumber, String expiryDate) {
        PaymentRequest request = new PaymentRequest(
                merchantId,
                Double.parseDouble(amount),
                cardNumber,
                expiryDate
        );

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentResponse> call = apiService.processPayment(
                "Bearer " + sharedPrefManager.getToken(),
                request
        );

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showPaymentResult(response.body());
                } else {
                    Toast.makeText(PaymentActivity.this, "Payment failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPaymentResult(PaymentResponse response) {
        // Show payment result to user
    }
}