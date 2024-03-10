package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.common.UserIdCallback;
import com.geocomply.techx_app.common.UserUtils;
import com.geocomply.techx_app.model.User;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPasswordActivity extends AppCompatActivity {
    TextInputEditText edPassword, edConfPassword;
    TextView tvMessage;
    Button btnSave;
    String email;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        edPassword = findViewById(R.id.password);
        edConfPassword = findViewById(R.id.confPassword);
        tvMessage = findViewById(R.id.showError);
        btnSave = findViewById(R.id.btnSave);

        session = new LoginSession(getApplicationContext());
        email = getIntent().getStringExtra("email");
        UserUtils.getUserId(email);

        btnSave.setOnClickListener(view -> {
            handleChangePassword();
        });

    }

    @SuppressLint("SetTextI18n")
    private void handleChangePassword() {
        String password = edPassword.getText().toString();
        String confPassword = edConfPassword.getText().toString();
        String userId = LoginSession.getIdKey();

        if (password.isEmpty() || confPassword.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else if (!password.equals(confPassword)) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Mật khẩu xác nhận không đúng.");
        } else {
            updatePassword(Integer.parseInt(userId), password);
        }
    }

    private void updatePassword(int id, String password) {
        User user = new User(id, password);
        Call<Void> update = ApiService.apiService.putUser(id, user);

        update.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    LoginSession.clearSession();
                    Intent intent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(NewPasswordActivity.this,
                            "Đặt lại mật khẩu thành công!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(NewPasswordActivity.this,
                            "Đặt lại mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(NewPasswordActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}