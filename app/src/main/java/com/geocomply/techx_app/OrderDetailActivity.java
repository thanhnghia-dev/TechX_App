package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.adapter.OrderAdapter;
import com.geocomply.techx_app.model.Order;
import com.geocomply.techx_app.model.OrderDetail;

import java.util.ArrayList;

public class OrderDetailActivity extends AppCompatActivity {
    TextView tvOrderId, tvOrderDate, tvOrderStatus, tvFullName, tvPhone, tvAddress, tvProductName,
            tvAmount, tvPrice, tvPaymentMethod, tvTemp, tvDiscount, tvTotalMoney;
    ImageView btnBack, productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvOrderId = findViewById(R.id.orderId);
        tvOrderDate = findViewById(R.id.orderDate);
        tvOrderStatus = findViewById(R.id.orderStatus);
        tvFullName = findViewById(R.id.fullName);
        tvPhone = findViewById(R.id.phoneNumber);
        tvAddress = findViewById(R.id.address);
        tvProductName = findViewById(R.id.productName);
        tvAmount = findViewById(R.id.amount);
        tvPrice = findViewById(R.id.total);
        tvPaymentMethod = findViewById(R.id.paymentMethod);
        tvTemp = findViewById(R.id.temp);
        tvDiscount = findViewById(R.id.discount);
        tvTotalMoney = findViewById(R.id.totalMoney);
        productImage = findViewById(R.id.productImage);
        btnBack = findViewById(R.id.btnBack);

        Intent intent1 = getIntent();
        int orderId = intent1.getIntExtra("orderId", -1);

        if (checkInternetPermission()) {
            if (orderId != -1) {
                loadOrderDetail();
            }
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    // Load order detail
    private void loadOrderDetail() {

    }

    @SuppressLint("SetTextI18n")
    private void getOrderStatus(Order order) {
        int status = order.getStatus();
        switch (status) {
            case 1:
                tvOrderStatus.setText("Đang xử lý");
                break;
            case 2:
                tvOrderStatus.setText("Đang vận chuyển");
                break;
            case 3:
                tvOrderStatus.setText("Giao hàng thành công");
                break;
            case 4:
                tvOrderStatus.setText("Đã hủy");
                break;
            default:
                tvOrderStatus.setText("Chờ thanh toán");
                break;
        }
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}