package com.geocomply.techx_app.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
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
import com.geocomply.techx_app.ProductDetailActivity;
import com.geocomply.techx_app.R;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.Product;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<ShoppingCart> shoppingCartList;
    static Context context;

    public CartAdapter(Context context, List<ShoppingCart> shoppingCartList) {
        this.context = context;
        this.shoppingCartList = shoppingCartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ShoppingCart cart = shoppingCartList.get(position);
        if (cart == null) {
            return;
        }
        holder.bindData(cart);

        holder.cvCart.setOnClickListener(view -> {
            int productId = cart.getCartItems().get(0).getProdIdNavigation().getId();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", productId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.tvClearItem.setOnClickListener(v -> {
            displayClearDialog(v.getContext(), position);
        });
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
            deleteCartItem(context, position);
            shoppingCartList.remove(position);
            dialog.dismiss();
        });

        btnNo.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void deleteCartItem(Context context, int id) {
        Call<Void> delete = ApiService.apiService.deleteCartItem(id);

        delete.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context,
                            "Sản phẩm đã được xóa", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context,
                            "API response unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(context, "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return shoppingCartList != null ? shoppingCartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvSpecification, tvClearItem;
        ImageView productImage;
        CardView cvCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            tvProductName = itemView.findViewById(R.id.productName);
            tvPrice = itemView.findViewById(R.id.price);
            tvSpecification = itemView.findViewById(R.id.specifications);
            tvClearItem = itemView.findViewById(R.id.clearItem);
            cvCart = itemView.findViewById(R.id.cvCart);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(ShoppingCart shoppingCart) {
            Product p = shoppingCart.getCartItems().get(0).prodIdNavigation;
            String price = formatNumber((int) p.getDiscounted());

            tvProductName.setText(p.getName());
            tvSpecification.setText(p.getRam() + "/ " + p.getStorage() + "/ " + p.getColor());

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
