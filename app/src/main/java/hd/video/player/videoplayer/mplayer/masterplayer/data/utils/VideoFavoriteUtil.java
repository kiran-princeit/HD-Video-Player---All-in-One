package hd.video.player.videoplayer.mplayer.masterplayer.data.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashSet;
import java.util.Set;
import hd.video.player.videoplayer.mplayer.masterplayer.util.SharedPreferencesUtils;

public class VideoFavoriteUtil {
    public static Set<Long> getAllFavoriteVideoId(Context context) {
        String string = SharedPreferencesUtils.getString(context, "FAVORITE_VIDEO_ID", "");
        return TextUtils.isEmpty(string) ? new HashSet() : (Set) new Gson().fromJson(string, new TypeToken<Set<Long>>() {
        }.getType());
    }

    public static void setFavoriteVideoId(Context context, Set<Long> set) {
        putFavoriteVideoListId(context, set);
    }

    private static void putFavoriteVideoListId(Context context, Set<Long> set) {
        SharedPreferencesUtils.putString(context, "FAVORITE_VIDEO_ID", new Gson().toJson((Object) set, new TypeToken<Set<Long>>() {
        }.getType()));
    }

    public static void addFavoriteVideoId(Context context, long j, boolean z) {
        Set allFavoriteVideoId = getAllFavoriteVideoId(context);

        Log.d("videoFavUtil", "addFavoriteVideoId: "+allFavoriteVideoId);
        if (z) {
            allFavoriteVideoId.add(Long.valueOf(j));
        } else {
            allFavoriteVideoId.remove(Long.valueOf(j));
        }
        putFavoriteVideoListId(context, allFavoriteVideoId);
    }

    public static boolean checkFavoriteVideoIdExisted(Context context, long j) {
        return getAllFavoriteVideoId(context).contains(Long.valueOf(j));
    }
}
