package com.hdvideoplayer.smartplayer.player;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {
    private static volatile Pref mInstance;
    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public Pref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VideoPlayer", 0);
        this.appSharedPrefs = sharedPreferences;
        this.prefsEditor = sharedPreferences.edit();
    }

    public Pref() {
    }

    public static Pref getInstance() {
        if (mInstance == null) {
            synchronized (Pref.class) {
                if (mInstance == null) {
                    mInstance = new Pref();
                }
            }
        }
        return mInstance;
    }

    public void init(Context context) {
        if (context == null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("videoPlayer", 0);
            this.appSharedPrefs = sharedPreferences;
            this.prefsEditor = sharedPreferences.edit();
        }
        if (this.appSharedPrefs == null) {
            SharedPreferences sharedPreferences2 = context.getSharedPreferences("videoPlayer", 0);
            this.appSharedPrefs = sharedPreferences2;
            this.prefsEditor = sharedPreferences2.edit();
        }
    }

    public String getString(String str, String str2) {
        return this.appSharedPrefs.getString(str, str2);
    }

    public String getString(String str) {
        return this.appSharedPrefs.getString(str, "");
    }

    public void setString(String str, String str2) {
        this.prefsEditor.putString(str, str2).commit();
    }

}
