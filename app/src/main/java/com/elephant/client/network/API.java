package com.elephant.client.network;


import com.elephant.client.models.FolderStructure;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {

    @POST("/fs/file")
    Call<String> uploadFile(@Body MultipartBody fileBody);

    @GET("user/test")
    Call<Boolean> testUserConnection();

    @GET("fs/folder")
    Call<FolderStructure> getFolders(@Query("id") Integer folderID);


}
