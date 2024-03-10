package com.geocomply.techx_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geocomply.techx_app.adapter.CartAdapter;
import com.geocomply.techx_app.adapter.FavoriteAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.CartItem;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {
    TextView tvCounter, tvTotal;
    ImageView btnDelete;
    Button btnCheckout;
    RecyclerView recyclerCart;
    CartAdapter adapter;
    ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();

    public interface OnCartUpdateListener {
        void onCartUpdated(int cartSize);
    }

    private OnCartUpdateListener cartUpdateListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCounter = view.findViewById(R.id.counter);
        tvTotal = view.findViewById(R.id.total);
        btnDelete = view.findViewById(R.id.btnDelete);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        recyclerCart = view.findViewById(R.id.recycler_cart);

        recyclerCart.setHasFixedSize(true);
        recyclerCart.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (checkInternetPermission()) {
            loadCartList();
        } else {
            Toast.makeText(getActivity(), "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnCheckout.setOnClickListener(v -> {
            for (ShoppingCart cart : shoppingCarts) {
                for (CartItem item : cart.getCartItems()) {
                    if (item == null) {
                        btnCheckout.setEnabled(false);
                    } else {
                        Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        btnDelete.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Xóa toàn bộ giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            cartUpdateListener = (OnCartUpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCartUpdateListener");
        }
    }

    private void updateCartCount(int count) {
        if (cartUpdateListener != null) {
            cartUpdateListener.onCartUpdated(count);
        }
    }

    // Load cart list
    @SuppressLint("SetTextI18n")
    private void loadCartList() {
        String userId = LoginSession.getIdKey();
        Call<ArrayList<ShoppingCart>> cart = ApiService.apiService.getShoppingCartsByUserId(Integer.parseInt(userId));

        cart.enqueue(new Callback<ArrayList<ShoppingCart>>() {
            @Override
            public void onResponse(Call<ArrayList<ShoppingCart>> call, Response<ArrayList<ShoppingCart>> response) {
                if (response.isSuccessful()) {
                    shoppingCarts = response.body();

                    assert shoppingCarts != null;
                    int total = 0;
                    for (ShoppingCart cart : shoppingCarts) {
                        for (CartItem item : cart.getCartItems()) {
                            Product product = item.prodIdNavigation;
                            total += (int) (product.getDiscounted() * item.getAmount());
                        }
                    }
                    tvTotal.setText(formatNumber(total) + " ₫");
                    tvCounter.setText("Tất cả (" + shoppingCarts.size() + ") sản phẩm");

                    adapter = new CartAdapter(getContext(), shoppingCarts);
                    recyclerCart.setAdapter(adapter);

                    updateCartCount(shoppingCarts.size());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ShoppingCart>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(getActivity(), "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        return decimalFormat.format(number);
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}