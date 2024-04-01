package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.geocomply.techx_app.adapter.ProductCatAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductCatActivity extends AppCompatActivity {
    TextView tvCategoryName;
    ShimmerFrameLayout shimmerProductCat;
    RecyclerView recyclerProductCat;
    ProductCatAdapter adapter;
    ImageView btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cat);

        tvCategoryName = findViewById(R.id.categoryName);
        btnBack = findViewById(R.id.btnBack);
        shimmerProductCat = findViewById(R.id.shimmer_product_cat);
        recyclerProductCat = findViewById(R.id.recycler_product_cat);

        recyclerProductCat.setHasFixedSize(true);
        recyclerProductCat.setLayoutManager(new GridLayoutManager(this, 2));
        shimmerProductCat.startShimmer();

        Intent intent1 = getIntent();
        int vendorId = intent1.getIntExtra("vendorId", -1);
        String vendorName = intent1.getStringExtra("vendorName");

        if (checkInternetPermission()) {
            if (vendorId != -1 && vendorName != null) {
                loadProductCatList(vendorId);
                tvCategoryName.setText(vendorName);
            }
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    // Load product cat list
    private void loadProductCatList(int vendorId) {
        Call<ArrayList<Product>> productList = ApiService.apiService.getProductsByVendorId(vendorId);

        productList.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Product> products = response.body();
                    shimmerProductCat.stopShimmer();
                    shimmerProductCat.setVisibility(View.GONE);
                    recyclerProductCat.setVisibility(View.VISIBLE);
                    adapter = new ProductCatAdapter(getApplicationContext(), products);
                    recyclerProductCat.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProductCatActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}