package com.hdvideoplayer.smartplayer.player.util.download;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public class FileDownloadUtils {
    public static String getDownloadPath(Context context) {
        try {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (externalStoragePublicDirectory != null) {
                return externalStoragePublicDirectory.getAbsolutePath();
            }
            externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            if (externalStoragePublicDirectory != null) {
                return externalStoragePublicDirectory.getAbsolutePath();
            }
            return getCacheDir(context);
        } catch (Exception unused) {
            return getCacheDir(context);
        }
    }

    public static String getMoviesPath(Context context) {
        try {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
            if (externalStoragePublicDirectory != null) {
                return externalStoragePublicDirectory.getAbsolutePath();
            }
            externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (externalStoragePublicDirectory != null) {
                return externalStoragePublicDirectory.getAbsolutePath();
            }
            return getCacheDir(context);
        } catch (Exception unused) {
            return getCacheDir(context);
        }
    }

    public static String getMusicPath(Context context) {
        try {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            if (externalStoragePublicDirectory != null) {
                return externalStoragePublicDirectory.getAbsolutePath();
            }
            externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (externalStoragePublicDirectory != null) {
                return externalStoragePublicDirectory.getAbsolutePath();
            }
            return getCacheDir(context);
        } catch (Exception unused) {
            return getCacheDir(context);
        }
    }

    public static String getCacheDir(Context context) {
        String str = "cache dir = ";
        try {
            File externalCacheDir;
            if (Environment.getExternalStorageState().equals("mounted")) {
                externalCacheDir = context.getExternalCacheDir();
                if (externalCacheDir == null || !externalCacheDir.exists()) {
                    externalCacheDir = getExternalCacheDirManual(context);
                }
            } else {
                externalCacheDir = null;
            }
            if (externalCacheDir == null) {
                externalCacheDir = context.getCacheDir();
                if (externalCacheDir == null || !externalCacheDir.exists()) {
                    externalCacheDir = getCacheDirManual(context);
                }
            }
            Log.w("aaa", externalCacheDir.getAbsolutePath());
            return externalCacheDir.getAbsolutePath();
        } catch (Throwable unused) {
            return "";
        }
    }

    private static File getExternalCacheDirManual(Context context) {
        File file = new File(new File(new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data"), context.getPackageName()), "cache");
        if (!file.exists()) {
            String str = "aaa";
            if (file.mkdirs()) {
                try {
                    new File(file, ".nomedia").createNewFile();
                } catch (IOException unused) {
                    Log.i(str, "Can't create \".nomedia\" file in application external cache directory");
                }
            } else {
                Log.w(str, "Unable to create external cache directory");
                return null;
            }
        }
        return file;
    }

    private static File getCacheDirManual(Context context) {
        return new File("/data/data/" + context.getPackageName() + "/cache");
    }
}
