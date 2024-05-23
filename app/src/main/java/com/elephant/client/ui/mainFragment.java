package com.elephant.client.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.CATEGORY_OPENABLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.elephant.client.FolderPath;
import com.elephant.client.R;
import com.elephant.client.databinding.FragmentMainBinding;
import com.elephant.client.models.FolderStructure;
import com.elephant.client.models.FsObject;
import com.elephant.client.models.ResourceFile;
import com.elephant.client.network.Network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainFragment extends Fragment implements FilesystemAdapter.OnItemClickListener {

    public static final int CHOOSE_FILE_REQUEST_CODE = 123;
    FragmentMainBinding binding;

    FilesystemAdapter adapter;
    Handler handler;
    Integer msgID = 10;
    ResourceFile resourceFile = null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USERNAME = "username";
    private static final String ARG_PASSWORD = "password";

    // TODO: Rename and change types of parameters
    private String username;
    private String password;


    // path should not have leading or trailing slashes e.g.
    // correct: "folder1/folder2"
    private Integer currentFolderID = 1;

    FolderPath folderPath = new FolderPath();
    private Handler refreshBtnHandler;

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
        args.putString(ARG_USERNAME, param1);
        args.putString(ARG_PASSWORD, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            password = getArguments().getString(ARG_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        binding = FragmentMainBinding.bind(view);



        //ask permissions
        //TODO move to main activity
//        PermissionsHandler permissionsHandler = new PermissionsHandler(this, view.getApplicationContext());
//        permissionsHandler.setPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
//
//        if(!permissionsHandler.allPermissionsGranted()) {
//            permissionsHandler.askPermissions();
//        }
        binding.accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_mainFragment_to_loginFragment);
            }
        });


        binding.chooseFileBtn.setOnClickListener(v -> {
            Intent intent = new Intent()
                    .setType("*/*")
                    .addCategory(CATEGORY_OPENABLE)
                    .setAction(Intent.ACTION_GET_CONTENT);
            //TODO add possibility to choose multiple file via EXTRA_ALLOW_MULTIPLE

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
        });


        binding.sendFileBtn.setOnClickListener(v -> {
            if (resourceFile != null) {
                Network.getInstance().uploadFile(handler, resourceFile, folderPath.getTopFolder().getId());

                Handler handler = new Handler();
                Runnable runnableCode = new Runnable() {
                    @Override
                    public void run() {
                        // refresh the list of files to see the uploaded file
                        Network.getInstance().getFolders(refreshBtnHandler, folderPath.getTopFolder().getId());
                    }
                };

// Delay the runnable task by posting through the handler
                handler.postDelayed(runnableCode, 2000);

            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Choose file!", Toast.LENGTH_SHORT).show();
            }
        });


        refreshBtnHandler = new Handler(msg -> {
            if (msg.what == Network.RESULT_CODE.SUCCESS.ordinal()) {
                FolderStructure folder = (FolderStructure) msg.obj;
                List<FsObject> o = new ArrayList<>();
                o.addAll(folder.getSubfolders());
                o.addAll(folder.getFiles());

                adapter.setObjects(o);
                folderPath.addFolder(folder.getParentFolder());

                binding.folderPathTv.setText(folderPath.getFullPath());
            }
            return false;
        });

        binding.refreshBtn.setOnClickListener(v -> {

//            Network.getInstance().getFolders(refreshBtnHandler, folderPath.getTopFolder().getId());
            Network.getInstance().getFolders(refreshBtnHandler, folderPath.getRootFolder().getId());

        });


        adapter = new FilesystemAdapter(this::onItemClick, refreshBtnHandler);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);


        // This callback is only called when MyFragment is at least started
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                folderPath.deleteFolder();
                Network.getInstance().getFolders(refreshBtnHandler, folderPath.getTopFolder().getId());

                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        Network.getInstance().getFolders(refreshBtnHandler, folderPath.getTopFolder().getId());

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // result of file choose
        if (requestCode == CHOOSE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            //The uri with the location of the file

            Uri uri = data.getData();
            if (uri != null) {
                try {
                    resourceFile = new ResourceFile(data.getData(), getActivity().getApplicationContext());
                } catch (IOException e) {
                    resourceFile = null;
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to retrieve file data!", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    @Override
    public void onItemClick(int id) {

        //binding.folderPathTv.setText(currentFolderID.toString());
        Network.getInstance().getFolders(refreshBtnHandler, id);

    }
}