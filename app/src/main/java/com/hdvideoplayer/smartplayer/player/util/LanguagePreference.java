package com.hdvideoplayer.smartplayer.player.util;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguagePreference {
    private static final String PREF_NAME = "language_pref";
    private static final String KEY_LANGUAGE = "language";

    private static final String PREFS_NAME = "app_preferences";

    private static final String KEY_FIRST_TIME = "LanguageFirstTime";
    private static final String KEY_ONBOARDING_SHOWN = "OnboardingShown";

    public static void saveLanguage(Context context, String languageCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply();
    }

    public static String getLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public static boolean isFirstTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_FIRST_TIME, true);
    }

    public static void setFirstTime(Context context, boolean isFirstTime) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_FIRST_TIME, isFirstTime);
        editor.apply();
    }


    public static boolean isOnboardingShown(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_ONBOARDING_SHOWN, false);
    }


    public static void setOnboardingShown(Context context, boolean isShown) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ONBOARDING_SHOWN, isShown);
        editor.apply();
    }
}
