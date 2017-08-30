package com.mobileappsprn.alldealership.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by FuGenX-10 on 26-07-2016.
 */
public class PermissionUtil {


    private static final int MY_PERMISSION_LOCATION = 123;
    private static final int MY_PERMISSION_CAMERA = 111;
    private static final int REQUEST_EXTERNAL_STORAGE = 112;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void askForCameraPermission(Activity activityRef) {
        int permission = ActivityCompat.checkSelfPermission(activityRef, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            hasCameraPermissionInManifest(activityRef);
        } else {
           // CommonUtil.toastMethod(activityRef, "Your Camera Permission is granted for this Device.");

        }
    }

    public static void hasCameraPermissionInManifest(Activity activityRef) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(activityRef, Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                try {
                    if (activityRef.checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        activityRef.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSION_CAMERA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void askForLocationPermission(Activity activityRef) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && activityRef.checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && activityRef.checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activityRef.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_LOCATION);

            } else {
                 //LocationUtil.getCurrentLatLangOfDevice(activityRef);
            }
        } else {
            //LocationUtil.getCurrentLatLangOfDevice(activityRef);
        }
    }

    public static void askForStoragePermissions(Activity activityRef) {
        // Check if we have write permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(activityRef, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activityRef,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    public static void hasPermissionInManifest(Activity activityRef) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(activityRef, Manifest.permission.CAMERA);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                try {
                    if (activityRef.checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {

                        activityRef.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSION_CAMERA);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }


}
