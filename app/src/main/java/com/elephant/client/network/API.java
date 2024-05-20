package com.elephant.client.network;


import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface API {

    @POST("/file")
    Call<String> uploadFile(@Body MultipartBody fileBody);

    @GET("user/test")
    Call<Boolean> testUserConnection();

}
