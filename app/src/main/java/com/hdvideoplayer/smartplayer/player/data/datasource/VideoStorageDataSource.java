package com.hdvideoplayer.smartplayer.player.data.datasource;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Video.Media;
import android.util.Log;
import android.util.LongSparseArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoFolder;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoSubtitle;

public class VideoStorageDataSource {
    private final Context mContext;

    public VideoStorageDataSource(Context context) {
        this.mContext = context;
    }
    public List<VideoInfo> getAllVideoFromStorage() {
        Uri contentUri;
        Exception exception = null;
        LongSparseArray<VideoInfo> longSparseArray = new LongSparseArray<>();
        HashSet<VideoInfo> hashSet = new HashSet<>();
        String[] strArr = new String[]{"_id", "_data", "_display_name", "duration", "_size", "resolution", "date_modified", "mime_type"};

        // Choose content URI based on SDK version
        if (VERSION.SDK_INT >= 29) {
            contentUri = Media.getContentUri("external");
        } else {
            contentUri = Media.EXTERNAL_CONTENT_URI;
        }

        // Query the media store
        Cursor query = this.mContext.getContentResolver().query(contentUri, strArr, null, null, "date_modified DESC");

        if (query != null) {
            try {
                // Column indices
                int columnIndexOrThrow = query.getColumnIndexOrThrow("_id");
                int columnIndexOrThrow2 = query.getColumnIndexOrThrow("_data");
                int columnIndexOrThrow3 = query.getColumnIndexOrThrow("_display_name");
                int columnIndexOrThrow4 = query.getColumnIndexOrThrow("duration");
                int columnIndexOrThrow5 = query.getColumnIndexOrThrow("_size");
                int columnIndexOrThrow6 = query.getColumnIndexOrThrow("resolution");
                int columnIndexOrThrow7 = query.getColumnIndexOrThrow("date_modified");
                int columnIndexOrThrow8 = query.getColumnIndexOrThrow("mime_type");

                // Iterate over each row
                while (query.moveToNext()) {
                    VideoInfo videoInfo = new VideoInfo();
                    long j = query.getLong(columnIndexOrThrow);
                    videoInfo.setId(j);
                    videoInfo.setUri(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, j).toString());
                    String path = query.getString(columnIndexOrThrow2);
                    videoInfo.setPath(path);
                    videoInfo.setDisplayName(query.getString(columnIndexOrThrow3));
                    videoInfo.setDuration(query.getLong(columnIndexOrThrow4));
                    videoInfo.setSize(query.getLong(columnIndexOrThrow5));
                    videoInfo.setResolution(query.getString(columnIndexOrThrow6));
                    videoInfo.setDate(query.getLong(columnIndexOrThrow7) * 1000);
                    videoInfo.setMimeType(query.getString(columnIndexOrThrow8));

                    // Extract folder name from path (getting the parent directory)
                    if (path != null && !path.isEmpty()) {
                        File file = new File(path);
                        String folderName = file.getParentFile() != null ? file.getParentFile().getName() : "";
                        videoInfo.setFolder(folderName);  // Set the folder name (directory)
                    }

                    // Add to the hash set and longSparseArray
                    hashSet.add(videoInfo);
                    longSparseArray.put(videoInfo.getId(), videoInfo);
                }
            } catch (Exception e) {
                exception = e;
                Log.d("fdsfdsds", exception.toString());
                exception.printStackTrace();
            } finally {
                if (query != null) {
                    query.close();
                }
            }
        }

        // Save the data to VideoDatabaseControl
        VideoDatabaseControl.getInstance().setNewData(hashSet, longSparseArray);
        return new ArrayList<>(hashSet);
    }


    public List<VideoFolder> getAllVideoOfFolder() {
        Throwable th = null;
        ArrayList<VideoFolder> arrayList = new ArrayList<>();
        HashMap<String, VideoFolder> hashMap = new HashMap<>();
        Cursor query = this.mContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI,
                new String[]{"bucket_id", "bucket_display_name", "_id", "_data", "_display_name", "duration", "_size", "resolution", "date_modified", "mime_type"},
                null, null, "date_modified DESC");

        if (query != null) {
            try {
                int columnIndexOrThrow = query.getColumnIndexOrThrow("bucket_id");
                int columnIndexOrThrow2 = query.getColumnIndexOrThrow("bucket_display_name");
                int columnIndexOrThrow3 = query.getColumnIndexOrThrow("_id");
                int columnIndexOrThrow4 = query.getColumnIndexOrThrow("_data");
                int columnIndexOrThrow5 = query.getColumnIndexOrThrow("_display_name");
                int columnIndexOrThrow6 = query.getColumnIndexOrThrow("duration");
                int columnIndexOrThrow7 = query.getColumnIndexOrThrow("_size");
                int columnIndexOrThrow8 = query.getColumnIndexOrThrow("resolution");
                int columnIndexOrThrow9 = query.getColumnIndexOrThrow("date_modified");
                int columnIndexOrThrow10 = query.getColumnIndexOrThrow("mime_type");

                while (query.moveToNext()) {
                    String folderName = query.getString(columnIndexOrThrow2); // folder name
                    VideoInfo videoInfo = new VideoInfo();
                    long id = query.getLong(columnIndexOrThrow3);
                    videoInfo.setId(id);
                    videoInfo.setUri(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, id).toString());
                    videoInfo.setPath(query.getString(columnIndexOrThrow4));
                    videoInfo.setDisplayName(query.getString(columnIndexOrThrow5));
                    videoInfo.setDuration(query.getLong(columnIndexOrThrow6));
                    videoInfo.setSize(query.getLong(columnIndexOrThrow7));
                    videoInfo.setResolution(query.getString(columnIndexOrThrow8));
                    videoInfo.setDate(query.getLong(columnIndexOrThrow9) * 1000);
                    videoInfo.setMimeType(query.getString(columnIndexOrThrow10));

                    VideoFolder videoFolder;
                    if (hashMap.containsKey(folderName)) {
                        videoFolder = hashMap.get(folderName);
                        if (videoFolder != null) {
                            videoFolder.addVideo(videoInfo);
                        }
                    } else {
                        videoFolder = new VideoFolder();
                        videoFolder.setFolderName(folderName);
                        videoFolder.addVideo(videoInfo);
                        hashMap.put(folderName, videoFolder);
                    }
                }
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            } finally {
                if (query != null) {
                    query.close();
                }
            }
        }

        // Use hashMap directly to get all VideoFolders
        for (Map.Entry<String, VideoFolder> entry : hashMap.entrySet()) {
            arrayList.add(entry.getValue());
        }

        return arrayList;
    }

    public List<VideoSubtitle> getAllSubFile() {
        ArrayList arrayList = new ArrayList();
        Cursor query = this.mContext.getContentResolver().query(Files.getContentUri("external"), null, "_data LIKE '%.srt'", null, "_id DESC");
        if (query != null) {
            try {
                int columnIndexOrThrow = query.getColumnIndexOrThrow("title");
                int columnIndexOrThrow2 = query.getColumnIndexOrThrow("_data");
                while (query.moveToNext()) {
                    arrayList.add(new VideoSubtitle(query.getString(columnIndexOrThrow2), query.getString(columnIndexOrThrow)));
                }
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
        }
        if (query != null) {
            query.close();
        }
        return arrayList;
    }
}
