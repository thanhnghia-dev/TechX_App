package com.geocomply.techx_app;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.api.ApiGHNService;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.Address;
import com.geocomply.techx_app.model.User;
import com.geocomply.techx_app.model.logistic.District;
import com.geocomply.techx_app.model.logistic.DistrictData;
import com.geocomply.techx_app.model.logistic.Province;
import com.geocomply.techx_app.model.logistic.ProvinceData;
import com.geocomply.techx_app.model.logistic.Ward;
import com.geocomply.techx_app.model.logistic.WardData;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {
    TextView tvMessage;
    TextInputEditText edName, edPhone, edADetail;
    AutoCompleteTextView acProvince, acDistrict, acWard;
    ImageView btnBack;
    Button btnSave;
    LoginSession session;
    String[] province, district, ward;
    int[] provinceId, districtId;
    int addressId;
    String selectedProvince, selectedDistrict, selectedWard;
    HashMap<String, Integer> provinceMap = new HashMap<>();
    HashMap<String, Integer> districtMap = new HashMap<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        edName = findViewById(R.id.fullName);
        edPhone = findViewById(R.id.phoneNumber);
        acProvince = findViewById(R.id.province);
        acDistrict = findViewById(R.id.city);
        acWard = findViewById(R.id.ward);
        edADetail = findViewById(R.id.addressDetail);
        tvMessage = findViewById(R.id.showError);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        session = new LoginSession(getApplicationContext());
        String userId = LoginSession.getIdKey();

        Intent intent1 = getIntent();
        addressId = intent1.getIntExtra("addressId", 0);

        if (checkInternetPermission()) {
            getUserInformation(Integer.parseInt(userId));
            getUserAddress(Integer.parseInt(userId));
            getProvince();
            getDistrict();
            getWard();
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        acProvince.setOnItemClickListener((parent, view, position, id) -> {
            selectedProvince = (String) parent.getItemAtPosition(position);
        });

        acDistrict.setOnItemClickListener((parent, view, position, id) -> {
            selectedDistrict = (String) parent.getItemAtPosition(position);
        });

        acWard.setOnItemClickListener((parent, view, position, id) -> {
            selectedWard = (String) parent.getItemAtPosition(position);
        });

        btnSave.setOnClickListener(view -> {
            if (checkInternetPermission()) {
                handleSaveButton(Integer.parseInt(userId));
            } else {
                Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(view -> {
            showCancelDialog();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showCancelDialog();
            }
        };
        AddressActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);

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
                        acProvince.setText(address.getProvince());
                        acDistrict.setText(address.getCity());
                        acWard.setText(address.getWard());
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
    private void handleSaveButton(int userId) {
        String name = edName.getText().toString();
        String phone = edPhone.getText().toString();
        String province = selectedProvince;
        String district = selectedDistrict;
        String ward = selectedWard;
        String detail = edADetail.getText().toString();

        Address address = new Address(userId, name, phone, detail, ward, district, province);

        if (name.isEmpty() || phone.isEmpty() || detail.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else {
            if (addressId != 0) {
                updateAddress(userId, address);
            } else {
                addAddress(address);
            }
        }
    }

    // Get Province in Vietnam
    private void getProvince() {
        String token = ApiGHNService.token;
        Call<ProvinceData> dataCall = ApiGHNService.apiService.getProvinces(token);

        dataCall.enqueue(new Callback<ProvinceData>() {
            @Override
            public void onResponse(Call<ProvinceData> call, Response<ProvinceData> response) {
                if (response.isSuccessful()) {
                    ProvinceData data = response.body();
                    if (data != null) {
                        ArrayList<Province> provinces = data.getData();

                        provinceId = new int[provinces.size()];
                        province = new String[provinces.size()];
                        for (int i = 0; i < provinces.size(); i++) {
                            provinceId[i] = provinces.get(i).getProvinceID();
                            province[i] = provinces.get(i).getProvinceName();
                            provinceMap.put(province[i], provinceId[i]);
                        }
                        displayProvince();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProvinceData> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(AddressActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProvince() {
        if (province != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, province);
            acProvince.setAdapter(adapter);
        }
    }

    private int getProvinceIdByName(String provinceName) {
        return provinceMap.get(provinceName);
    }

    // Get District according to Province
    private void getDistrict() {
        String token = ApiGHNService.token;
        Call<DistrictData> dataCall = ApiGHNService.apiService.getDistricts(token);

        dataCall.enqueue(new Callback<DistrictData>() {
            @Override
            public void onResponse(Call<DistrictData> call, Response<DistrictData> response) {
                if (response.isSuccessful()) {
                    DistrictData data = response.body();
                    if (data != null) {
                        ArrayList<District> districts = data.getData();

                        districtId = new int[districts.size()];
                        district = new String[districts.size()];
                        for (int i = 0; i < districts.size(); i++) {
                            districtId[i] = districts.get(i).getDistrictID();
                            district[i] = districts.get(i).getDistrictName();
                            districtMap.put(district[i], districtId[i]);
                        }
                        displayDistrict();
                    }
                }
            }

            @Override
            public void onFailure(Call<DistrictData> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(AddressActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDistrict() {
        if (district != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, district);
            acDistrict.setAdapter(adapter);
        }
    }

    private int getDistrictIdByName(String districtName) {
        return districtMap.get(districtName);
    }

    // Get Ward according to District
    private void getWard() {
        String token = ApiGHNService.token;
        int districtId = 1566;

        Call<WardData> dataCall = ApiGHNService.apiService.getWards(token, districtId);

        dataCall.enqueue(new Callback<WardData>() {
            @Override
            public void onResponse(Call<WardData> call, Response<WardData> response) {
                if (response.isSuccessful()) {
                    WardData data = response.body();
                    if (data != null) {
                        ArrayList<Ward> wards = data.getData();

                        ward = new String[wards.size()];
                        for (int i = 0; i < wards.size(); i++) {
                            ward[i] = wards.get(i).getWardName();
                        }
                        displayWard();
                    }
                }
            }

            @Override
            public void onFailure(Call<WardData> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(AddressActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayWard() {
        if (ward != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.drop_down_item, ward);
            acWard.setAdapter(adapter);
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
                    finish();
                } else {
                    Toast.makeText(AddressActivity.this,
                            "Địa chỉ lưu thất bại!", Toast.LENGTH_SHORT).show();
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

    private void updateAddress(int userId, Address address) {
        Call<Void> update = ApiService.apiService.putAddress(userId, address);

        update.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddressActivity.this,
                            "Địa chỉ cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddressActivity.this,
                            "Địa chỉ cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(AddressActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Bạn có chắc chắn rời khỏi trang này?")
                .setMessage("Thông tin bạn vừa nhập sẽ không được lưu lại.")
                .setPositiveButton("RỜI KHỎI", (dialog, which) -> {
                    finish();
                    dialog.dismiss();
                })
                .setNegativeButton("Ở LẠI", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}