package com.elephant.client.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.CATEGORY_OPENABLE;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.elephant.client.FolderPath;
import com.elephant.client.R;
import com.elephant.client.databinding.FragmentMainBinding;
import com.elephant.client.models.File;
import com.elephant.client.models.FolderStructure;
import com.elephant.client.models.FsObject;
import com.elephant.client.models.ResourceFile;
import com.elephant.client.network.Network;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainFragment extends Fragment
        implements FilesystemAdapter.OnFolderClickListener, FilesystemAdapter.OnFileDownloadBtnListener,
        FilesystemAdapter.OnFolderDeleteBtnListener, FilesystemAdapter.OnFileDeleteBtnListener {

    public static final int CHOOSE_FILE_REQUEST_CODE = 123;
    public static final int GET_FILE_REQUEST_CODE = 124;
    FragmentMainBinding binding;

    private byte[] saveFile;
    private String saveFileName;
    FilesystemAdapter adapter;
    Handler handler;
    Handler createFolderHandler;
    Handler deleteFileHandler;
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

    FolderPath folderPath = new FolderPath();
    private Handler refreshBtnHandler;
    private Handler getFileHandler;
    PopupWindow createObjectPopupWindow;
    PopupWindow createFolderPopupWindow;

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

        createFolderHandler = new Handler(msg -> {
            if (msg.what == Network.RESULT_CODE.SUCCESS.ordinal()) {
                Network.getInstance().getFolders(refreshBtnHandler, folderPath.getTopFolder().getId());
            }
            return false;
        });

        getFileHandler = new Handler(msg -> {
            if (msg.what == Network.RESULT_CODE.SUCCESS.ordinal()) {
                try (ResponseBody responseBody = ((Response<ResponseBody>) msg.obj).body()) {
                    saveFile = responseBody.bytes().clone();
                    Log.d("size0", String.valueOf(responseBody.contentLength()));

                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_TITLE, saveFileName);
                    startActivityForResult(intent, GET_FILE_REQUEST_CODE);

                } catch (Exception e) {
                    Log.e("mainFragment", "Failed to get file!", e);
                }

            } else if (msg.what == Network.RESULT_CODE.REQUEST_FAILURE.ordinal()) {
                Toast.makeText(getActivity().getApplicationContext(), "Failed to get file!", Toast.LENGTH_SHORT).show();
            }
            return false;
        });


        deleteFileHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == Network.RESULT_CODE.SUCCESS.ordinal()) {
                    Network.getInstance().getFolders(refreshBtnHandler, folderPath.getTopFolder().getId());
                }
                return false;
            }
        });

        binding.refreshBtn.setOnClickListener(v -> {

//            Network.getInstance().getFolders(refreshBtnHandler, folderPath.getTopFolder().getId());
            Network.getInstance().getFolders(refreshBtnHandler, folderPath.getRootFolder().getId());

        });

        binding.fab.setOnClickListener(v -> {
            showPopupWindow(v, inflater);

        });

        adapter = new FilesystemAdapter(this, this,this, this, refreshBtnHandler);
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
        if (requestCode == CHOOSE_FILE_REQUEST_CODE)
            createObjectPopupWindow.dismiss();

        // result of file choose
        if (requestCode == CHOOSE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            //The uri with the location of the file

            Uri uri = data.getData();
            if (uri != null) {
                try {
                    resourceFile = new ResourceFile(data.getData(), getActivity().getApplicationContext());
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

                        handler.postDelayed(runnableCode, 2000);

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Choose file!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    resourceFile = null;
                    Toast.makeText(getActivity().getApplicationContext(), "Failed to retrieve file data!", Toast.LENGTH_SHORT).show();
                }

            }

        } else if(requestCode == GET_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            //just as an example, I am writing a String to the Uri I received from the user:

            try {
                OutputStream output = getContext().getContentResolver().openOutputStream(uri);
                try {
                    Log.d("size1", String.valueOf(saveFile.length));
                    output.write(saveFile);
                } catch (Exception e) {
                    output.flush();
                    output.close();
                }

            }
            catch(IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }




    public void showPopupWindow(View view, LayoutInflater inflater) {
        // inflate the layout of the popup window

        View popupView = inflater.inflate(R.layout.add_object_popup_window, null);

        popupView.findViewById(R.id.choose_folder_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddFolderPopupWindow(view, inflater);
            }
        });


        popupView.findViewById(R.id.choose_file_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent()
                        .setType("*/*")
                        .addCategory(CATEGORY_OPENABLE)
                        .setAction(Intent.ACTION_GET_CONTENT);
                //TODO add possibility to choose multiple file via EXTRA_ALLOW_MULTIPLE

                startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);


            }
        });

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        createObjectPopupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        createObjectPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        binding.bacDimLayout.setVisibility(View.VISIBLE);


        createObjectPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                binding.bacDimLayout.setVisibility(View.GONE);
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                createObjectPopupWindow.dismiss();
                return true;
            }
        });
    }

    public void showAddFolderPopupWindow(View view, LayoutInflater inflater) {
        // inflate the layout of the popup window

        View popupView = inflater.inflate(R.layout.add_folder_popup_window, null);

        popupView.findViewById(R.id.create_folder_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folderName = ((android.widget.EditText) popupView.findViewById(R.id.folder_name_ed)).getText().toString();
                Network.getInstance().createFolder(createFolderHandler, folderPath.getTopFolder().getId(), folderName);
                createFolderPopupWindow.dismiss();
            }
        });

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        createFolderPopupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        createFolderPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        binding.bacDimLayout.setVisibility(View.VISIBLE);


        createFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                createObjectPopupWindow.dismiss();
                binding.bacDimLayout.setVisibility(View.GONE);
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                createFolderPopupWindow.dismiss();
                return true;
            }
        });
    }


    @Override
    public void onFolderDeleteBtnClick(int id) {
        Network.getInstance().deleteFolder(deleteFileHandler, id);

    }

    @Override
    public void onFileDeleteBtnClick(File file) {
        Network.getInstance().deleteFile(deleteFileHandler, file.getId());
    }

    @Override
    public void onFolderDownloadBtnClick(int id) {

        //binding.folderPathTv.setText(currentFolderID.toString());
        Network.getInstance().getFolders(refreshBtnHandler, id);
    }

    @Override
    public void onFileDownloadBtnClick(File file) {
        Network.getInstance().getFile(getFileHandler, file.getId());
        saveFileName = file.getName();
    }

}