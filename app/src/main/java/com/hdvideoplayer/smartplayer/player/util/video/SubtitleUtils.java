package com.hdvideoplayer.smartplayer.player.util.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.text.TextUtils;
import androidx.documentfile.provider.DocumentFile;
import com.google.android.exoplayer2.util.MimeTypes;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class SubtitleUtils {
    public static Uri convertToUTF(Context context, Uri uri) {
        return uri;
    }

    public static void cacheSubtitleFileUri(Context context, long j, String str) {
        Editor edit = context.getSharedPreferences("SUBTITLE_URI", 0).edit();
        edit.putString(String.valueOf(j), str);
        edit.apply();
    }

    public static String getSubtitleFileUri(Context context, long j) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SUBTITLE_URI", 0);
        String str = "";
        return sharedPreferences != null ? sharedPreferences.getString(String.valueOf(j), str) : str;
    }

    public static String getSubtitleMime(Uri uri) {
        String path = uri.getPath();
        String str = "application/x-subrip";
        if (TextUtils.isEmpty(path)) {
            return str;
        }
        if (path.endsWith(".ssa") || path.endsWith(".ass")) {
            return MimeTypes.TEXT_SSA;
        }
        if (path.endsWith(".vtt")) {
            return MimeTypes.TEXT_VTT;
        }
        return (path.endsWith(".ttml") || path.endsWith(".xml") || path.endsWith(".dfxp")) ? MimeTypes.APPLICATION_TTML : str;
    }

    public static String getSubtitleLanguage(Uri uri) {
        String path = uri.getPath();
        if (!TextUtils.isEmpty(path) && path.endsWith(".srt")) {
            String str = ".";
            int lastIndexOf = path.lastIndexOf(str);
            int i = lastIndexOf;
            int i2 = i;
            while (i >= 0) {
                i2 = path.indexOf(str, i);
                if (i2 != lastIndexOf) {
                    break;
                }
                i--;
            }
            int i3 = lastIndexOf - i2;
            if (i3 >= 2 && i3 <= 6) {
                return path.substring(i2 + 1, lastIndexOf);
            }
        }
        return "en";
    }

    public static DocumentFile findUriInScope(Context context, Uri uri, Uri uri2) {
        DocumentFile fromTreeUri = DocumentFile.fromTreeUri(context, uri);
        String[] trailFromUri = getTrailFromUri(uri);
        String[] trailFromUri2 = getTrailFromUri(uri2);
        int i = 0;
        while (i < trailFromUri2.length) {
            if (i >= trailFromUri.length) {
                fromTreeUri = fromTreeUri.findFile(trailFromUri2[i]);
                if (fromTreeUri == null) {
                    return null;
                }
            } else if (!trailFromUri[i].equals(trailFromUri2[i])) {
                return null;
            }
            i++;
            if (i == trailFromUri2.length) {
                return fromTreeUri;
            }
        }
        return null;
    }

    public static DocumentFile findDocInScope(DocumentFile documentFile, DocumentFile documentFile2) {
        for (DocumentFile documentFile3 : documentFile.listFiles()) {
            DocumentFile documentFile32 = null;
            if (documentFile32.isDirectory()) {
                documentFile32 = findDocInScope(documentFile32, documentFile2);
                if (documentFile32 != null) {
                    return documentFile32;
                }
            } else if (documentFile2.length() == documentFile32.length() && documentFile2.getName().equals(documentFile32.getName())) {
                return documentFile32;
            }
        }
        return null;
    }

    public static String[] getTrailFromUri(Uri uri) {
        String[] split = uri.getPath().split(":");
        return split.length > 1 ? split[1].split("/") : new String[0];
    }

    private static String getFileBaseName(String str) {
        String str2 = ".";
        return str.indexOf(str2) > 0 ? str.substring(0, str.lastIndexOf(str2)) : str;
    }

    public static DocumentFile findSubtitle(DocumentFile documentFile) {
        return findSubtitle(documentFile, documentFile.getParentFile());
    }

    public static DocumentFile findSubtitle(DocumentFile documentFile, DocumentFile documentFile2) {
        String fileBaseName = getFileBaseName(documentFile.getName());
        if (documentFile2 != null && documentFile2.isDirectory()) {
            ArrayList arrayList = new ArrayList();
            DocumentFile[] listFiles = documentFile2.listFiles();
            int length = listFiles.length;
            int i = 0;
            int i2 = i;
            while (i < length) {
                DocumentFile documentFile3 = listFiles[i];
                if (isSubtitleFile(documentFile3)) {
                    arrayList.add(documentFile3);
                }
                if (isVideoFile(documentFile3)) {
                    i2++;
                }
                i++;
            }
            if (i2 == 1 && arrayList.size() == 1) {
                return (DocumentFile) arrayList.get(0);
            }
            if (arrayList.size() >= 1) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    DocumentFile documentFile4 = (DocumentFile) it.next();
                    if (documentFile4.getName().startsWith(fileBaseName + '.')) {
                        return documentFile4;
                    }
                }
            }
        }
        return null;
    }

    public static boolean isVideoFile(DocumentFile documentFile) {
        return documentFile.isFile() && documentFile.getType().startsWith("video/");
    }

    public static boolean isSubtitleFile(DocumentFile documentFile) {
        if (!documentFile.isFile()) {
            return false;
        }
        String name = documentFile.getName();
        if (name.endsWith(".srt") || name.endsWith(".ssa") || name.endsWith(".ass") || name.endsWith(".vtt") || name.endsWith(".ttml")) {
            return true;
        }
        return false;
    }

    public static void clearCache(Context context) {
        try {
            for (File file : context.getCacheDir().listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
