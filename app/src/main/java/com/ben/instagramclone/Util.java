package com.ben.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

public class Util {

    public static <T> void navigateTo(Class<T> clazz, Context context) {
        context.startActivity(new Intent(context, clazz));
    }

    public static void askPermission(String permission, AppCompatActivity activity, int requestCode) {

        // Check permission
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            //Ask for permission
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

}
