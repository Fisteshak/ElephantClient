package com.elephant.client;

import static android.content.Intent.CATEGORY_OPENABLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elephant.client.databinding.ActivityMainBinding;
import com.elephant.client.message.Message;
import com.elephant.client.message.Network;
import com.elephant.client.message.ResourceFile;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainFragment extends Fragment {

    public static final int CHOOSE_FILE_REQUEST_CODE = 123;
    ActivityMainBinding binding;

    Handler handler;
    Network network = new Network();
    Integer msgID = 10;
    ResourceFile resourceFile = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mainFragment newInstance(String param1, String param2) {
        mainFragment fragment = new mainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        //ask permissions
        //TODO move to main activity
//        PermissionsHandler permissionsHandler = new PermissionsHandler(this, view.getApplicationContext());
//        permissionsHandler.setPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
//
//        if(!permissionsHandler.allPermissionsGranted()) {
//            permissionsHandler.askPermissions();
//        }


        view.findViewById(R.id.chooseFileBtn).setOnClickListener(v -> {
            Intent intent = new Intent()
                    .setType("*/*")
                    .addCategory(CATEGORY_OPENABLE)
                    .setAction(Intent.ACTION_GET_CONTENT);
            //TODO add possibility to choose multiple file via EXTRA_ALLOW_MULTIPLE

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        });

        view.findViewById(R.id.refreshButton).setOnClickListener(v -> {


        });

        view.findViewById(R.id.sendFileBtn).setOnClickListener(v -> {
            if (resourceFile != null) {
                network.uploadFile(handler, resourceFile);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Choose file!", Toast.LENGTH_SHORT).show();
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
        return view;
    }
}