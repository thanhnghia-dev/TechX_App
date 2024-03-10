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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.User;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    TextView tvMessage;
    TextInputEditText edName, edPhone, edEmail;
    ImageView btnBack;
    Button btnSave;
    RelativeLayout btnAddress;
    LoginSession session;
    
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edName = findViewById(R.id.fullName);
        edPhone = findViewById(R.id.phoneNumber);
        edEmail = findViewById(R.id.email);
        tvMessage = findViewById(R.id.showError);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnAddress = findViewById(R.id.address);

        session = new LoginSession(getApplicationContext());
        String id = LoginSession.getIdKey();

        if (checkInternetPermission()) {
            getUserInformation(Integer.parseInt(id));
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(view -> {
            if (checkInternetPermission()) {
                updateProfile(Integer.parseInt(id));
            } else {
                Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        btnAddress.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivity(intent);
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
                        edEmail.setText(user.getEmail());
                        edEmail.setFocusable(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(EditProfileActivity.this, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateProfile(int id) {
        String name = edName.getText().toString();
        String phone = edPhone.getText().toString();

        User user = new User(id, name, phone);

        if (name.isEmpty() || phone.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else {
            updateUser(id, user);
        }
    }

    private void updateUser(int id, User user) {
        Call<Void> update = ApiService.apiService.putUser(id, user);

        update.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this,
                            "Thông tin cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(EditProfileActivity.this,
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