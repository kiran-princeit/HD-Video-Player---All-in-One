package com.hdvideoplayer.smartplayer.player.util;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashSet;
import java.util.Set;

public class CoinUtils {
    public static Set<Integer> getAllThemeRedeem(Context context) {
        String string = SharedPreferencesUtils.getString(context, "THEME_REDEEM", "");
        return TextUtils.isEmpty(string) ? new HashSet() : (Set) new Gson().fromJson(string, new TypeToken<Set<Integer>>() {
        }.getType());
    }

    public static void addThemeRedeem(Context context, int i) {
        Set<Integer> allThemeRedeem = getAllThemeRedeem(context);
        allThemeRedeem.add(Integer.valueOf(i));
        SharedPreferencesUtils.putString(context, "THEME_REDEEM", new Gson().toJson(allThemeRedeem, new TypeToken<Set<Integer>>() {
        }.getType()));
    }

    public static boolean checkThemeRedeem(Context context, int i) {
        return getAllThemeRedeem(context).contains(Integer.valueOf(i));
    }

    public static int getTotalCoin(Context context) {
        return SharedPreferencesUtils.getInt(context, "NUMBER_COIN", 0);
    }

    public static void setTotalCoin(Context context, int i) {
        SharedPreferencesUtils.putInt(context, "NUMBER_COIN", i);
    }

    public static void redeem7DayPremium(Context context) {
        setTimePremium(context, getTimePremium(context) + 604800000);
    }

    public static void setTimePremium(Context context, long j) {
        Utility.sTimeExpires = j;
        SharedPreferencesUtils.putLong(context, "TIME_PREMIUM", j);
    }

    public static long getTimePremium(Context context) {
        return Math.max(SharedPreferencesUtils.getLong(context, "TIME_PREMIUM", 0), System.currentTimeMillis());
    }

    public static boolean isPremium() {
        return Utility.sTimeExpires > System.currentTimeMillis();
    }

    public static boolean checkTimeShowFloatingButtonGift(Context context) {
        return System.currentTimeMillis() - SharedPreferencesUtils.getLong(context, "TIME_FLOATING_GIFT_BUTTON", 0) > 54000000;
    }

    public static void putTimeShowFloatingButtonGift(Context context) {
        SharedPreferencesUtils.putLong(context, "TIME_FLOATING_GIFT_BUTTON", System.currentTimeMillis());
    }
}
