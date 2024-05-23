package com.elephant.client.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.elephant.client.models.FolderStructure;
import com.elephant.client.models.ResourceFile;
import com.elephant.client.models.User;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
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
    User user;
    String currentFolder = "/";

    private static Network instance = null;


    public static enum RESULT_CODE {
        SUCCESS,
        BAD_CREDENTIALS,
        NETWORK_FAILURE,
        REQUEST_FAILURE,
        CONFLICT

    }

    private Network(User user) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new BasicAuthInterceptor(user.getUsername(), user.getPassword()))
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.103:8080")
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder()
                                .setLenient()
                                .create())
                )
                .client(client)
                .build();

        api = retrofit.create(API.class);

    }

    public static Network getInstance(User user) {

        instance = new Network(user);

        return instance;
    }

    public static Network getInstance() {
        if (instance == null) {
            return null;
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
        instance = new Network(user);
    }

    public void getFolders(Handler handler, Integer parentFolderID) {
        Call<FolderStructure> call = api.getFolders(parentFolderID);
        call.enqueue(new Callback<FolderStructure>() {
            @Override
            public void onResponse(Call<FolderStructure> call, Response<FolderStructure> response) {
                Message msg = new Message();
                if (response.code() == 200) {
                    msg.obj = response.body();
                    msg.what = RESULT_CODE.SUCCESS.ordinal();
                } else if (response.code() == 409) {
                    msg.what = RESULT_CODE.CONFLICT.ordinal();
                }
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<FolderStructure> call, Throwable t) {
                Message msg = new Message();
                msg.what = RESULT_CODE.NETWORK_FAILURE.ordinal();
                handler.sendMessage(msg);
            }
        });

    }

    public void createFolder(Handler handler, Integer parentID, String folderName) {
        Call<Integer> call = api.createFolder(parentID, folderName);

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                Message msg = new Message();
                if (response.code() == 200) {
                    msg.obj = response.body();
                    msg.what = RESULT_CODE.SUCCESS.ordinal();
                } else if (response.code() == 409) {
                    msg.what = RESULT_CODE.CONFLICT.ordinal();
                }
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Message msg = new Message();
                msg.what = RESULT_CODE.NETWORK_FAILURE.ordinal();
                handler.sendMessage(msg);
            }
        });
    }

    public void uploadFile(Handler handler, ResourceFile resourceFile, Integer parentID) {
        MultipartBody.Part mainFilePart = MultipartBody.Part.createFormData("file", resourceFile.getName(),
                RequestBody.create(MediaType.parse("multipart/form-data"), resourceFile.getBytes(), 0, resourceFile.getSizeInt()));

        MultipartBody fileBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("parent_id", parentID.toString())
                .addFormDataPart("name", resourceFile.getName())
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

    public void testCredentials(Handler handler) {
        Call<Boolean> call = api.testUserConnection();
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Message msg = handler.obtainMessage();
                if (response.isSuccessful()) {
                    msg.what = RESULT_CODE.SUCCESS.ordinal();
                } else if (response.code() == 401 || response.code() == 403) {
                    msg.what = RESULT_CODE.BAD_CREDENTIALS.ordinal();
                } else {
                    msg.what = RESULT_CODE.REQUEST_FAILURE.ordinal();
                }
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Message msg = handler.obtainMessage();
                msg.what = RESULT_CODE.NETWORK_FAILURE.ordinal();
                handler.sendMessage(msg);
            }
        });
    }


}
