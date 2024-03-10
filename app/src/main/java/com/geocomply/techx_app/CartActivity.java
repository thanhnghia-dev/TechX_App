package com.geocomply.techx_app;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.adapter.CartAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.CartItem;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.Product;
import com.google.android.material.badge.ExperimentalBadgeUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    TextView tvCounter, tvTotal;
    ImageView btnBack, btnDelete;
    Button btnCheckout;
    RecyclerView recyclerCart;
    CartAdapter adapter;
    LoginSession session;
    ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @OptIn(markerClass = ExperimentalBadgeUtils.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        tvCounter = findViewById(R.id.counter);
        tvTotal = findViewById(R.id.total);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);
        btnCheckout = findViewById(R.id.btnCheckout);
        recyclerCart = findViewById(R.id.recycler_cart);

        session = new LoginSession(getApplicationContext());

        recyclerCart.setHasFixedSize(true);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));

        if (checkInternetPermission()) {
            loadCartList();
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnCheckout.setOnClickListener(view -> {
            for (ShoppingCart cart : shoppingCarts) {
                for (CartItem item : cart.getCartItems()) {
                    if (item == null) {
                        btnCheckout.setEnabled(false);
                    } else {
                        Intent intent = new Intent(this, CheckoutActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        btnDelete.setOnClickListener(view -> {
            Toast.makeText(this, "Xóa toàn bộ giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Load cart list
    @SuppressLint("SetTextI18n")
    private void loadCartList() {
        String userId = LoginSession.getIdKey();
        Call<ArrayList<ShoppingCart>> cart = ApiService.apiService.getShoppingCartsByUserId(Integer.parseInt(userId));

        cart.enqueue(new Callback<ArrayList<ShoppingCart>>() {
            @Override
            public void onResponse(Call<ArrayList<ShoppingCart>> call, Response<ArrayList<ShoppingCart>> response) {
                if (response.isSuccessful()) {
                    shoppingCarts = response.body();

                    assert shoppingCarts != null;
                    int total = 0;
                    for (ShoppingCart cart : shoppingCarts) {
                        for (CartItem item : cart.getCartItems()) {
                            Product product = item.prodIdNavigation;
                            total += (int) (product.getDiscounted() * item.getAmount());
                        }
                    }
                    tvTotal.setText(formatNumber(total) + " ₫");
                    tvCounter.setText("Tất cả (" + shoppingCarts.size() + ") sản phẩm");

                    adapter = new CartAdapter(getApplicationContext(), shoppingCarts);
                    recyclerCart.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ShoppingCart>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(CartActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        return decimalFormat.format(number);
    }
}