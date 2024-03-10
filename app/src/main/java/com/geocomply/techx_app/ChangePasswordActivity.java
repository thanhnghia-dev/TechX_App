package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class ChangePasswordActivity extends AppCompatActivity {
    TextInputEditText edOldPassword, edPassword, edConfPassword;
    TextView tvMessage;
    ImageView btnBack;
    Button btnSave;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edOldPassword = findViewById(R.id.oldPassword);
        edPassword = findViewById(R.id.password);
        edConfPassword = findViewById(R.id.confPassword);
        tvMessage = findViewById(R.id.showError);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        session = new LoginSession(getApplicationContext());
        String id = LoginSession.getIdKey();

        btnSave.setOnClickListener(view -> {
            handleChangePassword(Integer.parseInt(id));
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint("SetTextI18n")
    private void handleChangePassword(int id) {
        String oldPassword = edOldPassword.getText().toString();
        String password = edPassword.getText().toString();
        String confPassword = edConfPassword.getText().toString();

        if (oldPassword.isEmpty() || password.isEmpty() || confPassword.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else if (!password.equals(confPassword)) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Mật khẩu xác nhận không đúng.");
        } else {
            updatePassword(id, password);
        }
    }

    private void updatePassword(int id, String password) {
        User user = new User(id, password);
        Call<Void> update = ApiService.apiService.putUser(id, user);

        update.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(ChangePasswordActivity.this,
                            "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ChangePasswordActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserPassword(int userId, final UserIdCallback callback) {
        Call<User> information = ApiService.apiService.getUser(userId);

        information.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();

                    if (user != null) {
                        String userPassword = user.getPassword();
                        callback.onUserIdReceived(userPassword);
                    } else {
                        callback.onUserIdReceived(null);
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ChangePasswordActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}