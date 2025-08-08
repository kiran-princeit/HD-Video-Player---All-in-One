package com.hdvideoplayer.smartplayer.player.data.dao.video;

import android.database.Cursor;

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

import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoHistory;

public final class VideoHistoryDAO_Impl extends VideoHistoryDAO {
    private final RoomDatabase __db;
    private final EntityInsertionAdapter<VideoHistory> __insertionAdapterOfVideoHistory;
    private final SharedSQLiteStatement __preparedStmtOfDeleteAllVideoHistory;
    private final SharedSQLiteStatement __preparedStmtOfDeleteVideoHistoryById;
    private final EntityDeletionOrUpdateAdapter<VideoHistory> __updateAdapterOfVideoHistory;

    public VideoHistoryDAO_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
        this.__insertionAdapterOfVideoHistory = new EntityInsertionAdapter<VideoHistory>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR REPLACE INTO `history_video_table` (`_video_id`,`_current_position`,`_date_added`) VALUES (?,?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, VideoHistory videoHistory) {
                supportSQLiteStatement.bindLong(1, videoHistory.getId());
                supportSQLiteStatement.bindLong(2, videoHistory.getCurrentPosition());
                supportSQLiteStatement.bindLong(3, videoHistory.getDateAdded());
            }
        };
        this.__updateAdapterOfVideoHistory = new EntityDeletionOrUpdateAdapter<VideoHistory>(roomDatabase) {
            public String createQuery() {
                return "UPDATE OR ABORT `history_video_table` SET `_video_id` = ?,`_current_position` = ?,`_date_added` = ? WHERE `_video_id` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, VideoHistory videoHistory) {
                supportSQLiteStatement.bindLong(1, videoHistory.getId());
                supportSQLiteStatement.bindLong(2, videoHistory.getCurrentPosition());
                supportSQLiteStatement.bindLong(3, videoHistory.getDateAdded());
                supportSQLiteStatement.bindLong(4, videoHistory.getId());
            }
        };
        this.__preparedStmtOfDeleteVideoHistoryById = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM history_video_table WHERE _video_id = ?";
            }
        };
        this.__preparedStmtOfDeleteAllVideoHistory = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM history_video_table";
            }
        };
    }

    public static List<Class<?>> getRequiredConverters() {
        return Collections.emptyList();
    }

    public void insertNewHistoryVideo(VideoHistory videoHistory) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfVideoHistory.insert((VideoHistory) videoHistory);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateHistoryVideo(VideoHistory videoHistory) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfVideoHistory.handle(videoHistory) + 0;
            this.__db.setTransactionSuccessful();
            return handle;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void deleteVideoHistoryById(long j) {
        this.__db.assertNotSuspendingTransaction();
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteVideoHistoryById.acquire();
        acquire.bindLong(1, j);
        this.__db.beginTransaction();
        try {
            acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeleteVideoHistoryById.release(acquire);
        }
    }

    public void deleteAllVideoHistory() {
        this.__db.assertNotSuspendingTransaction();
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteAllVideoHistory.acquire();
        this.__db.beginTransaction();
        try {
            acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeleteAllVideoHistory.release(acquire);
        }
    }

    public List<VideoHistory> getAllHistoryVideo() {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from history_video_table ORDER BY _date_added DESC", 0);
        this.__db.assertNotSuspendingTransaction();
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_video_id");
            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_current_position");
            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_date_added");
            List<VideoHistory> arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                VideoHistory videoHistory = new VideoHistory();
                videoHistory.setId(query.getLong(columnIndexOrThrow));
                videoHistory.setCurrentPosition(query.getLong(columnIndexOrThrow2));
                videoHistory.setDateAdded(query.getLong(columnIndexOrThrow3));
                arrayList.add(videoHistory);
            }
            return arrayList;
        } finally {
            query.close();
            acquire.release();
        }
    }

    public VideoHistory getAHistoryVideo(long j) {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from history_video_table WHERE _video_id = ?", 1);
        acquire.bindLong(1, j);
        this.__db.assertNotSuspendingTransaction();
        VideoHistory videoHistory = null;
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_video_id");
            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_current_position");
            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_date_added");
            if (query.moveToFirst()) {
                videoHistory = new VideoHistory();
                videoHistory.setId(query.getLong(columnIndexOrThrow));
                videoHistory.setCurrentPosition(query.getLong(columnIndexOrThrow2));
                videoHistory.setDateAdded(query.getLong(columnIndexOrThrow3));
            }
            query.close();
            acquire.release();
            return videoHistory;
        } catch (Throwable th) {
            query.close();
            acquire.release();
        }
        return videoHistory;
    }

    public int getAVideoTimeData(long j) {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT _current_position FROM history_video_table WHERE _video_id = ?", 1);
        acquire.bindLong(1, j);
        this.__db.assertNotSuspendingTransaction();
        int i = 0;
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            if (query.moveToFirst()) {
                i = query.getInt(0);
            }
            query.close();
            acquire.release();
            return i;
        } catch (Throwable th) {
            query.close();
            acquire.release();
        }
        return i;
    }
}
