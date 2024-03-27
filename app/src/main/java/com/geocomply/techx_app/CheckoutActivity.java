package com.geocomply.techx_app;

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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.adapter.CartAdapter;
import com.geocomply.techx_app.adapter.CheckoutAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.Address;
import com.geocomply.techx_app.model.CartItem;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    TextView tvFullName, tvPhone, tvAddress, tvCounter, tvTemp, tvTotal;
    ImageView btnBack;
    Button btnPayment;
    RadioButton payCash, payEWallet, payATM;
    RecyclerView recyclerCheckout;
    RelativeLayout btnDeliveryAddress;
    CheckoutAdapter adapter;
    LoginSession session;
    ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvFullName = findViewById(R.id.fullName);
        tvPhone = findViewById(R.id.phoneNumber);
        tvAddress = findViewById(R.id.address);
        tvCounter = findViewById(R.id.counter);
        tvTemp = findViewById(R.id.temp);
        tvTotal = findViewById(R.id.total);
        payCash = findViewById(R.id.payCash);
        payEWallet = findViewById(R.id.payEWallet);
        payATM = findViewById(R.id.payATM);
        btnBack = findViewById(R.id.btnBack);
        btnPayment = findViewById(R.id.btnPayment);
        btnDeliveryAddress = findViewById(R.id.deliveryAddress);
        recyclerCheckout = findViewById(R.id.recycler_checkout);

        session = new LoginSession(getApplicationContext());
        String userId = LoginSession.getIdKey();

        recyclerCheckout.setHasFixedSize(true);
        recyclerCheckout.setLayoutManager(new LinearLayoutManager(this));

        if (checkInternetPermission()) {
            loadCartList();
            getUserInformation(Integer.parseInt(userId));
            getUserAddress(Integer.parseInt(userId));
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnDeliveryAddress.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivity(intent);
        });

        btnPayment.setOnClickListener(view -> {
            Intent intent = new Intent(this, SuccessActivity.class);
            startActivity(intent);
            finish();
        });

        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void getUserInformation(int id) {
        Call<User> userInfo = ApiService.apiService.getUser(id);

        userInfo.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();

                    if (user != null) {
                        tvFullName.setText(user.getName());
                        tvPhone.setText(user.getPhone());
                        tvAddress.setText("Địa chỉ chi tiết");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(CheckoutActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserAddress(int userId) {
        Call<Address> address = ApiService.apiService.getAddressByUserId(userId);

        address.enqueue(new Callback<Address>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful()) {
                    Address address = response.body();

                    if (address != null) {
                        tvAddress.setText(address.getDetail() + ", " + address.getWard()
                                + ", " + address.getCity() + ", " + address.getProvince());
                    }
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(CheckoutActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
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
                    tvTemp.setText(formatNumber(total) + " ₫");
                    tvTotal.setText(formatNumber(total) + " ₫");
                    tvCounter.setText("Sản phẩm (" + shoppingCarts.size() + ")");

                    adapter = new CheckoutAdapter(getApplicationContext(), shoppingCarts);
                    recyclerCheckout.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ShoppingCart>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(CheckoutActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
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