package hd.video.player.videoplayer.mplayer.masterplayer.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.provider.MediaStore.Files;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.util.download.FileDownloadUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.thread.ThreadExecutor;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utility {
    public static String sCuttingVideoPath;
    public static String sHiddenVideoPath;
    public static String sMusicConvertPath;
    public static long sTimeExpires;
    public static String sVideoPath;

    public static void initFolderPath(Context context) {
        try {
            sHiddenVideoPath = FileDownloadUtils.getMoviesPath(context) + "/MyPlayer/HideVideo/";
            new File(sHiddenVideoPath).mkdirs();
            sMusicConvertPath = FileDownloadUtils.getMusicPath(context) + "/MyPlayer/MusicConvert/";
            new File(sMusicConvertPath).mkdirs();
            sCuttingVideoPath = FileDownloadUtils.getMoviesPath(context) + "/MyPlayer/VideoCutting/";
            new File(sCuttingVideoPath).mkdirs();
            sVideoPath = FileDownloadUtils.getMoviesPath(context) + "/MyPlayer/Video/";
            new File(sVideoPath).mkdirs();
        } catch (Exception unused) {
        }
    }

    public static String convertLongToTime(long j, String str) {
        return new SimpleDateFormat(str, Locale.ENGLISH).format(new Date(j));
    }

    public static String convertLongToDuration(long j) {
        String str;
        int i = (j > 3600000 ? 1 : (j == 3600000 ? 0 : -1));
        String str2 = "0";
        String str3 = ":";
        if (i > 0) {
            int i2 = (int) (j / 3600000);
            str = i2 < 10 ? i2 + str3 : i2 + str3;
        } else {
            str = "";
        }
        int i3 = ((int) (j / 60000)) % 60;
        if (i3 < 10) {
            str = str + str2 + i3 + str3;
        } else {
            str = str + i3 + str3;
        }
        int i4 = ((int) (j / 1000)) % 60;
        if (i4 < 10) {
            return str + str2 + i4;
        }
        return str + i4;
    }

    public static String convertSize(long j) {
        if (j < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            return j + " B";
        }
        if (j < PlaybackStateCompat.ACTION_SET_CAPTIONING_ENABLED) {
            return new DecimalFormat("##.## KB").format((double) (((float) j) / 1024.0f));
        }
        if (j < 1073741824) {
            return new DecimalFormat("##.## MB").format((double) (((float) j) / 1048576.0f));
        }
        return new DecimalFormat("##.## GB").format((double) (((float) j) / 1.07374182E9f));
    }

    public static int getVideoSortMode(Context context) {
        return SharedPreferencesUtils.getInt(context, "VIDEO_SORT_TYPE", 0);
    }

    public static int getMusicSortMode(Context context) {
        return SharedPreferencesUtils.getInt(context, "MUSIC_SORT_TYPE", 0);
    }

    public static boolean getVideoSortAscending(Context context) {
        return SharedPreferencesUtils.getBoolean(context, "VIDEO_SORT_ASCENDING", false);
    }

    public static boolean getMusicSortAscending(Context context) {
        return SharedPreferencesUtils.getBoolean(context, "MUSIC_SORT_ASCENDING", false);
    }

    public static void setVideoSortModeAndAscending(Context context, int i, boolean z) {
        SharedPreferencesUtils.putInt(context, "VIDEO_SORT_TYPE", i);
        SharedPreferencesUtils.putBoolean(context, "VIDEO_SORT_ASCENDING", z);
    }

    public static void setMusicSortModeAndAscending(Context context, int i, boolean z) {
        SharedPreferencesUtils.putInt(context, "MUSIC_SORT_TYPE", i);
        SharedPreferencesUtils.putBoolean(context, "MUSIC_SORT_ASCENDING", z);
    }

    public static List<VideoInfo> searchVideoByVideoName(List<VideoInfo> list, String str) {
        ArrayList arrayList = new ArrayList();
        for (VideoInfo videoInfo : list) {
            if (videoInfo.getDisplayName().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(videoInfo);
            }
        }
        return arrayList;
    }

    public static List<MusicInfo> searchMusicByMusicName(List<MusicInfo> list, String str) {
        ArrayList arrayList = new ArrayList();
        for (MusicInfo musicInfo : list) {
            if (musicInfo.getDisplayName().toLowerCase().contains(str.toLowerCase())) {
                arrayList.add(musicInfo);
            }
        }
        return arrayList;
    }

    public static void deleteAVideo(final Context context, VideoInfo videoInfo) {
        final String path = videoInfo.getPath();
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                Utility.deleteAVideo(path, context);
            }
        });
    }

    public static void deleteAVideo(String str, Context context) {
        String[] strArr = new String[]{str};
        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = Files.getContentUri("external");
        String str2 = "_data=?";
        contentResolver.delete(contentUri, str2, strArr);
        File file = new File(str);
        if (file.exists()) {
            contentResolver.delete(contentUri, str2, strArr);
        }
        Log.d("aaa", " deleteAVideo = " + file);
    }

    public static void deleteMusicFiles(final Context context, MusicInfo musicInfo) {
        final String path = musicInfo.getPath();
        ThreadExecutor.runOnDatabaseThread(new Runnable() {
            public final void run() {
                Utility.deleteAMusic(path, context);
            }
        });
    }

    public static void deleteAMusic(String str, Context context) {
        String[] strArr = new String[]{str};
        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = Files.getContentUri("external");
        String str2 = "_data=?";
        contentResolver.delete(contentUri, str2, strArr);
        File file = new File(str);
        if (file.exists()) {
            contentResolver.delete(contentUri, str2, strArr);
        }
        Log.d("aaa", " deleteAMusic = " + file);
    }

    public static void renameAVideo(Context context, VideoInfo videoInfo, String str, OnScanCompletedListener onScanCompletedListener) {
        Log.d("aaa", " renameAVideo = " + videoInfo.toString());
        File file = new File(videoInfo.getPath());
        File file2 = new File(file.getAbsolutePath());
        File file3 = new File(file.getAbsolutePath().replace(file.getName(), str));
        if (file3.exists()) {
            Toast.makeText(context, "File name already exists, please use another name", 0).show();
        } else if (file2.renameTo(file3)) {
            MediaScannerConnection.scanFile(context, new String[]{file3.getPath(), file2.getPath()}, null, onScanCompletedListener);
        } else {
            Toast.makeText(context, "Renaming failed", 0).show();
        }
    }

    public static void renameAMusic(Context context, MusicInfo musicInfo, String str, OnScanCompletedListener onScanCompletedListener) {
        Log.d("aaa", " renameAMusic = " + musicInfo.toString());
        File file = new File(musicInfo.getPath());
        File file2 = new File(file.getAbsolutePath());
        File file3 = new File(file.getAbsolutePath().replace(file.getName(), str));
        if (file3.exists()) {
            Toast.makeText(context, "File name already exists, please use another name", 0).show();
        } else if (file2.renameTo(file3)) {
            MediaScannerConnection.scanFile(context, new String[]{file3.getPath(), file2.getPath()}, null, onScanCompletedListener);
        } else {
            Toast.makeText(context, "Renaming failed", 0).show();
        }
    }


    public static void shareVideo(Context context, VideoInfo videoInfo) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("video/*");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(videoInfo.getPath())));
            intent.addFlags(1);
            context.startActivities(new Intent[]{Intent.createChooser(intent, "Share via")});
        } catch (Exception unused) {
            FirebaseAnalyticsUtils.putEventClick(context, "Log_error", "share_video_error");
        }
    }

    public static void shareMusic(Context context, MusicInfo musicInfo) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.setType("audio/*");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(musicInfo.getPath())));
            intent.addFlags(1);
            context.startActivities(new Intent[]{Intent.createChooser(intent, "Share via")});
        } catch (Exception unused) {
            FirebaseAnalyticsUtils.putEventClick(context, "Log_error", "share_video_error");
        }
    }
    public static int getColorAttr(Context context, int attributeIdOrColorResId) {
    TypedValue typedValue = new TypedValue();

    // Check if it's a theme attribute (R.attr)
    boolean isAttribute = context.getTheme().resolveAttribute(attributeIdOrColorResId, typedValue, true);

    // If it's an attribute and resolved correctly, return the color
    if (isAttribute && typedValue.resourceId != 0) {
        return ContextCompat.getColor(context, typedValue.resourceId);
    }

    // If it's not an attribute, treat it as a color resource
    try {
        return ContextCompat.getColor(context, attributeIdOrColorResId);
    } catch (Resources.NotFoundException e) {
        throw new Resources.NotFoundException("Resource ID #" + attributeIdOrColorResId + " could not be resolved.");
    }
}
    public static int getColorAttr(Context context, int attributeId, int defaultColorResId) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(attributeId, typedValue, true);

        if (!resolved || typedValue.resourceId == 0) {
            return ContextCompat.getColor(context, defaultColorResId); // Use a fallback color
        }

        return ContextCompat.getColor(context, typedValue.resourceId);
    }

}
