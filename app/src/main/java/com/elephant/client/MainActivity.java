package com.elephant.client;

import static android.content.Intent.CATEGORY_OPENABLE;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.elephant.client.databinding.ActivityMainBinding;
import com.elephant.client.message.Message;
import com.elephant.client.message.Network;
import com.elephant.client.message.ResourceFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final int CHOOSE_FILE_REQUEST_CODE = 123;
    ActivityMainBinding binding;

    Handler handler;
    Network network = new Network();
    Integer msgID = 10;
    ResourceFile resourceFile = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //default things
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //ask permissions
        PermissionsHandler permissionsHandler = new PermissionsHandler(this, getApplicationContext());
        permissionsHandler.setPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        if(!permissionsHandler.allPermissionsGranted()) {
            permissionsHandler.askPermissions();
        }



        binding.chooseFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent()
                    .setType("*/*")
                    .addCategory(CATEGORY_OPENABLE)
                    .setAction(Intent.ACTION_GET_CONTENT);
            //TODO add possibility to choose multiple file via EXTRA_ALLOW_MULTIPLE

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        });

        binding.refreshButton.setOnClickListener(v -> {
            //network.getMessages(handler);
        });

        binding.sendFileBtn.setOnClickListener(v -> {
            if (resourceFile != null) {
                network.uploadFile(handler, resourceFile);
            }
            else {
                Toast.makeText(getApplicationContext(), "Choose file!", Toast.LENGTH_SHORT).show();
            }

        });

        handler = new Handler(Objects.requireNonNull(Looper.myLooper())) {
            @Override
            public void handleMessage(@NonNull android.os.Message msg) {
                super.handleMessage(msg);
                if (msg.obj != null && msg.what == 1) {
                    ArrayList<Message> messages;

                }

            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // result of file choose
        if(requestCode == CHOOSE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            //The uri with the location of the file

            Uri uri = data.getData();
            if (uri != null) {
                try {
                    resourceFile = new ResourceFile(data.getData(), getApplicationContext());
                }
                catch (IOException e) {
                    resourceFile = null;
                    Toast.makeText(getApplicationContext(), "Failed to retrieve file data!", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }


}