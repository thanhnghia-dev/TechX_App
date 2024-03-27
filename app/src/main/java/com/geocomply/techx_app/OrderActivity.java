package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.geocomply.techx_app.adapter.OrderAdapter;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.Image;
import com.geocomply.techx_app.model.Order;
import com.geocomply.techx_app.model.OrderDetail;
import com.geocomply.techx_app.model.Product;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    ImageView btnBack;
    RecyclerView recyclerOrder;
    OrderAdapter adapter;
    LoginSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        btnBack = findViewById(R.id.btnBack);
        recyclerOrder = findViewById(R.id.recycler_order);

        recyclerOrder.setHasFixedSize(true);
        recyclerOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        session = new LoginSession(getApplicationContext());
        String userId = LoginSession.getIdKey();

        if (checkInternetPermission()) {
            loadOrderList();
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    // Load order list
    private void loadOrderList() {
        Image image = new Image();
        image.setUrl("https://cdn.tgdd.vn/Products/Images/42/309864/vivo-v29e-den-glr-1.jpg");
        ArrayList<Image> images = new ArrayList<>();
        images.add(image);
        Product product = new Product();
        product.setId(64);
        product.setName("Vivo V29e 5G");
        product.setDiscounted(9490000);
        product.setImages(images);
        Order order = new Order();
        order.setId(1);
        order.setStatus(1);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setAmount(2);
        orderDetail.setProdIdNavigation(product);
        orderDetail.setOrderIdNavigation(order);

        ArrayList<OrderDetail> orders = new ArrayList<>();
        orders.add(orderDetail);
        orders.add(orderDetail);

        adapter = new OrderAdapter(getApplicationContext(), orders);
        recyclerOrder.setAdapter(adapter);
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}