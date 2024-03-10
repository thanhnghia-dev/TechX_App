package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.Address;
import com.geocomply.techx_app.model.User;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {
    TextView tvMessage;
    TextInputEditText edName, edPhone, edProvince, edCity, edWard, edADetail;
    ImageView btnBack;
    Button btnSave;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        edName = findViewById(R.id.fullName);
        edPhone = findViewById(R.id.phoneNumber);
        edProvince = findViewById(R.id.province);
        edCity = findViewById(R.id.city);
        edWard = findViewById(R.id.ward);
        edADetail = findViewById(R.id.addressDetail);
        tvMessage = findViewById(R.id.showError);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        session = new LoginSession(getApplicationContext());
        String userId = LoginSession.getIdKey();

        if (checkInternetPermission()) {
            getUserInformation(Integer.parseInt(userId));
            getUserAddress(Integer.parseInt(userId));
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(view -> {
            if (checkInternetPermission()) {
                insertAddress(Integer.parseInt(userId));
            } else {
                Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            startActivity(intent);
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
                        edName.setText(user.getName());
                        edPhone.setText(user.getPhone());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(AddressActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
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
                        edProvince.setText(address.getProvince());
                        edCity.setText(address.getCity());
                        edWard.setText(address.getWard());
                        edADetail.setText(address.getDetail());
                    }
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(AddressActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void insertAddress(int userId) {
        String name = edName.getText().toString();
        String phone = edPhone.getText().toString();
        String province = edProvince.getText().toString();
        String city = edCity.getText().toString();
        String ward = edWard.getText().toString();
        String detail = edADetail.getText().toString();

        Address address = new Address(userId, name, phone, detail, ward, city, province);

        if (name.isEmpty() || phone.isEmpty() || province.isEmpty()
                || city.isEmpty() || ward.isEmpty() || detail.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else {
            addAddress(address);
        }
    }

    private void addAddress(Address address) {
        Call<Address> insert = ApiService.apiService.postAddress(address);

        insert.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddressActivity.this,
                            "Địa chỉ lưu thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddressActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AddressActivity.this,
                            "API response successful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(AddressActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}