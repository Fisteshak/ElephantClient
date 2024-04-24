package com.elephant.client.message;

import android.os.Handler;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {
    private static final long READ_TIMEOUT_SECONDS = 5;
    private static final long WRITE_TIMEOUT_SECONDS = 5;
    private static final long CONNECTION_TIMEOUT_SECONDS = 5;
    Retrofit retrofit;
    API api;
    long id;
    public Network() {

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.102:8080")
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                                .setLenient()
                                .create())
                )
                .build();
        api = retrofit.create(API.class);

    }

    public void getMessages(Handler handler) {
        Call<List<Message>> call = api.getMessages();
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                android.os.Message msg = new android.os.Message();
                msg.obj = response.body();
                msg.what = 1;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.d("GET MESSAGES FAIL", t.toString());
            }
        });
    }



    public void uploadFile(Handler handler, ContentFile contentFile) {
        MultipartBody.Part mainFilePart = MultipartBody.Part.createFormData("file", contentFile.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), contentFile.getBytes(), 0, contentFile.getSizeInt()));

        MultipartBody fileBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("path", "testfolder")
                .addFormDataPart("name", contentFile.getName())
                .addPart(mainFilePart)
                .build();


        // Call the API to upload the file
        Call<String> call = api.uploadFile(fileBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("UPLOAD FILE CODE", "Response: " + response.body() + " Code: " + String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    Log.d("UPLOAD FILE SUCC", response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("UPLOAD FILE FAIL", t.toString());

            }
        });
        // Rest of your code
    }

    public void createMessage(Handler handler, Message message) {

        Call<Message> call = api.createMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                Log.d("CREATE MESSAGE", "CODE = " + response.code());
                if (response.isSuccessful()) {
                    android.os.Message msg = new android.os.Message();
                    msg.obj = response.body();
                    handler.sendMessage(msg);
                    Log.d("CREATE MESSAGE SUCC", response.toString());
                }
                else {

                }

            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Log.d("CREATE MESSAGE FAIL", t.toString());
            }
        });
    }
}
