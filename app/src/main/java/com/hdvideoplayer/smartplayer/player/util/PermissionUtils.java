package com.hdvideoplayer.smartplayer.player.util;

import android.content.Context;
import android.os.Build.VERSION;

import androidx.core.content.ContextCompat;

public class PermissionUtils {
    public static final String[] PERMISSTIONSAbove = new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_AUDIO", "android.permission.READ_MEDIA_VIDEO", "android.permission.POST_NOTIFICATIONS"};
    public static final String[] PERMISSTIONSBelow = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"};

    public static boolean checkStoragePermission(Context context) {
        if (VERSION.SDK_INT >= 33) {
            return checkAbove33Permission(context);
        }
        return checkBelow33Permission(context);
    }

    private static boolean checkBelow33Permission(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    private static boolean checkAbove33Permission(Context context) {
        return ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_IMAGES") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_VIDEO") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.READ_MEDIA_AUDIO") == 0 && ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") == 0;
    }
}
