package com.paymentapp.android.merchant;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.paymentapp.android.databinding.ItemMerchantBinding;
import com.paymentapp.android.model.Merchant;
import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> {
    private final List<Merchant> merchantList;

    public MerchantAdapter(List<Merchant> merchantList) {
        this.merchantList = merchantList;
    }

    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMerchantBinding binding = ItemMerchantBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MerchantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
        Merchant merchant = merchantList.get(position);
        holder.binding.tvMerchantName.setText(merchant.getMerchantName());
        holder.binding.tvMerchantId.setText(merchant.getMerchantId());
    }

    @Override
    public int getItemCount() {
        return merchantList.size();
    }

    static class MerchantViewHolder extends RecyclerView.ViewHolder {
        ItemMerchantBinding binding;

        public MerchantViewHolder(ItemMerchantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}