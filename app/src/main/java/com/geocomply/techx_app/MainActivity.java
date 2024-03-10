package com.geocomply.techx_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.geocomply.techx_app.common.LoginSession;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements CartFragment.OnCartUpdateListener {
    BottomNavigationView bottomNav;
    BadgeDrawable badgeDrawable;
    LoginSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);

        session = new LoginSession(getApplicationContext());

        badgeDrawable = bottomNav.getOrCreateBadge(R.id.nav_cart);
        badgeDrawable.setVisible(false);
        setBadgeNumber(0);

        replaceFragment(new HomeFragment());
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.nav_category) {
                replaceFragment(new ProductFragment());
            } else if (id == R.id.nav_cart) {
                if (!isLoggedIn()) {
                    handleAlertDialog();
                    return false;
                }
                replaceFragment(new CartFragment());
            } else {
                if (!isLoggedIn()) {
                    handleAlertDialog();
                    return false;
                }
                replaceFragment(new AccountFragment());
            }
            return true;
        });
    }

    // Replace a fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    private boolean isLoggedIn() {
        String email = LoginSession.getEmailKey();
        if (email != null) {
            return true;
        }
        return false;
    }

    // Handle display logout dialog
    @SuppressLint("SetTextI18n")
    private void handleAlertDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_logout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvMessage = dialog.findViewById(R.id.message);
        Button btnYes = dialog.findViewById(R.id.btnSend);
        Button btnNo = dialog.findViewById(R.id.btnCancel);

        tvMessage.setText("Vui lòng đăng nhập để xem tài khoản");
        btnYes.setText("Đăng nhập");

        btnYes.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    // Set badge number
    private void setBadgeNumber(int count) {
        if (count > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(count);
        } else {
            badgeDrawable.setVisible(false);
            badgeDrawable.setNumber(0);
        }
    }

    @Override
    public void onCartUpdated(int cartSize) {
        setBadgeNumber(cartSize);
    }
}