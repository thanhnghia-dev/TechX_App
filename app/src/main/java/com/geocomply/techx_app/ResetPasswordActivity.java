package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.common.MailService;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ResetPasswordActivity extends AppCompatActivity {
    TextView tvMessage, tvEmail, btnResend;
    TextInputEditText edEmail;
    Button btnSend, btnConfirm;
    PinView pinView;
    LinearLayout layoutEmail, layoutPinView;
    int code;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        tvMessage = findViewById(R.id.showError);
        tvEmail = findViewById(R.id.tvEmail);
        edEmail = findViewById(R.id.email);
        btnSend = findViewById(R.id.btnSend);
        btnResend = findViewById(R.id.btnResend);
        btnConfirm = findViewById(R.id.btnConfirm);
        pinView = findViewById(R.id.firstPinView);
        layoutEmail = findViewById(R.id.LinearLayout_Email);
        layoutPinView = findViewById(R.id.LinearLayout_PinView);

        btnSend.setOnClickListener(view -> {
            handleSendOTP();
        });

        btnResend.setOnClickListener(view -> {
            handleReSendOTP();
        });

        btnConfirm.setOnClickListener(view -> {
            handleConfirm();
        });

    }

    private void handleConfirm() {
        String inputCode = pinView.getText().toString();
        String email = tvEmail.getText().toString();

        if (inputCode.equals(String.valueOf(code))) {
            Toast.makeText(ResetPasswordActivity.this,
                    "Xác thực thành công", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(ResetPasswordActivity.this, NewPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ResetPasswordActivity.this,
                    "Xác thực thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleSendOTP() {
        String email = edEmail.getText().toString();

        if (email.isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("Vui lòng điền đầy đủ thông tin.");
        } else {
            int otp = MailService.sendOtp(email);
            if (otp != 0) {
                code = otp;

                layoutEmail.setVisibility(View.GONE);
                layoutPinView.setVisibility(View.VISIBLE);
                tvEmail.setText(email);
                Toast.makeText(this,
                        "Mã OTP đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra khi gửi email", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void handleReSendOTP() {
        String email = tvEmail.getText().toString();
        int otp = MailService.sendOtp(email);
        if (otp != 0) {
            code = otp;
            Toast.makeText(this, "Đã gửi lại mã", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra khi gửi email", Toast.LENGTH_SHORT).show();
        }
    }

}