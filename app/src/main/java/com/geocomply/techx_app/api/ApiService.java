package com.geocomply.techx_app.api;

import com.geocomply.techx_app.model.Address;
import com.geocomply.techx_app.model.Comment;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.Log;
import com.geocomply.techx_app.model.Order;
import com.geocomply.techx_app.model.OrderDetail;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.User;
import com.geocomply.techx_app.model.Vendor;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    String apiUrl = "http://192.168.1.20/api/";

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC);

    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder().addInterceptor(interceptor);

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okBuilder.build())
            .build()
            .create(ApiService.class);

    @POST("Register")
    Call<User> register(@Body User user);
    @POST("Login")
    Call<User> loginUser(@Body User user);

    @GET("Users/id/{email}")
    Call<String> getUserId(@Path("email") String email);
    @GET("Users/{id}")
    Call<User> getUser(@Path("id") int id);
    @PUT("Users/{id}")
    Call<Void> putUser(@Path("id") int id, @Body User user);
    @DELETE("Users/{id}")
    Call<Void> deleteUser(@Path("id") int id);

    @GET("Products")
    Call<ArrayList<Product>> getProducts();
    @GET("Products/Vendor/{vendorId}")
    Call<ArrayList<Product>> getProductsByVendorId(@Path("vendorId") int vendorId);
    @GET("Products/{id}")
    Call<Product> getProduct(@Path("id") int id);

    @GET("Vendors")
    Call<ArrayList<Vendor>> getVendors();

    @POST("Comments")
    Call<Comment> postComment(@Body Comment comment);
    @GET("Comments")
    Call<ArrayList<Comment>> getComments();

    @POST("Favorites")
    Call<Favorite> postFavorite(@Body Favorite favorite);
    @GET("Favorites/User/{userId}")
    Call<ArrayList<Favorite>> getFavoritesByUserId(@Path("userId") int userId);
    @DELETE("Favorites/{id}")
    Call<Void> deleteFavorite(@Path("id") int id);

    @POST("ShoppingCarts")
    Call<ShoppingCart> postShoppingCart(@Body ShoppingCart shoppingCart);
    @GET("ShoppingCarts/User/{userId}")
    Call<ArrayList<ShoppingCart>> getShoppingCartsByUserId(@Path("userId") int userId);
    @DELETE("ShoppingCarts/User/{userId}")
    Call<Void> deleteShoppingCartsByUserId(@Path("userId") int userId);
    @DELETE("CartItems/{id}")
    Call<Void> deleteCartItem(@Path("id") int id);

    @POST("Orders")
    Call<Order> postOrder(@Body Order order);

    @GET("OrderDetails/User/{userId}")
    Call<ArrayList<OrderDetail>> getOrderDetailsByUserId(@Path("userId") int userId);
    @GET("OrderDetails/Order/{orderId}")
    Call<OrderDetail> getOrderDetailByOrderId(@Path("orderId") int orderId);

    @POST("Addresses")
    Call<Address> postAddress(@Body Address address);
    @GET("Addresses/User/{userId}")
    Call<Address> getAddressByUserId(@Path("userId") int userId);
    @PUT("Addresses/User/{userId}")
    Call<Void> putAddress(@Path("userId") int userId, @Body Address address);

    @POST("Logs")
    Call<Log> postLog(@Body Log log);
}
