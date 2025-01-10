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
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.Converters;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;

public final class MusicPlaylistDAO_Impl extends MusicPlaylistDAO {
    private final RoomDatabase __db;
    private final EntityInsertionAdapter<MusicPlaylist> __insertionAdapterOfMusicPlaylist;
    private final SharedSQLiteStatement __preparedStmtOfDeletePlaylist;
    private final EntityDeletionOrUpdateAdapter<MusicPlaylist> __updateAdapterOfMusicPlaylist;

    public MusicPlaylistDAO_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
        this.__insertionAdapterOfMusicPlaylist = new EntityInsertionAdapter<MusicPlaylist>(roomDatabase) {
            public String createQuery() {
                return "INSERT OR ABORT INTO `playlist_music_table` (`_playlist_date_added`,`_playlist_music_name`,`_playlist_all_music`) VALUES (?,?,?)";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, MusicPlaylist musicPlaylist) {
                supportSQLiteStatement.bindLong(1, musicPlaylist.getDateAdded());
                if (musicPlaylist.getPlaylistName() == null) {
                    supportSQLiteStatement.bindNull(2);
                } else {
                    supportSQLiteStatement.bindString(2, musicPlaylist.getPlaylistName());
                }
                String fromArrayList = Converters.fromArrayList(musicPlaylist.getMusicIdList());
                if (fromArrayList == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindString(3, fromArrayList);
                }
            }
        };
        this.__updateAdapterOfMusicPlaylist = new EntityDeletionOrUpdateAdapter<MusicPlaylist>(roomDatabase) {
            public String createQuery() {
                return "UPDATE OR ABORT `playlist_music_table` SET `_playlist_date_added` = ?,`_playlist_music_name` = ?,`_playlist_all_music` = ? WHERE `_playlist_date_added` = ?";
            }

            public void bind(SupportSQLiteStatement supportSQLiteStatement, MusicPlaylist musicPlaylist) {
                supportSQLiteStatement.bindLong(1, musicPlaylist.getDateAdded());
                if (musicPlaylist.getPlaylistName() == null) {
                    supportSQLiteStatement.bindNull(2);
                } else {
                    supportSQLiteStatement.bindString(2, musicPlaylist.getPlaylistName());
                }
                String fromArrayList = Converters.fromArrayList(musicPlaylist.getMusicIdList());
                if (fromArrayList == null) {
                    supportSQLiteStatement.bindNull(3);
                } else {
                    supportSQLiteStatement.bindString(3, fromArrayList);
                }
                supportSQLiteStatement.bindLong(4, musicPlaylist.getDateAdded());
            }
        };
        this.__preparedStmtOfDeletePlaylist = new SharedSQLiteStatement(roomDatabase) {
            public String createQuery() {
                return "DELETE FROM playlist_music_table WHERE _playlist_date_added = ?";
            }
        };
    }

    public static List<Class<?>> getRequiredConverters() {
        return Collections.emptyList();
    }

    public void insertNewPlaylist(MusicPlaylist musicPlaylist) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            this.__insertionAdapterOfMusicPlaylist.insert((MusicPlaylist) musicPlaylist);
            this.__db.setTransactionSuccessful();
        } finally {
            this.__db.endTransaction();
        }
    }

    public int updateVideoPlaylist(MusicPlaylist musicPlaylist) {
        this.__db.assertNotSuspendingTransaction();
        this.__db.beginTransaction();
        try {
            int handle = this.__updateAdapterOfMusicPlaylist.handle(musicPlaylist) + 0;
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

    public List<MusicPlaylist> getAllPlaylist() {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from playlist_music_table ORDER BY _playlist_date_added ASC", 0);
        this.__db.assertNotSuspendingTransaction();
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_playlist_date_added");
            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_music_name");
            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_all_music");
            List<MusicPlaylist> arrayList = new ArrayList(query.getCount());
            while (query.moveToNext()) {
                MusicPlaylist musicPlaylist = new MusicPlaylist();
                musicPlaylist.setDateAdded(query.getLong(columnIndexOrThrow));
                musicPlaylist.setPlaylistName(query.isNull(columnIndexOrThrow2) ? null : query.getString(columnIndexOrThrow2));
                musicPlaylist.setMusicIdList(Converters.fromString(query.isNull(columnIndexOrThrow3) ? null : query.getString(columnIndexOrThrow3)));
                arrayList.add(musicPlaylist);
            }
            return arrayList;
        } finally {
            query.close();
            acquire.release();
        }
    }

    public MusicPlaylist getPlaylistByDateAdded(long j) {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT * from playlist_music_table WHERE _playlist_date_added = ?", 1);
        acquire.bindLong(1, j);
        this.__db.assertNotSuspendingTransaction();
        MusicPlaylist musicPlaylist = null;
        Cursor query = DBUtil.query(this.__db, acquire, false, null);
        try {
            int columnIndexOrThrow = CursorUtil.getColumnIndexOrThrow(query, "_playlist_date_added");
            int columnIndexOrThrow2 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_music_name");
            int columnIndexOrThrow3 = CursorUtil.getColumnIndexOrThrow(query, "_playlist_all_music");
            if (query.moveToFirst()) {
                String string = null;
                MusicPlaylist musicPlaylist2 = new MusicPlaylist();
                musicPlaylist2.setDateAdded(query.getLong(columnIndexOrThrow));
                musicPlaylist2.setPlaylistName(query.isNull(columnIndexOrThrow2) ? null : query.getString(columnIndexOrThrow2));
                if (!query.isNull(columnIndexOrThrow3)) {
                    string = query.getString(columnIndexOrThrow3);
                }
                musicPlaylist2.setMusicIdList(Converters.fromString(string));
                musicPlaylist = musicPlaylist2;
            }
            query.close();
            acquire.release();
            return musicPlaylist;
        } catch (Throwable th) {
            query.close();
            acquire.release();
        }
        return musicPlaylist;
    }

    public String getPlaylistContainingSpecificName(String str) {
        RoomSQLiteQuery acquire = RoomSQLiteQuery.acquire("SELECT _playlist_music_name AS _name FROM playlist_music_table WHERE _name = ?", 1);
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
