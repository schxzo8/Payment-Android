package com.paymentapp.android.merchant;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.paymentapp.android.databinding.ActivityMerchantBinding;
import com.paymentapp.android.model.Merchant;
import com.paymentapp.android.api.ApiClient;
import com.paymentapp.android.api.ApiInterface;
import com.paymentapp.android.utils.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantActivity extends AppCompatActivity {
    private ActivityMerchantBinding binding;
    private MerchantAdapter adapter;
    private List<Merchant> merchantList = new ArrayList<>();
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMerchantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefManager = new SharedPrefManager(this);

        setupRecyclerView();
        loadMerchants();

        binding.fabAddMerchant.setOnClickListener(v -> {
            // Start AddMerchantActivity
        });
    }

    private void setupRecyclerView() {
        adapter = new MerchantAdapter(merchantList);
        binding.rvMerchants.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMerchants.setAdapter(adapter);
    }

    private void loadMerchants() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Merchant>> call = apiService.getMerchants(
                "Bearer " + sharedPrefManager.getToken());

        call.enqueue(new Callback<List<Merchant>>() {
            @Override
            public void onResponse(Call<List<Merchant>> call, Response<List<Merchant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    merchantList.clear();
                    merchantList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Merchant>> call, Throwable t) {
                // Handle error
            }
        });
    }
}