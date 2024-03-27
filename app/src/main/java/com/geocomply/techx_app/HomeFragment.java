package com.geocomply.techx_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.geocomply.techx_app.adapter.CategoryAdapter;
import com.geocomply.techx_app.adapter.ProductAdapter;
import com.geocomply.techx_app.adapter.ProductCatAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.Vendor;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    ImageSlider imageSlider;
    ShimmerFrameLayout shimmerCategory, shimmerNewProducts, shimmerHotProducts, shimmerPromotionProducts;
    RecyclerView recyclerCategory, recyclerNewProducts, recyclerHotProducts, recyclerPromotionProducts;
    ProductAdapter adapter;
    CategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageSlider = view.findViewById(R.id.imageSlider);

        recyclerCategory = view.findViewById(R.id.recycler_category);
        recyclerNewProducts = view.findViewById(R.id.recycler_new_products);
        recyclerHotProducts = view.findViewById(R.id.recycler_hot_products);
        recyclerPromotionProducts = view.findViewById(R.id.recycler_promotion_products);

        shimmerCategory = view.findViewById(R.id.shimmer_category);
        shimmerNewProducts = view.findViewById(R.id.shimmer_new_products);
        shimmerHotProducts = view.findViewById(R.id.shimmer_hot_products);
        shimmerPromotionProducts = view.findViewById(R.id.shimmer_promotion_products);

        recyclerCategory.setHasFixedSize(true);
        recyclerCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        shimmerCategory.startShimmer();

        recyclerNewProducts.setHasFixedSize(true);
        recyclerNewProducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        shimmerNewProducts.startShimmer();

        recyclerHotProducts.setHasFixedSize(true);
        recyclerHotProducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        shimmerHotProducts.startShimmer();

        recyclerPromotionProducts.setHasFixedSize(true);
        recyclerPromotionProducts.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        shimmerPromotionProducts.startShimmer();

        if (checkInternetPermission()) {
            loadImageSliderList();
            loadCategoryList();
            loadProductList();
        } else {
            Toast.makeText(getActivity(), "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

    }

    // Load image slider
    private void loadImageSliderList() {
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner4, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    // Load category list
    private void loadCategoryList() {
        Call<ArrayList<Vendor>> vendorList = ApiService.apiService.getVendors();

        vendorList.enqueue(new Callback<ArrayList<Vendor>>() {
            @Override
            public void onResponse(Call<ArrayList<Vendor>> call, Response<ArrayList<Vendor>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Vendor> vendors = response.body();
                    shimmerCategory.stopShimmer();
                    shimmerCategory.setVisibility(View.GONE);
                    recyclerCategory.setVisibility(View.VISIBLE);
                    categoryAdapter = new CategoryAdapter(getContext(), vendors);
                    recyclerCategory.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Vendor>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(getActivity(), "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load product list
    private void loadProductList() {
        Call<ArrayList<Product>> productList = ApiService.apiService.getProducts();

        productList.enqueue(new Callback<ArrayList<Product>>() {
            @Override
            public void onResponse(Call<ArrayList<Product>> call, Response<ArrayList<Product>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Product> products = response.body();
                    shimmerNewProducts.stopShimmer();
                    shimmerNewProducts.setVisibility(View.GONE);
                    recyclerNewProducts.setVisibility(View.VISIBLE);

                    shimmerHotProducts.stopShimmer();
                    shimmerHotProducts.setVisibility(View.GONE);
                    recyclerHotProducts.setVisibility(View.VISIBLE);

                    shimmerPromotionProducts.stopShimmer();
                    shimmerPromotionProducts.setVisibility(View.GONE);
                    recyclerPromotionProducts.setVisibility(View.VISIBLE);

                    adapter = new ProductAdapter(getContext(), products);
                    recyclerNewProducts.setAdapter(adapter);
                    recyclerHotProducts.setAdapter(adapter);
                    recyclerPromotionProducts.setAdapter(adapter);
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