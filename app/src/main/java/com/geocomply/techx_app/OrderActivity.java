package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.adapter.OrderAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.Image;
import com.geocomply.techx_app.model.Order;
import com.geocomply.techx_app.model.OrderDetail;
import com.geocomply.techx_app.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    LinearLayout emptyOrder;
    ImageView btnBack;
    RecyclerView recyclerOrder;
    OrderAdapter adapter;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        emptyOrder = findViewById(R.id.emptyOrder);
        btnBack = findViewById(R.id.btnBack);
        recyclerOrder = findViewById(R.id.recycler_order);

        recyclerOrder.setHasFixedSize(true);
        recyclerOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        session = new LoginSession(getApplicationContext());
        String userId = LoginSession.getIdKey();

        if (checkInternetPermission()) {
            loadOrderList(Integer.parseInt(userId));
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    // Load order list
    private void loadOrderList(int userId) {
        Call<ArrayList<OrderDetail>> order = ApiService.apiService.getOrderDetailsByUserId(userId);

        order.enqueue(new Callback<ArrayList<OrderDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<OrderDetail>> call, Response<ArrayList<OrderDetail>> response) {
                if (response.isSuccessful()) {
                    ArrayList<OrderDetail> orderDetails = response.body();
                    if (orderDetails != null && !orderDetails.isEmpty()) {
                        adapter = new OrderAdapter(getApplicationContext(), orderDetails);
                        recyclerOrder.setAdapter(adapter);
                    } else {
                        emptyOrder.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderDetail>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(OrderActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
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