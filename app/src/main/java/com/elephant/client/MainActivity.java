package com.elephant.client;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {




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



    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // result of file choose
//        if(requestCode == CHOOSE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
//            //The uri with the location of the file
//
//            Uri uri = data.getData();
//            if (uri != null) {
//                try {
//                    resourceFile = new ResourceFile(data.getData(), getApplicationContext());
//                }
//                catch (IOException e) {
//                    resourceFile = null;
//                    Toast.makeText(getApplicationContext(), "Failed to retrieve file data!", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//        }
//    }


}