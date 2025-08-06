package com.paymentapp.android.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.paymentapp.android.databinding.ActivityLoginBinding;
import com.paymentapp.android.model.LoginRequest;
import com.paymentapp.android.model.LoginResponse;
import com.paymentapp.android.api.ApiClient;
import com.paymentapp.android.api.ApiInterface;
import com.paymentapp.android.utils.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = new SharedPrefManager(this);

        if (sharedPrefManager.isLoggedIn()) {
            startMainActivity();
            finish();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (validateInputs(username, password)) {
                loginUser(username, password);
            }
        });
    }

    private boolean validateInputs(String username, String password) {
        boolean isValid = true;
        if (username.isEmpty()) {
            binding.etUsername.setError("Username required");
            isValid = false;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Password required");
            isValid = false;
        }
        return isValid;
    }

    private void loginUser(String username, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiService.login(new LoginRequest(username, password));

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    sharedPrefManager.saveToken(response.body().getAccessToken());
                    startMainActivity();
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}