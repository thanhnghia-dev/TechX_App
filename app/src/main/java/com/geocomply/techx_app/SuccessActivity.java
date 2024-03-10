package com.geocomply.techx_app;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SuccessActivity extends AppCompatActivity {
    Button btnOrder;
    TextView btnHome;
    ImageView btnClose;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        btnOrder = findViewById(R.id.btnMyOrder);
        btnHome = findViewById(R.id.btnBackToHome);
        btnClose = findViewById(R.id.btnClose);

        // Button back home
        btnHome.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Button my order
        btnOrder.setOnClickListener(view -> {
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//                finish();
        });

        // Button close
        btnClose.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Press back key
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        SuccessActivity.this.getOnBackPressedDispatcher().addCallback(this, callback);
    }

}