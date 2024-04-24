package com.elephant.client.message;


import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface API {
    @GET("/message")
    Call<List<Message>> getMessages();

    @POST("/message")
    Call <Message> createMessage(@Body Message message);

    @POST("/file")
    Call<String> uploadFile(@Body MultipartBody fileBody);

}
