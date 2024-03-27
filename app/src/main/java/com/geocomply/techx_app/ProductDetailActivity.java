package com.geocomply.techx_app;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.geocomply.techx_app.adapter.CommentAdapter;
import com.geocomply.techx_app.api.ApiService;
import com.geocomply.techx_app.common.LoginSession;
import com.geocomply.techx_app.model.CartItem;
import com.geocomply.techx_app.model.Comment;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.User;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    ImageView btnBack, btnCart, btnFavorite, productImage;
    Button btnSend;
    TextView tvProductName, tvBrand, tvProductId, tvPrice, tvScreen, tvCamera, tvOS, tvCPU, tvRAM,
            tvStorage, tvPin, tvColor, tvDiscountPercent, tvCounter, btnAddToCart, btnBuyNow, tvAverage;
    BadgeDrawable badgeDrawable;
    RecyclerView recyclerComment;
    CommentAdapter adapter;
    LoginSession session;

    @SuppressLint("MissingInflatedId")
    @OptIn(markerClass = ExperimentalBadgeUtils.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvProductName = findViewById(R.id.productName);
        tvBrand = findViewById(R.id.brandName);
        tvProductId = findViewById(R.id.productId);
        tvPrice = findViewById(R.id.price);
        tvScreen = findViewById(R.id.screen);
        tvCamera = findViewById(R.id.camera);
        tvOS = findViewById(R.id.operatingSystem);
        tvCPU = findViewById(R.id.cpu);
        tvRAM = findViewById(R.id.ram);
        tvStorage = findViewById(R.id.storage);
        tvPin = findViewById(R.id.pin);
        tvColor = findViewById(R.id.color);
        tvDiscountPercent = findViewById(R.id.discountPercent);
        tvCounter = findViewById(R.id.counter);
        tvAverage = findViewById(R.id.average);
        productImage = findViewById(R.id.productImage);

        btnBack = findViewById(R.id.btnBack);
        btnCart = findViewById(R.id.btnCart);
        btnFavorite = findViewById(R.id.btnFavorite);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnSend = findViewById(R.id.btnSend);
        recyclerComment = findViewById(R.id.recycler_comment);

        badgeDrawable = BadgeDrawable.create(this);
        badgeDrawable.setNumber(2);
        BadgeUtils.attachBadgeDrawable(badgeDrawable, btnCart, null);

        recyclerComment.setHasFixedSize(true);
        recyclerComment.setLayoutManager(new LinearLayoutManager(this));

        session = new LoginSession(getApplicationContext());
        String userEmail = LoginSession.getEmailKey();

        Intent intent1 = getIntent();
        int productId = intent1.getIntExtra("productId", -1);

        if (checkInternetPermission()) {
            if (productId != -1) {
                loadProductDetail(productId);
                loadCommentList(productId);
            }
        } else {
            Toast.makeText(this, "Vui lòng kểm tra kết nối mạng...", Toast.LENGTH_SHORT).show();
        }

        btnCart.setOnClickListener(view -> {
            if (userEmail != null) {
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnFavorite.setOnClickListener(view -> {
            btnFavorite.setImageResource(R.drawable.icon_heart_active);
            if (productId != -1) {
                handleAddToFavorite(productId);
            }
        });

        btnAddToCart.setOnClickListener(view -> {
            if (productId != -1) {
                handleAddToCart(productId);
            }
        });

        btnBuyNow.setOnClickListener(view -> {
            Toast.makeText(this, "Buy now", Toast.LENGTH_SHORT).show();
        });

        btnSend.setOnClickListener(view -> {
            showRatingDialog();
        });

        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    // Load product detail
    @SuppressLint("SetTextI18n")
    private void loadProductDetail(int productId) {
        Call<Product> productById = ApiService.apiService.getProduct(productId);

        productById.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product p = response.body();

                    if (p != null) {
                        String price = formatNumber((int) p.getDiscounted());
                        long percent = discountPercent((int) p.getPrice(), (int) p.getDiscounted());

                        tvProductName.setText(p.getName());
                        tvBrand.setText(p.getBrand());
                        tvProductId.setText(String.valueOf(p.getId()));
                        tvScreen.setText(p.getScreen());
                        tvCamera.setText(p.getCamera());
                        tvOS.setText(p.getOperatingSystem());
                        tvCPU.setText(p.getCpu());
                        tvRAM.setText(p.getRam());
                        tvStorage.setText(p.getStorage());
                        tvPin.setText(p.getPin());
                        tvColor.setText(p.getColor());

                        if (percent > 0) {
                            tvDiscountPercent.setText("-" + percent + "%");
                        } else {
                            tvDiscountPercent.setVisibility(View.INVISIBLE);
                        }

                        if (p.getPrice() > 0 || p.getDiscounted() > 0) {
                            tvPrice.setText(price + " ₫");
                        } else {
                            tvPrice.setText(0 + " ₫");
                        }

                        if (p.getImages() != null && !p.getImages().isEmpty()) {
                            String imageUrl = p.getImages().get(0).getUrl();
                            Glide.with(ProductDetailActivity.this).load(imageUrl).into(productImage);
                        }
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this,
                            "API response unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAddToCart(int productId) {
        String userId = LoginSession.getIdKey();
        CartItem cartItem = new CartItem(productId, 1);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);

        addCart(userId, items);
    }

    private void addCart(String userId, List<CartItem> items) {
        ShoppingCart shoppingCart = new ShoppingCart(Integer.parseInt(userId), items);
        Call<ShoppingCart> insert = ApiService.apiService.postShoppingCart(shoppingCart);

        insert.enqueue(new Callback<ShoppingCart>() {
            @Override
            public void onResponse(Call<ShoppingCart> call, Response<ShoppingCart> response) {
                if (response.isSuccessful()) {
                    if (userId.isEmpty()) {
                        Toast.makeText(ProductDetailActivity.this,
                                "Vui lòng đăng nhập để thực hiện chức năng!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this,
                                "Sản phẩm đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this,
                            "API response unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShoppingCart> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAddToFavorite(int productId) {
        String userId = LoginSession.getIdKey();

        Favorite favorite = new Favorite(Integer.parseInt(userId), productId);
        Call<Favorite> insert = ApiService.apiService.postFavorite(favorite);

        insert.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                if (response.isSuccessful()) {
                    if (userId.isEmpty()) {
                        Toast.makeText(ProductDetailActivity.this,
                                "Vui lòng đăng nhập để thực hiện chức năng!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this,
                                "Sản phẩm đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this,
                            "API response unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadCommentList(int productId) {
        Call<ArrayList<Comment>> comment = ApiService.apiService.getComments();

        comment.enqueue(new Callback<ArrayList<Comment>>() {
            @Override
            public void onResponse(Call<ArrayList<Comment>> call, Response<ArrayList<Comment>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Comment> comments = response.body();
                    if (comments != null) {
                        for (Comment c : comments) {
                            int id = c.getProductId();
                            int avg = c.getRating();
                            if (id == productId) {
                                tvCounter.setText("(" + comments.size() + " đánh giá)");
                                tvAverage.setText(String.valueOf(avg));

                                adapter = new CommentAdapter(comments);
                                recyclerComment.setAdapter(adapter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Comment>> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show rating dialog
    @SuppressLint("SetTextI18n")
    private void showRatingDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_rating_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView message = dialog.findViewById(R.id.message);
        RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
        EditText edComment = dialog.findViewById(R.id.comment);
        Button btnSend = dialog.findViewById(R.id.btnSend);
        ImageView btnCancel = dialog.findViewById(R.id.btnClose);

        message.setText("Hãy cho chúng tôi biết đánh giá của bạn về sản phẩm này!");

        btnSend.setOnClickListener(view -> {
            String comment = edComment.getText().toString();
            float rateCount = ratingBar.getRating();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (rateCount == 0) {
                    Toast.makeText(this, "Vui lòng chọn đánh giá!", Toast.LENGTH_SHORT).show();
                } else if (comment.isEmpty()) {
                    Toast.makeText(this,
                            "Vui lòng nhập nội dung đánh giá!", Toast.LENGTH_SHORT).show();
                } else {
                    insertComment(comment, (int) rateCount);
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void insertComment(String content, int rating) {
        Intent intent1 = getIntent();
        int productId = intent1.getIntExtra("productId", -1);
        String userId = LoginSession.getIdKey();
        
        Comment comment = new Comment(Integer.parseInt(userId), productId, content, rating);
        Call<Comment> insert = ApiService.apiService.postComment(comment);

        insert.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    if (userId.isEmpty()) {
                        Toast.makeText(ProductDetailActivity.this,
                                "Vui lòng đăng nhập để thực hiện bình luận!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProductDetailActivity.this,
                                "Đánh giá đã được lưu thành công!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProductDetailActivity.this,
                            "Đánh giá lưu không thành công!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.e("API_ERROR", "Error occurred: " + t.getMessage());
                Toast.makeText(ProductDetailActivity.this,
                        "Get API Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check internet permission
    public boolean checkInternetPermission() {
        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private long discountPercent(int price, int discounted) {
        return Math.round(((float) (price - discounted) / price) * 100);
    }

    private String formatNumber(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,###");
        return decimalFormat.format(number);
    }
}