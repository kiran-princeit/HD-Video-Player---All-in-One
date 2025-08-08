package com.hdvideoplayer.smartplayer.player.util.video;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;

public class VideoPlayerUtils {
    public static void log(String str) {
    }

    public static int dpToPx(int i) {
        return (int) (((float) i) * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getFileName(Context context, Uri uri) {
        String str = "_display_name";
        String str2 = null;
        try {
            if ("content".equals(uri.getScheme())) {
                Cursor query = context.getContentResolver().query(uri, new String[]{str}, null, null, null);
                if (query != null && query.moveToFirst()) {
                    int columnIndex = query.getColumnIndex(str);
                    if (columnIndex > -1) {
                        str2 = query.getString(columnIndex);
                    }
                }
                if (query != null) {
                    query.close();
                }
            }
            if (str2 == null) {
                str2 = uri.getPath();
                int lastIndexOf = str2.lastIndexOf(47);
                if (lastIndexOf != -1) {
                    str2 = str2.substring(lastIndexOf + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str2 == null ? "" : str2;
    }
}
