package com.geocomply.techx_app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.common.LoginSession;

public class AccountFragment extends Fragment {
    RelativeLayout profile, myOrder, favorite;
    Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profile = view.findViewById(R.id.profile);
        myOrder = view.findViewById(R.id.myOrder);
        favorite = view.findViewById(R.id.btnFavorite);
        btnLogout = view.findViewById(R.id.btnLogout);

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });

        myOrder.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Move to Order", Toast.LENGTH_SHORT).show();
        });

        favorite.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavoritesActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            handleLogoutDialog();
        });
    }

    // Handle display logout dialog
    @SuppressLint("SetTextI18n")
    private void handleLogoutDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_logout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvMessage = dialog.findViewById(R.id.message);
        Button btnYes = dialog.findViewById(R.id.btnSend);
        Button btnNo = dialog.findViewById(R.id.btnCancel);

        tvMessage.setText("Bạn có chắc muốn đăng xuất?");

        btnYes.setOnClickListener(view -> {
            LoginSession.clearSession();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }
}