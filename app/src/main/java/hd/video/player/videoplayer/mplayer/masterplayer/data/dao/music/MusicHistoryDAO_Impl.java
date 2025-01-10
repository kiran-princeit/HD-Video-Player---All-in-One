package hd.video.player.videoplayer.mplayer.masterplayer.data.dao.music;

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
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicHistory;

public final class MusicHistoryDAO_Impl extends MusicHistoryDAO {
    private final RoomDatabase __db;
    private final EntityInsertionAdapter<MusicHistory> __insertionAdapterOfMusicHistory;
    private final SharedSQLiteStatement __preparedStmtOfDeleteAllMusicHistory;
    private final SharedSQLiteStatement __preparedStmtOfDeleteMusicHistoryById;
    private final EntityDeletionOrUpdateAdapter<MusicHistory> __updateAdapterOfMusicHistory;

    public MusicHistoryDAO_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
        this.__insertionAdapterOfMusicHistory = new EntityInsertionAdapter<MusicHistory>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR REPLACE INTO `history_music_table` (`_music_id`,`_current_position`,`_date_added`) VALUES (?,?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, MusicHistory musicHistory) {
                supportSQLiteStatement.bindLong(1, musicHistory.getId());
                supportSQLiteStatement.bindLong(2, musicHistory.getCurrentPosition());
                supportSQLiteStatement.bindLong(3, musicHistory.getDateAdded());
            }
        };
        this.__updateAdapterOfMusicHistory = new EntityDeletionOrUpdateAdapter<MusicHistory>(roomDatabase) {
            public String createQuery() {
                return "UPDATE OR ABORT `history_music_table` SET `_music_id` = ?,`_current_position` = ?,`_date_added` = ? WHERE `_music_id` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, MusicHistory musicHistory) {
                supportSQLiteStatement.bindLong(1, musicHistory.getId());
                supportSQLiteStatement.bindLong(2, musicHistory.getCurrentPosition());
                supportSQLiteStatement.bindLong(3, musicHistory.getDateAdded());
                supportSQLiteStatement.bindLong(4, musicHistory.getId());
            }
        };
        this.__preparedStmtOfDeleteMusicHistoryById = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM history_music_table WHERE _music_id = ?";
            }
        };
        this.__preparedStmtOfDeleteAllMusicHistory = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM history_music_table";
            }
        };
    }

    public static List<Class<?>> getRequiredConverters() {
        return Collections.emptyList();
    }

    public void insertNewHistoryMusic(MusicHistory musicHistory) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfMusicHistory.insert((MusicHistory) musicHistory);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateHistoryMusic(MusicHistory musicHistory) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfMusicHistory.handle(musicHistory) + 0;
            this.__db.setTransactionSuccessful();
            return handle;
        } finally {
            this.__db.endTransaction();
        }
    }

    public void deleteMusicHistoryById(long j) {
        this.__db.assertNotSuspendingTransaction();
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteMusicHistoryById.acquire();
        acquire.bindLong(1, j);
        this.__db.beginTransaction();
        try {
            acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeleteMusicHistoryById.release(acquire);
        }
    }

    public void deleteAllMusicHistory() {
        this.__db.assertNotSuspendingTransaction();
        SupportSQLiteStatement acquire = this.__preparedStmtOfDeleteAllMusicHistory.acquire();
        this.__db.beginTransaction();
        try {
            acquire.executeUpdateDelete();
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
            this.__preparedStmtOfDeleteAllMusicHistory.release(acquire);
        }
    }

    public List<MusicHistory> getAllHistoryMusic() {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from history_music_table ORDER BY _date_added DESC", 0);
        this.__db.assertNotSuspendingTransaction();
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_music_id");
            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_current_position");
            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_date_added");
            List<MusicHistory> arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                MusicHistory musicHistory = new MusicHistory();
                musicHistory.setId(query.getLong(columnIndexOrThrow));
                musicHistory.setCurrentPosition(query.getLong(columnIndexOrThrow2));
                musicHistory.setDateAdded(query.getLong(columnIndexOrThrow3));
                arrayList.add(musicHistory);
            }
            return arrayList;
        } finally {
            query.close();
            acquire.release();
        }
    }

    public MusicHistory getAHistoryMusic(long j) {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from history_music_table WHERE _music_id = ?", 1);
        acquire.bindLong(1, j);
        this.__db.assertNotSuspendingTransaction();
        MusicHistory musicHistory = null;
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_music_id");
            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_current_position");
            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_date_added");
            if (query.moveToFirst()) {
                musicHistory = new MusicHistory();
                musicHistory.setId(query.getLong(columnIndexOrThrow));
                musicHistory.setCurrentPosition(query.getLong(columnIndexOrThrow2));
                musicHistory.setDateAdded(query.getLong(columnIndexOrThrow3));
            }
            query.close();
            acquire.release();
            return musicHistory;
        } catch (Throwable th) {
            query.close();
            acquire.release();
        }
        return musicHistory;
    }
}
