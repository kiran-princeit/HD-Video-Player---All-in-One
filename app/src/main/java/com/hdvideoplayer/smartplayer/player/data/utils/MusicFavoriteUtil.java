package com.hdvideoplayer.smartplayer.player.data.utils;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashSet;
import java.util.Set;
import com.hdvideoplayer.smartplayer.player.util.SharedPreferencesUtils;

public class MusicFavoriteUtil {
    public static Set<Long> getAllFavoriteMusicId(Context context) {
        String string = SharedPreferencesUtils.getString(context, "FAVORITE_MUSIC_ID", "");
        return TextUtils.isEmpty(string) ? new HashSet() : (Set) new Gson().fromJson(string, new TypeToken<Set<Long>>() {
        }.getType());
    }

    public static void setFavoriteMusicId(Context context, Set<Long> set) {
        putFavoriteMusicListId(context, set);
    }

    private static void putFavoriteMusicListId(Context context, Set<Long> set) {
        SharedPreferencesUtils.putString(context, "FAVORITE_MUSIC_ID", new Gson().toJson((Object) set, new TypeToken<Set<Long>>() {
        }.getType()));
    }

    public static void addFavoriteMusicId(Context context, long j, boolean z) {
        Set allFavoriteMusicId = getAllFavoriteMusicId(context);
        if (z) {
            allFavoriteMusicId.add(Long.valueOf(j));
        } else {
            allFavoriteMusicId.remove(Long.valueOf(j));
        }
        putFavoriteMusicListId(context, allFavoriteMusicId);
    }

    public static boolean checkFavoriteMusicIdExisted(Context context, long j) {
        return getAllFavoriteMusicId(context).contains(Long.valueOf(j));
    }
}
