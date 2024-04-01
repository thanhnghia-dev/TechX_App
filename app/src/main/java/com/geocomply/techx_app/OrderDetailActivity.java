package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geocomply.techx_app.adapter.OrderAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.model.Address;
import com.geocomply.techx_app.model.Order;
import com.geocomply.techx_app.model.OrderDetail;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.User;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    TextView tvOrderId, tvOrderDate, tvOrderStatus, tvFullName, tvPhone, tvAddress, tvProductName,
            tvAmount, tvPrice, tvPaymentMethod, tvTemp, tvTotalMoney;
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
        tvTotalMoney = findViewById(R.id.totalMoney);
        productImage = findViewById(R.id.productImage);
        btnBack = findViewById(R.id.btnBack);

        Intent intent1 = getIntent();
        int orderId = intent1.getIntExtra("orderId", -1);

        if (checkInternetPermission()) {
            if (orderId != -1) {
                loadOrderDetail(orderId);
            }
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(v -> finish());
    }

    // Load order detail
    private void loadOrderDetail(int orderId) {
        Call<OrderDetail> orderDetail = ApiService.apiService.getOrderDetailByOrderId(orderId);

        orderDetail.enqueue(new Callback<OrderDetail>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<OrderDetail> call, Response<OrderDetail> response) {
                if (response.isSuccessful()) {
                    OrderDetail od = response.body();
                    assert od != null;
                    Order o = od.getOrderIdNavigation();
                    Product p = od.getProdIdNavigation();
                    User u = o.getUserIdNavigation();
                    Address a = o.getAddrIdNavigation();
                    String total = formatNumber((int) od.getPrice() * od.getAmount());

                    tvOrderId.setText(String.valueOf(od.getOrderId()));
                    tvOrderDate.setText(convertDateType(o.getOrderDate()));
                    getOrderStatus(o.getStatus());
                    tvFullName.setText(u.getName());
                    tvPhone.setText(u.getPhone());
                    tvAddress.setText(a.getDetail() + ", " + a.getWard()
                            + ", " + a.getCity() + ", " + a.getProvince());
                    tvProductName.setText(p.getName());
                    tvAmount.setText(String.valueOf(od.getAmount()));
                    tvPrice.setText(total + " ₫");
                    tvPaymentMethod.setText(o.getPaymentMethod());
                    tvTemp.setText(total + " ₫");
                    tvTotalMoney.setText(total + " ₫");

                    if (p.getImages() != null && !p.getImages().isEmpty()) {
                        String imageUrl = p.getImages().get(0).getUrl();
                        Glide.with(OrderDetailActivity.this).load(imageUrl).into(productImage);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderDetail> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(OrderDetailActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getOrderStatus(int status) {
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

    @SuppressLint("SimpleDateFormat")
    private String convertDateType(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date date = inputFormat.parse(inputDate);
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        return decimalFormat.format(number);
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}