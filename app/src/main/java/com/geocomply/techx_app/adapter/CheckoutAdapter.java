package com.geocomply.techx_app.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geocomply.techx_app.R;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CartViewHolder> {
    private final List<ShoppingCart> shoppingCartList;
    static Context context;

    public CheckoutAdapter(Context context, List<ShoppingCart> shoppingCartList) {
        this.context = context;
        this.shoppingCartList = shoppingCartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkout_item, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bindData(shoppingCartList.get(position));
    }

    @SuppressLint("SetTextI18n")
    private void displayClearDialog(Context context, int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_clear_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvMessage = dialog.findViewById(R.id.message);
        Button btnYes = dialog.findViewById(R.id.btnSend);
        Button btnNo = dialog.findViewById(R.id.btnCancel);

        tvMessage.setText("Bạn có muốn xóa sản phẩm khỏi giỏ hàng?");

        btnYes.setOnClickListener(view -> {
            shoppingCartList.remove(position);
            Toast.makeText(context, "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return shoppingCartList != null ? shoppingCartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvSpecification, tvAmount;
        ImageView productImage;
        CardView cvCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            tvProductName = itemView.findViewById(R.id.productName);
            tvPrice = itemView.findViewById(R.id.price);
            tvSpecification = itemView.findViewById(R.id.specifications);
            tvAmount = itemView.findViewById(R.id.amount);
            cvCart = itemView.findViewById(R.id.cvCart);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(ShoppingCart shoppingCart) {
            Product p = shoppingCart.getCartItems().get(0).prodIdNavigation;
            String price = formatNumber((int) p.getDiscounted());

            tvProductName.setText(p.getName());
            tvSpecification.setText(p.getRam() + "/ " + p.getStorage() + "/ " + p.getColor());
            tvAmount.setText("SL: x" + shoppingCart.getCartItems().get(0).amount);

            if (p.getPrice() != 0 || p.getDiscounted() != 0) {
                tvPrice.setText(price + " ₫");
            } else {
                tvPrice.setText(0 + " ₫");
            }

            if (p.getImages() != null && !p.getImages().isEmpty()) {
                String imageUrl = p.getImages().get(0).getUrl();
                Glide.with(context).load(imageUrl).into(productImage);
            }
        }

        private String formatNumber(int number) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
            return decimalFormat.format(number);
        }
    }
}
