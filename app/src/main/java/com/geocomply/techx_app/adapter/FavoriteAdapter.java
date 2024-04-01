package com.geocomply.techx_app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.geocomply.techx_app.FavoritesActivity;
import com.geocomply.techx_app.ProductDetailActivity;
import com.geocomply.techx_app.R;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private final List<Favorite> favoriteList;
    static Context context;

    public FavoriteAdapter(Context context, List<Favorite> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cat_item, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);
        if (favorite == null) {
            return;
        }
        holder.bindData(favorite);

        holder.cvProduct.setOnClickListener(view -> {
            int productId = favorite.getProdIdNavigation().getId();
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productId", productId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList != null ? favoriteList.size() : 0;
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvSpecification, tvDiscountPercent;
        ImageView productImage;
        CardView cvProduct;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.productName);
            tvPrice = itemView.findViewById(R.id.price);
            tvSpecification = itemView.findViewById(R.id.specifications);
            tvDiscountPercent = itemView.findViewById(R.id.discountPercent);
            productImage = itemView.findViewById(R.id.productImage);
            cvProduct = itemView.findViewById(R.id.cvProduct);
        }

        @SuppressLint("SetTextI18n")
        public void bindData(Favorite favorite) {
            Product product = favorite.getProdIdNavigation();
            String price = formatNumber((int) product.getDiscounted());
            long percent = discountPercent((int) product.getPrice(), (int) product.getDiscounted());

            tvProductName.setText(product.getName());
            tvSpecification.setText(product.getRam() + "/ " + product.getStorage());

            if (percent > 0) {
                tvDiscountPercent.setText("-" + percent + "%");
            } else {
                tvDiscountPercent.setVisibility(View.INVISIBLE);
            }

            if (product.getPrice() > 0 || product.getDiscounted() > 0) {
                tvPrice.setText(price + " ₫");
            } else {
                tvPrice.setText(0 + " ₫");
            }

            if (product.getImages() != null && !product.getImages().isEmpty()) {
                String imageUrl = product.getImages().get(0).getUrl();
                Glide.with(context).load(imageUrl).into(productImage);
            }
        }

        private String formatNumber(int number) {
            DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
            return decimalFormat.format(number);
        }

        private long discountPercent(int price, int discounted) {
            return Math.round(((float) (price - discounted) / price) * 100);
        }
    }
}
