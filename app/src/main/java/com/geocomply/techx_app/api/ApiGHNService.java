package com.geocomply.techx_app.api;

import com.geocomply.techx_app.model.Address;
import com.geocomply.techx_app.model.Comment;
import com.geocomply.techx_app.model.Favorite;
import com.geocomply.techx_app.model.Product;
import com.geocomply.techx_app.model.ShoppingCart;
import com.geocomply.techx_app.model.User;
import com.geocomply.techx_app.model.Vendor;
import com.geocomply.techx_app.model.logistic.DistrictData;
import com.geocomply.techx_app.model.logistic.ProvinceData;
import com.geocomply.techx_app.model.logistic.WardData;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiGHNService {
    String apiUrl = "https://online-gateway.ghn.vn/shiip/public-api/master-data/";
    String token = "2e8e9a29-b5e2-11ed-bcba-eac62dba9bd9";

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BASIC);

    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder().addInterceptor(interceptor);

    ApiGHNService apiService = new Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okBuilder.build())
            .build()
            .create(ApiGHNService.class);

    @GET("province")
    Call<ProvinceData> getProvinces(@Header("Token") String token);

    @GET("district")
    Call<DistrictData> getDistricts(@Header("Token") String token);

    @GET("ward")
    Call<WardData> getWards(@Header("Token") String token, @Query("district_id") int districtId);

}
