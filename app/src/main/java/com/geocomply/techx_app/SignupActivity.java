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

public class SignupActivity extends AppCompatActivity {
    TextInputEditText edName, edPhone, edEmail, edPassword, edConfPassword;
    Button btnSignup;
    TextView tvMessage, btnLogin;
    RelativeLayout btnLoginGoogle, btnLoginFacebook;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        edName = findViewById(R.id.fullName);
        edPhone = findViewById(R.id.phoneNumber);
        edEmail = findViewById(R.id.email);
        edPassword = findViewById(R.id.password);
        edConfPassword = findViewById(R.id.confPassword);
        tvMessage = findViewById(R.id.showError);
        btnSignup = findViewById(R.id.btnSignup);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);

        btnSignup.setOnClickListener(view -> {
            if (checkInternetPermission()) {
                handleSignup();
            } else {
                Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnLoginGoogle.setOnClickListener(view -> {
            handleLoginGoogle();
        });

        btnLoginFacebook.setOnClickListener(view -> {
            Toast.makeText(this, "Continue with Facebook", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void handleSignup() {
        String name = edName.getText().toString();
        String phone = edPhone.getText().toString();
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        String confPassword = edConfPassword.getText().toString();

        User user = new User(name, phone, email, password);

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()
                || password.isEmpty() || confPassword.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else if (!password.equals(confPassword)) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Mật khẩu xác nhận không đúng.");
        } else {
            addUser(user);
        }
    }

    private void addUser(User user) {
        Call<User> register = ApiService.apiService.register(user);

        register.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignupActivity.this,
                            "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this,
                            "Tạo tài khoản thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(SignupActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLoginGoogle() {
        Toast.makeText(this, "Continue with Google", Toast.LENGTH_SHORT).show();
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}