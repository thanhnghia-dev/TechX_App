package com.geocomply.techx_app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geocomply.techx_app.ProductDetailActivity;
import com.geocomply.techx_app.R;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.Vendor;

import java.text.DecimalFormat;
import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final List<Vendor> vendorList;
    static Context context;

    public CategoryAdapter(Context context, List<Vendor> vendorList) {
        this.context = context;
        this.vendorList = vendorList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Vendor vendor = vendorList.get(position);
        if (vendor == null) {
            return;
        }
        holder.bindData(vendor);

        holder.cvCategory.setOnClickListener(view -> {
            Toast.makeText(context, vendor.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return vendorList != null ? vendorList.size() : 0;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        CardView cvCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            catImage = itemView.findViewById(R.id.catImage);
            cvCategory = itemView.findViewById(R.id.cvCategory);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Vendor vendor) {
            if (vendor.getAddress() != null && !vendor.getAddress().isEmpty()) {
                String imageUrl = vendor.getAddress();
                Glide.with(context).load(imageUrl).into(catImage);
            }
        }
    }
}
