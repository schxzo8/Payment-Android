package com.paymentapp.android.payment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.paymentapp.android.databinding.ActivityPaymentBinding;
import com.paymentapp.android.model.PaymentRequest;
import com.paymentapp.android.model.PaymentResponse;
import com.paymentapp.android.api.ApiClient;
import com.paymentapp.android.api.ApiInterface;
import com.paymentapp.android.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.regex.Pattern;

public class PaymentActivity extends AppCompatActivity {
    private static final String TAG = "PaymentActivity";
    private ActivityPaymentBinding binding;
    private SharedPrefManager sharedPrefManager;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = new SharedPrefManager(this);
        setupUI();
    }

    private void setupUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Payment");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        binding.btnProcessPayment.setOnClickListener(v -> {
            if (!isProcessing) {
                handlePaymentClick();
            }
        });

        // Format card number input with spaces
        binding.etCardNumber.addTextChangedListener(new CreditCardTextWatcher());

        // Format expiry date input
        binding.etExpiryDate.addTextChangedListener(new ExpiryDateTextWatcher());
    }

    private void handlePaymentClick() {
        String merchantId = binding.etMerchantId.getText().toString().trim();
        String amount = binding.etAmount.getText().toString().trim();
        String cardNumber = binding.etCardNumber.getText().toString().trim().replaceAll("\\s", "");
        String expiryDate = binding.etExpiryDate.getText().toString().trim();
        String cvv = binding.etCvv.getText().toString().trim();

        if (validateInputs(merchantId, amount, cardNumber, expiryDate, cvv)) {
            showPaymentConfirmation(merchantId, amount, cardNumber, expiryDate, cvv);
        }
    }

    private boolean validateInputs(String merchantId, String amount, String cardNumber, String expiryDate, String cvv) {
        // Reset previous errors
        binding.etMerchantId.setError(null);
        binding.etAmount.setError(null);
        binding.etCardNumber.setError(null);
        binding.etExpiryDate.setError(null);
        binding.etCvv.setError(null);

        boolean isValid = true;

        // Validate Merchant ID
        if (TextUtils.isEmpty(merchantId)) {
            binding.etMerchantId.setError("Merchant ID is required");
            binding.etMerchantId.requestFocus();
            isValid = false;
        } else if (merchantId.length() < 3) {
            binding.etMerchantId.setError("Merchant ID must be at least 3 characters");
            binding.etMerchantId.requestFocus();
            isValid = false;
        }

        // Validate Amount
        if (TextUtils.isEmpty(amount)) {
            binding.etAmount.setError("Amount is required");
            if (isValid) binding.etAmount.requestFocus();
            isValid = false;
        } else {
            try {
                double amountValue = Double.parseDouble(amount);
                if (amountValue <= 0) {
                    binding.etAmount.setError("Amount must be greater than 0");
                    if (isValid) binding.etAmount.requestFocus();
                    isValid = false;
                } else if (amountValue > 999999.99) {
                    binding.etAmount.setError("Amount is too large");
                    if (isValid) binding.etAmount.requestFocus();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                binding.etAmount.setError("Invalid amount format");
                if (isValid) binding.etAmount.requestFocus();
                isValid = false;
            }
        }

        // Validate Card Number
        if (TextUtils.isEmpty(cardNumber)) {
            binding.etCardNumber.setError("Card number is required");
            if (isValid) binding.etCardNumber.requestFocus();
            isValid = false;
        } else if (!isValidCardNumber(cardNumber)) {
            binding.etCardNumber.setError("Invalid card number");
            if (isValid) binding.etCardNumber.requestFocus();
            isValid = false;
        }

        // Validate Expiry Date
        if (TextUtils.isEmpty(expiryDate)) {
            binding.etExpiryDate.setError("Expiry date is required");
            if (isValid) binding.etExpiryDate.requestFocus();
            isValid = false;
        } else if (!isValidExpiryDate(expiryDate)) {
            binding.etExpiryDate.setError("Invalid expiry date (MM/YY format)");
            if (isValid) binding.etExpiryDate.requestFocus();
            isValid = false;
        } else if (isExpiredCard(expiryDate)) {
            binding.etExpiryDate.setError("Card has expired");
            if (isValid) binding.etExpiryDate.requestFocus();
            isValid = false;
        }

        // Validate CVV
        if (TextUtils.isEmpty(cvv)) {
            binding.etCvv.setError("CVV is required");
            if (isValid) binding.etCvv.requestFocus();
            isValid = false;
        } else if (cvv.length() < 3 || cvv.length() > 4) {
            binding.etCvv.setError("CVV must be 3 or 4 digits");
            if (isValid) binding.etCvv.requestFocus();
            isValid = false;
        } else if (!cvv.matches("\\d+")) {
            binding.etCvv.setError("CVV must contain only numbers");
            if (isValid) binding.etCvv.requestFocus();
            isValid = false;
        }

        return isValid;
    }

    private boolean isValidCardNumber(String cardNumber) {
        // Remove any spaces or dashes
        cardNumber = cardNumber.replaceAll("[\\s-]", "");

        // Check if it contains only digits and has valid length
        if (!cardNumber.matches("\\d+") || cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }

        // Luhn algorithm for card number validation
        return luhnCheck(cardNumber);
    }

    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    private boolean isValidExpiryDate(String expiryDate) {
        // Check format MM/YY
        Pattern pattern = Pattern.compile("^(0[1-9]|1[0-2])/([0-9]{2})$");
        return pattern.matcher(expiryDate).matches();
    }

    private boolean isExpiredCard(String expiryDate) {
        try {
            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = Integer.parseInt(parts[1]) + 2000; // Convert YY to YYYY

            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
            int currentYear = calendar.get(java.util.Calendar.YEAR);

            if (year < currentYear) {
                return true;
            } else if (year == currentYear && month < currentMonth) {
                return true;
            }

            return false;
        } catch (Exception e) {
            return true; // Treat parsing errors as expired
        }
    }

    private void showPaymentConfirmation(String merchantId, String amount, String cardNumber, String expiryDate, String cvv) {
        String maskedCardNumber = maskCardNumber(cardNumber);
        String message = String.format(
                "Confirm Payment Details:\n\n" +
                        "Merchant: %s\n" +
                        "Amount: $%s\n" +
                        "Card: %s\n" +
                        "Expiry: %s\n\n" +
                        "Proceed with payment?",
                merchantId, amount, maskedCardNumber, expiryDate
        );

        new AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    processPayment(merchantId, amount, cardNumber, expiryDate, cvv);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() <= 4) {
            return cardNumber;
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    private void processPayment(String merchantId, String amount, String cardNumber, String expiryDate, String cvv) {
        setProcessingState(true);

        PaymentRequest request = new PaymentRequest(
                merchantId,
                Double.parseDouble(amount),
                cardNumber, // In production, this should be tokenized
                expiryDate
        );

        String token = sharedPrefManager.getToken();
        if (TextUtils.isEmpty(token)) {
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
            setProcessingState(false);
            // Redirect to login activity
            return;
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentResponse> call = apiService.processPayment(
                "Bearer " + token,
                request
        );

        call.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                setProcessingState(false);

                if (response.isSuccessful() && response.body() != null) {
                    showPaymentResult(response.body());
                } else {
                    String errorMessage = "Payment failed";
                    if (response.code() == 401) {
                        errorMessage = "Authentication failed. Please login again.";
                    } else if (response.code() == 402) {
                        errorMessage = "Insufficient funds";
                    } else if (response.code() == 400) {
                        errorMessage = "Invalid payment details";
                    }

                    Log.e(TAG, "Payment failed: " + response.code() + " - " + response.message());
                    showErrorDialog("Payment Failed", errorMessage);
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                setProcessingState(false);
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                showErrorDialog("Network Error",
                        "Unable to process payment. Please check your internet connection and try again.");
            }
        });
    }

    private void setProcessingState(boolean processing) {
        isProcessing = processing;
        binding.btnProcessPayment.setEnabled(!processing);
        binding.btnProcessPayment.setText(processing ? "Processing..." : "Process Payment");

        // Disable/enable input fields during processing
        binding.etMerchantId.setEnabled(!processing);
        binding.etAmount.setEnabled(!processing);
        binding.etCardNumber.setEnabled(!processing);
        binding.etExpiryDate.setEnabled(!processing);
        binding.etCvv.setEnabled(!processing);
    }

    private void showPaymentResult(PaymentResponse response) {
        if (response != null) {
            if (response.isSuccess()) {
                showSuccessDialog(response);
            } else {
                String errorMessage = response.getMessage();
                if (TextUtils.isEmpty(errorMessage)) {
                    errorMessage = "Payment was declined. Please check your card details and try again.";
                }
                showErrorDialog("Payment Declined", errorMessage);
            }
        } else {
            showErrorDialog("Payment Error", "Invalid response from payment server.");
        }
    }

    private void showSuccessDialog(PaymentResponse response) {
        String message = String.format(
                response.getTransactionId(),
                "Payment Successful!\n\n" +
                        "Transaction ID: %s\n" +
                        "Amount: $%.2f\n" +
                        "Status: %s",
                response.getAmount(),
                response.getStatus()
        );

        new AlertDialog.Builder(this)
                .setTitle("Payment Successful")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    // Clear form and return to previous activity
                    clearForm();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void clearForm() {
        binding.etMerchantId.setText("");
        binding.etAmount.setText("");
        binding.etCardNumber.setText("");
        binding.etExpiryDate.setText("");
        binding.etCvv.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    // Helper classes for input formatting
    private static class CreditCardTextWatcher implements android.text.TextWatcher {
        private boolean isUpdating = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {
            if (isUpdating) return;

            isUpdating = true;
            String input = s.toString().replaceAll("\\s", "");
            StringBuilder formatted = new StringBuilder();

            for (int i = 0; i < input.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formatted.append(" ");
                }
                formatted.append(input.charAt(i));
            }

            s.replace(0, s.length(), formatted.toString());
            isUpdating = false;
        }
    }

    private static class ExpiryDateTextWatcher implements android.text.TextWatcher {
        private boolean isUpdating = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {
            if (isUpdating) return;

            isUpdating = true;
            String input = s.toString().replaceAll("[^\\d]", "");

            if (input.length() >= 2) {
                input = input.substring(0, 2) + "/" + input.substring(2, Math.min(input.length(), 4));
            }

            s.replace(0, s.length(), input);
            isUpdating = false;
        }
    }
}