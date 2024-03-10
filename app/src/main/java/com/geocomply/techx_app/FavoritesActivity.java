package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.geocomply.techx_app.adapter.FavoriteAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {
    ImageView btnBack;
    ShimmerFrameLayout shimmerFavorites;
    RecyclerView recyclerFavorites;
    FavoriteAdapter adapter;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        btnBack = findViewById(R.id.btnBack);
        
        session = new LoginSession(getApplicationContext());

        shimmerFavorites = findViewById(R.id.shimmer_favorites);
        recyclerFavorites = findViewById(R.id.recycler_favorites);

        recyclerFavorites.setHasFixedSize(true);
        recyclerFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        shimmerFavorites.startShimmer();

        if (checkInternetPermission()) {
            loadFavoriteList();
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFavoriteList() {
        String userId = LoginSession.getIdKey();
        Call<ArrayList<Favorite>> favoriteList = ApiService.apiService.getFavoritesByUserId(Integer.parseInt(userId));

        favoriteList.enqueue(new Callback<ArrayList<Favorite>>() {
            @Override
            public void onResponse(Call<ArrayList<Favorite>> call, Response<ArrayList<Favorite>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Favorite> favorites = response.body();
                    shimmerFavorites.stopShimmer();
                    shimmerFavorites.setVisibility(View.GONE);
                    recyclerFavorites.setVisibility(View.VISIBLE);
                    adapter = new FavoriteAdapter(getApplicationContext(), favorites);
                    recyclerFavorites.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Favorite>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(FavoritesActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}