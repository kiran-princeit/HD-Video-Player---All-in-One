package hd.video.player.videoplayer.mplayer.masterplayer.util;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.google.firebase.analytics.FirebaseAnalytics.Param;

public class FirebaseAnalyticsUtils {
    public static final String EVENT_PROX_MUSIC_HISTORY = "prox_music_history";
    public static final String EVENT_PROX_MUSIC_HISTORY_MORE = "prox_music_history_more_icon";
    public static final String EVENT_PROX_MUSIC_LAYOUT = "prox_music_layout";
    public static final String EVENT_PROX_MUSIC_MORE = "prox_music_more_icon";
    public static final String EVENT_PROX_MUSIC_PLAYER = "prox_music_player";
    public static final String EVENT_PROX_MUSIC_PLAYLIST = "prox_music_playlist";
    public static final String EVENT_PROX_MUSIC_PLAYLIST_MORE = "prox_music_playlist_more_icon";
    public static final String EVENT_PROX_VIDEO_FOLDER = "prox_video_folder";
    public static final String EVENT_PROX_VIDEO_FOLDER_MORE = "prox_video_folder_more_icon";
    public static final String EVENT_PROX_VIDEO_HISTORY = "prox_video_history";
    public static final String EVENT_PROX_VIDEO_MORE = "prox_video_more_icon";


    public static void putEventClick(Context context, String str, String str2) {
        FirebaseAnalytics instance = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("event_type", str2);
        instance.logEvent(str, bundle);
    }

    public static void putScreenChecking(Context context, String str) {
        FirebaseAnalytics instance = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString(Param.SCREEN_NAME, str);
        bundle.putString(Param.SCREEN_CLASS, str);
        instance.logEvent(Event.SCREEN_VIEW, bundle);
    }

    public static void putEventPlay(Context context, String str, String str2, String str3) {
        FirebaseAnalytics instance = FirebaseAnalytics.getInstance(context);
        Bundle bundle = new Bundle();
        bundle.putString("file_name", str2);
        bundle.putString("file_extension", str3);
        bundle.putString("event_type", "OnCreate");
        instance.logEvent(str, bundle);
    }

}
