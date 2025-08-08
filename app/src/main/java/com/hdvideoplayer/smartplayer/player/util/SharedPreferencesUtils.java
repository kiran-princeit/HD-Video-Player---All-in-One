package com.hdvideoplayer.smartplayer.player.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoHistory;

public class SharedPreferencesUtils {
    public static void putInt(Context context, String str, int i) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putInt(str, i);
        edit.apply();
    }

    public static void putString(Context context, String str, String str2) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public static void putFloat(Context context, String str, float f) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putFloat(str, f);
        edit.apply();
    }

    public static void putBoolean(Context context, String str, boolean z) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(str, z);
        edit.apply();
    }

    public static void putLong(Context context, String str, long j) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putLong(str, j);
        edit.apply();
    }

    public static int getInt(Context context, String str, int i) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences != null ? defaultSharedPreferences.getInt(str, i) : i;
    }

    public static String getString(Context context, String str, String str2) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences != null ? defaultSharedPreferences.getString(str, str2) : str2;
    }

    public static float getFloat(Context context, String str, float f) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences != null ? defaultSharedPreferences.getFloat(str, f) : f;
    }

    public static boolean getBoolean(Context context, String str, boolean z) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences != null ? defaultSharedPreferences.getBoolean(str, z) : false;
    }

    public static long getLong(Context context, String str, long j) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return defaultSharedPreferences != null ? defaultSharedPreferences.getLong(str, j) : j;
    }
    public static List<VideoHistory> getHistoryList(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences("video_prefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(key, "[]");  // Default to empty list
        Type type = new TypeToken<List<VideoHistory>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void putHistoryList(Context context, String key, List<VideoHistory> videoHistoryList) {
        SharedPreferences preferences = context.getSharedPreferences("video_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(videoHistoryList);
        editor.putString(key, json);
        editor.apply();
    }


}
