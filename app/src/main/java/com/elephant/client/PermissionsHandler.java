package com.elephant.client;

import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for handling permissions in the application.
 * It provides methods to set permissions, ask for permissions, and check if all permissions are granted.
 */
public class PermissionsHandler {

    private Context context;
    private String[] permissions = new String[] {};
    AppCompatActivity activity;

    private ActivityResultLauncher<String[]> requestLauncher;

    /**
     * Constructor for the PermissionsHandler class.
     * @param activity The activity from which to launch the permissions request
     * @param context The context to use for checking and requesting permissions
     */
    public PermissionsHandler(AppCompatActivity activity, Context context) {
        this.activity = activity;
        this.context = context;

    }

    /**
     * Registers the permissions with the ActivityResultLauncher.
     */
    public void registerPermissions() {
        requestLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
            AtomicBoolean permissionGranted = new AtomicBoolean(true);
            permissions.forEach((key, value) -> {
                if (Arrays.asList(this.permissions).contains(key) && !value) {
                    Toast.makeText(context,
                            key + " " + value,
                            Toast.LENGTH_SHORT).show();
                    permissionGranted.set(false);
                }
            });
            if (!permissionGranted.get()) {
                Toast.makeText(context,
                        "Permission request denied",
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * Sets the permissions that this handler should manage.
     * @param permissions An array of android.Manifest.permission permissions to manage
     */
    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    /**
     * Asks for the permissions that have been set.
     */
    public void askPermissions() {
        registerPermissions();
        requestLauncher.launch(permissions);
    }

    /**
     * Checks if all the permissions that have been set are granted.
     * @return true if all permissions are granted, false otherwise
     */
    public boolean allPermissionsGranted() {
        AtomicBoolean good = new AtomicBoolean(true);
        Arrays.asList(permissions).forEach(it -> {
            if (ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED) {
                good.set(false);
            }
        });
        return good.get();
    }
}