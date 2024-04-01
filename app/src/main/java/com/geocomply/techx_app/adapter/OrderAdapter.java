package com.geocomply.techx_app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geocomply.techx_app.OrderDetailActivity;
import com.geocomply.techx_app.ProductDetailActivity;
import com.geocomply.techx_app.R;
import com.geocomply.techx_app.model.Order;
import com.geocomply.techx_app.model.OrderDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<OrderDetail> orderList;
    static Context context;

    public OrderAdapter(Context context, List<OrderDetail> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDetail order = orderList.get(position);
        if (order == null) {
            return;
        }
        holder.bindData(order);

        holder.btnSeeDetail.setOnClickListener(view -> {
            int orderId = order.getOrderIdNavigation().getId();
            Intent intent = new Intent(context, OrderDetailActivity.class);
            intent.putExtra("orderId", orderId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.btnReBuy.setOnClickListener(view -> {
            int productId = order.getProdIdNavigation().getId();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", productId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvTotal, tvAmount, tvOrderStatus, btnSeeDetail, btnReBuy;
        ImageView productImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.productName);
            tvTotal = itemView.findViewById(R.id.total);
            tvAmount = itemView.findViewById(R.id.amount);
            tvOrderStatus = itemView.findViewById(R.id.orderStatus);
            productImage = itemView.findViewById(R.id.productImage);
            btnSeeDetail = itemView.findViewById(R.id.btnSeeDetail);
            btnReBuy = itemView.findViewById(R.id.btnReBuy);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(OrderDetail order) {
            String total = formatNumber((int) order.getPrice() * order.getAmount());
            int status = order.getOrderIdNavigation().getStatus();

            tvProductName.setText(order.getProdIdNavigation().getName());
            tvAmount.setText(String.valueOf(order.getAmount()));

            if (order.getProdIdNavigation().getDiscounted() > 0) {
                tvTotal.setText(total + " ₫");
            } else {
                tvTotal.setText(0 + " ₫");
            }

            switch (status) {
                case 1:
                    tvOrderStatus.setText("Đang xử lý");
                    break;
                case 2:
                    tvOrderStatus.setText("Đang vận chuyển");
                    break;
                case 3:
                    tvOrderStatus.setText("Giao hàng thành công");
                    break;
                case 4:
                    tvOrderStatus.setText("Đã hủy");
                    break;
                default:
                    tvOrderStatus.setText("Chờ thanh toán");
                    break;
            }

            if (order.getProdIdNavigation().getImages() != null
                    && !order.getProdIdNavigation().getImages().isEmpty()) {
                String imageUrl = order.getProdIdNavigation().getImages().get(0).getUrl();
                Glide.with(context).load(imageUrl).into(productImage);
            }
        }

        private String formatNumber(int number) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
            return decimalFormat.format(number);
        }
    }
}
