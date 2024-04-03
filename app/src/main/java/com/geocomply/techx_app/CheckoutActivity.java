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
import com.geocomply.techx_app.model.Order;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.User;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    TextView tvFullName, tvPhone, tvAddress, tvCounter, tvTemp, tvDiscount, tvTotal;
    ImageView btnBack;
    Button btnPayment;
    RadioButton payCash, payCreditCard, payATM;
    RecyclerView recyclerCheckout;
    RelativeLayout btnDeliveryAddress;
    CheckoutAdapter adapter;
    LoginSession session;
    int addressId, total;
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
        tvDiscount = findViewById(R.id.discount);
        tvTotal = findViewById(R.id.total);
        payCash = findViewById(R.id.payCash);
        payCreditCard = findViewById(R.id.payCreditCard);
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
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnDeliveryAddress.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivity(intent);
        });

        btnPayment.setOnClickListener(view -> {
            handlePayment(Integer.parseInt(userId));
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
                        getUserAddress(id);
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
                        addressId = address.getId();
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
                    int temp = 0, discount = 0;
                    LocalDate date;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        date = LocalDate.now();
                        int day = date.getDayOfMonth();
                        int month = date.getMonthValue();

                        for (ShoppingCart cart : shoppingCarts) {
                            for (CartItem item : cart.getCartItems()) {
                                Product product = item.prodIdNavigation;
                                temp += (int) (product.getDiscounted() * item.getAmount());
                            }
                        }
                        if (day == month) {
                            discount = (int) (temp * 0.5);
                        }
                    }
                    total = temp - discount;

                    tvTemp.setText(formatNumber(temp) + " ₫");
                    tvDiscount.setText("-" + formatNumber(discount) + " ₫");
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

    private void handlePayment(int userId) {
        String paymentMethod = getPaymentMethod();

        Order order = new Order(userId, addressId, (double) total, paymentMethod);

        if (!payCash.isChecked() || !payCreditCard.isChecked() || !payATM.isChecked()) {
            Toast.makeText(this, "Vui lòng chọn Phương thức thanh toán!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, SuccessActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void addOrder(Order order) {
        Call<Order> insert = ApiService.apiService.postOrder(order);

        insert.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(CheckoutActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getPaymentMethod() {
        if (payCash.isChecked()) {
            return (String) payCash.getHint();
        } else if (payCreditCard.isChecked()) {
            return (String) payCreditCard.getHint();
        } else {
            return (String) payATM.getHint();
        }
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