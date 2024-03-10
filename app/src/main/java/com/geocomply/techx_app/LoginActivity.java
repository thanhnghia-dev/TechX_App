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
import com.geocomply.techx_app.common.UserIdCallback;
import com.geocomply.techx_app.model.User;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText edEmail, edPassword;
    Button btnLogin;
    TextView tvMessage, btnSignup, btnForgotPassword;
    RelativeLayout btnLoginGoogle, btnLoginFacebook;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edEmail = findViewById(R.id.email);
        edPassword = findViewById(R.id.password);
        tvMessage = findViewById(R.id.showError);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnLoginFacebook = findViewById(R.id.btnLoginFacebook);

        session = new LoginSession(getApplicationContext());

        String email = LoginSession.getEmailKey();
        if (email != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(view -> {
            if (checkInternetPermission()) {
                handleLogin();
            } else {
                Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });

        btnForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        btnLoginGoogle.setOnClickListener(view -> {
            handleLoginGoogle();
        });

        btnLoginFacebook.setOnClickListener(view -> {
            Toast.makeText(this, "Continue with Facebook", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void handleLogin() {
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else {
            addUser(email, password);
        }
    }

    private void addUser(String email, String password) {
        User user = new User(email, password);
        Call<User> login = ApiService.apiService.loginUser(user);

        login.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    getUserId(email, userId -> {
                        if (userId != null) {
                            LoginSession.saveLoginSession(userId, email, password);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Đăng nhập không thành công.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(LoginActivity.this,
                            "Đăng nhập không thành công. Vui lòng kiểm tra email và mật khẩu.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(LoginActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLoginGoogle() {
        Toast.makeText(this, "Continue with Google", Toast.LENGTH_SHORT).show();
    }

    private void getUserId(String email, final UserIdCallback callback) {
        Call<String> call = ApiService.apiService.getUserId(email);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String userId = response.body();
                    callback.onUserIdReceived(userId);
                } else {
                    callback.onUserIdReceived(null);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                callback.onUserIdReceived(null);
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