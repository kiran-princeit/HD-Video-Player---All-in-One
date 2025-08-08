package com.hdvideoplayer.smartplayer.player.data.dao.video;

import android.database.Cursor;
import android.os.CancellationSignal;

import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.data.database.Converters;
import com.hdvideoplayer.smartplayer.player.data.entity.video.Playlist;

public final class VideoPlaylistDAO_Impl extends VideoPlaylistDAO {
    private final RoomDatabase __db;
    private final EntityInsertionAdapter<Playlist> __insertionAdapterOfVideoPlaylist;
    private final SharedSQLiteStatement __preparedStmtOfDeletePlaylist;
    private final EntityDeletionOrUpdateAdapter<Playlist> __updateAdapterOfVideoPlaylist;

    public VideoPlaylistDAO_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
        this.__insertionAdapterOfVideoPlaylist = new EntityInsertionAdapter<Playlist>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR ABORT INTO `playlist_video_table` (`_playlist_video_name`,`_playlist_date_added`,`_playlist_all_video`) VALUES (?,?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, Playlist playlist) {
                if (playlist.getPlaylistName() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindString(1, playlist.getPlaylistName());
                }
                supportSQLiteStatement.bindLong(2, playlist.getDateAdded());
                String fromArrayList = Converters.fromArrayList(playlist.getVideoIdList());
                if (fromArrayList == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindString(3, fromArrayList);
                }
            }
        };
        this.__updateAdapterOfVideoPlaylist = new EntityDeletionOrUpdateAdapter<Playlist>(roomDatabase) {
            public String createQuery() {
                return "UPDATE OR ABORT `playlist_video_table` SET `_playlist_video_name` = ?,`_playlist_date_added` = ?,`_playlist_all_video` = ? WHERE `_playlist_date_added` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, Playlist playlist) {
                if (playlist.getPlaylistName() == null) {
                    supportSQLiteStatement.bindNull(1);
                } else {
                    supportSQLiteStatement.bindString(1, playlist.getPlaylistName());
                }
                supportSQLiteStatement.bindLong(2, playlist.getDateAdded());
                String fromArrayList = Converters.fromArrayList(playlist.getVideoIdList());
                if (fromArrayList == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindString(3, fromArrayList);
                }
                supportSQLiteStatement.bindLong(4, playlist.getDateAdded());
            }
        };
        this.__preparedStmtOfDeletePlaylist = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM playlist_video_table WHERE _playlist_date_added = ?";
            }
        };
    }

    public static List<Class<?>> getRequiredConverters() {
        return Collections.emptyList();
    }

    public void insertNewPlaylist(Playlist playlist) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfVideoPlaylist.insert(playlist);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateVideoPlaylist(Playlist playlist) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfVideoPlaylist.handle(playlist) + 0;
            this.__db.setTransactionSuccessful();
            return handle;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void deletePlaylist(long j) {
        this.__db.assertNotSuspendingTransaction();
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeletePlaylist.acquire();
        acquire.bindLong(1, j);
        this.__db.beginTransaction();
        try {
            acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeletePlaylist.release(acquire);
        }
    }

    public List<Playlist> getAllPlaylist() {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from playlist_video_table ORDER BY _playlist_date_added ASC", 0);
        this.__db.assertNotSuspendingTransaction();
        Cursor query = DBUtil.query(this.__db, acquire, false, (CancellationSignal) null);
        try {
            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_playlist_video_name");
            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_date_added");
            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_all_video");
            ArrayList arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                Playlist playlist = new Playlist();
                playlist.setPlaylistName(query.isNull(columnIndexOrThrow) ? null : query.getString(columnIndexOrThrow));
                playlist.setDateAdded(query.getLong(columnIndexOrThrow2));
                playlist.setVideoIdList(Converters.fromString(query.isNull(columnIndexOrThrow3) ? null : query.getString(columnIndexOrThrow3)));
                arrayList.add(playlist);
            }
            return arrayList;
        } finally {
            query.close();
            acquire.release();
        }
    }

//    public VideoPlaylist getPlaylistByDateAdded(long j) {
//        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from playlist_video_table WHERE _playlist_date_added = ?", 1);
//        acquire.bindLong(1, j);
//        this.__db.assertNotSuspendingTransaction();
//        VideoPlaylist videoPlaylist = null;
//        Cursor query = DBUtil.query(this.__db, acquire, false, null);
//        try {
//            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_playlist_video_name");
//            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_date_added");
//            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_all_video");
//            if (query.moveToFirst()) {
//                String string = null;
//                VideoPlaylist videoPlaylist2 = new VideoPlaylist();
//                videoPlaylist2.setPlaylistName(query.isNull(columnIndexOrThrow) ? null : query.getString(columnIndexOrThrow));
//                videoPlaylist2.setDateAdded(query.getLong(columnIndexOrThrow2));
//                if (!query.isNull(columnIndexOrThrow3)) {
//                    string = query.getString(columnIndexOrThrow3);
//                }
//                videoPlaylist2.setVideoIdList(Converters.fromString(string));
//                videoPlaylist = videoPlaylist2;
//            }
//            query.close();
//            acquire.release();
//            return videoPlaylist;
//        } catch (Throwable th) {
//            query.close();
//            acquire.release();
//        }
//        return videoPlaylist;
//    }

    public Playlist getPlaylistByDateAdded(long dateAdded) {
        // SQL query to fetch the playlist by date added
        String query = "SELECT * FROM playlist_video_table WHERE _playlist_date_added = ?";
        RoomSQLiteQuery sqLiteQuery = RoomSQLiteQuery.acquire(query, 1);
        sqLiteQuery.bindLong(1, dateAdded);

        // Ensure database is ready for a transaction
        __db.assertNotSuspendingTransaction();

        Cursor cursor = null;
        Playlist playlist = null;

        try {
            // Execute the query
            cursor = DBUtil.query(__db, sqLiteQuery, false, null);

            // Get column indices
            int columnIndexName = CursorUtil.getColumnIndexOrThrow(cursor, "_playlist_video_name");
            int columnIndexDateAdded = CursorUtil.getColumnIndexOrThrow(cursor, "_playlist_date_added");
            int columnIndexAllVideo = CursorUtil.getColumnIndexOrThrow(cursor, "_playlist_all_video");

            // Check if the cursor has at least one result
            if (cursor.moveToFirst()) {
                playlist = new Playlist();

                // Fetch and set playlist name
                String playlistName = cursor.isNull(columnIndexName) ? null : cursor.getString(columnIndexName);
                playlist.setPlaylistName(playlistName);

                // Fetch and set date added
                long date = cursor.getLong(columnIndexDateAdded);
                playlist.setDateAdded(date);

                // Fetch and set the video ID list
                String videoIdListString = cursor.isNull(columnIndexAllVideo) ? null : cursor.getString(columnIndexAllVideo);
                List<Long> videoIdList = Converters.fromString(videoIdListString);
                playlist.setVideoIdList(videoIdList);
            }
        } finally {
            // Close resources
            if (cursor != null) {
                cursor.close();
            }
            sqLiteQuery.release();
        }

        return playlist;
    }


    public String getPlaylistContainingSpecificName(String str) {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT _playlist_video_name AS _name FROM playlist_video_table WHERE _name = ?", 1);
        if (str == null) {
            acquire.bindNull(1);
        } else {
            acquire.bindString(1, str);
        }
        this.__db.assertNotSuspendingTransaction();
        String str2 = null;
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            if (query.moveToFirst() && !query.isNull(0)) {
                str2 = query.getString(0);
            }
            query.close();
            acquire.release();
            return str2;
        } catch (Throwable th) {
            query.close();
            acquire.release();
        }
        return str2;
    }
}
