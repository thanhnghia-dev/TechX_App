package com.geocomply.techx_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.geocomply.techx_app.adapter.ProductCatAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends Fragment {
    ShimmerFrameLayout shimmerProductCat;
    RecyclerView recyclerProductCat;
    ProductCatAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerProductCat = view.findViewById(R.id.shimmer_product_cat);
        recyclerProductCat = view.findViewById(R.id.recycler_product_cat);

        recyclerProductCat.setHasFixedSize(true);
        recyclerProductCat.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        shimmerProductCat.startShimmer();

        if (checkInternetPermission()) {
            loadProductCatList();
        } else {
            Toast.makeText(getActivity(), "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }
    }

    // Load product cat list
    private void loadProductCatList() {
        Call<ArrayList<Product>> productList = ApiService.apiService.getProducts();

        productList.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Product> products = response.body();
                    shimmerProductCat.stopShimmer();
                    shimmerProductCat.setVisibility(View.GONE);
                    recyclerProductCat.setVisibility(View.VISIBLE);
                    adapter = new ProductCatAdapter(getContext(), products);
                    recyclerProductCat.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Product>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(getActivity(), "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}