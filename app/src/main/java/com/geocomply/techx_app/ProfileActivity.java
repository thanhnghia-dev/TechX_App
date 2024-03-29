package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.AddressIdCallback;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.common.UserIdCallback;
import com.geocomply.techx_app.model.Address;
import com.geocomply.techx_app.model.Log;
import com.geocomply.techx_app.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    TextView tvName, tvAddress, tvEmail, tvPhone;
    ImageView btnBack;
    RelativeLayout changePassword, editProfile, addressList, deleteAccount;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.fullName);
        tvAddress = findViewById(R.id.address);
        tvEmail = findViewById(R.id.email);
        tvPhone = findViewById(R.id.phoneNumber);
        btnBack = findViewById(R.id.btnBack);
        changePassword = findViewById(R.id.changePassword);
        editProfile = findViewById(R.id.editProfile);
        addressList = findViewById(R.id.addressList);
        deleteAccount = findViewById(R.id.deleteAccount);

        session = new LoginSession(getApplicationContext());
        String id = LoginSession.getIdKey();

        if (checkInternetPermission()) {
            getUserInformation(Integer.parseInt(id));
            getUserAddress(Integer.parseInt(id));
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnBack.setOnClickListener(view -> {
            finish();
        });

        editProfile.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
            finish();
        });

        addressList.setOnClickListener(view -> {
            getAddressIdByUserId(Integer.parseInt(id), addressId -> {
                Intent intent = new Intent(ProfileActivity.this, AddressActivity.class);
                if (addressId > 0) {
                    intent.putExtra("addressId", addressId);
                }
                startActivity(intent);
            });

        });

        changePassword.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        });

        deleteAccount.setOnClickListener(view -> {
            handleDeleteAccDialog();
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
                        tvName.setText(user.getName());
                        tvEmail.setText(user.getEmail());
                        tvPhone.setText(user.getPhone());
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                android.util.Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
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
                android.util.Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getAddressIdByUserId(int userId, final AddressIdCallback callback) {
        Call<Address> call = ApiService.apiService.getAddressByUserId(userId);

        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                if (response.isSuccessful()) {
                    Address address = response.body();
                    assert address != null;
                    int addressId = address.getId();
                    callback.onAddressIdReceived(addressId);
                } else {
                    callback.onAddressIdReceived(0);
                }
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                android.util.Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                callback.onAddressIdReceived(0);
            }
        });
    }


    // Handle display logout dialog
    @SuppressLint("SetTextI18n")
    private void handleDeleteAccDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_logout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvMessage = dialog.findViewById(R.id.message);
        Button btnYes = dialog.findViewById(R.id.btnSend);
        Button btnNo = dialog.findViewById(R.id.btnCancel);

        tvMessage.setText("Bạn có chắc muốn xóa tài khoản?");

        btnYes.setOnClickListener(view -> {
            deleteAccount();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void deleteAccount() {
        String id = LoginSession.getIdKey();

        if (id == null || id.isEmpty()) {
            Toast.makeText(ProfileActivity.this,
                    "Invalid user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<Void> delete = ApiService.apiService.deleteUser(Integer.parseInt(id));

        delete.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
//                    Log log = new Log(Integer.parseInt(id), Log.WARNING, getPhoneIpAddress(),
//                            "Delete account", "Account id: " + id + " is deleted", Log.SUCCESS);
//                    addLog(log);

                    Toast.makeText(ProfileActivity.this,
                            "Tài khoản đã được xóa thành công", Toast.LENGTH_SHORT).show();

                    LoginSession.clearSession();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
//                    Log log = new Log(Integer.parseInt(id), Log.ALERT, getPhoneIpAddress(),
//                            "Delete account", "Account id: " + id + " deletes failed", Log.FAILED);
//                    addLog(log);

                    Toast.makeText(ProfileActivity.this,
                            "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                android.util.Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLog(Log log) {
        Call<Log> loginLog = ApiService.apiService.postLog(log);

        loginLog.enqueue(new Callback<Log>() {
            @Override
            public void onResponse(Call<Log> call, Response<Log> response) {
                if (response.isSuccessful()) {
                    android.util.Log.e("API_SUCCESS", "Logs: " + response.body());
                }
            }

            @Override
            public void onFailure(Call<Log> call, Throwable t) {
                android.util.Log.e("API_ERROR", "Error occurred: " + t.getMessage());
            }
        });
    }

    private String getPhoneIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}