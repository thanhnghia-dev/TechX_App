package com.geocomply.techx_app;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geocomply.techx_app.adapter.OrderAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.OrderDetail;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveringOrderFragment extends Fragment {
    LinearLayout emptyOrder;
    RecyclerView recyclerOrder;
    OrderAdapter adapter;
    LoginSession session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivering_order, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyOrder = view.findViewById(R.id.emptyOrder);
        recyclerOrder = view.findViewById(R.id.recycler_order);

        recyclerOrder.setHasFixedSize(true);
        recyclerOrder.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        session = new LoginSession(getContext());
        String userId = LoginSession.getIdKey();

        if (checkInternetPermission()) {
            loadOrderList(Integer.parseInt(userId));
        } else {
            Toast.makeText(getActivity(), "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

    }

    // Load order list
    private void loadOrderList(int userId) {
        Call<ArrayList<OrderDetail>> order = ApiService.apiService.getOrderDetailsByStatus(2, userId);

        order.enqueue(new Callback<ArrayList<OrderDetail>>() {
            @Override
            public void onResponse(Call<ArrayList<OrderDetail>> call, Response<ArrayList<OrderDetail>> response) {
                if (response.isSuccessful()) {
                    ArrayList<OrderDetail> orderDetails = response.body();
                    if (orderDetails != null && !orderDetails.isEmpty()) {
                        adapter = new OrderAdapter(getContext(), orderDetails);
                        recyclerOrder.setAdapter(adapter);
                    } else {
                        emptyOrder.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<OrderDetail>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(getActivity(), "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}