package com.hdvideoplayer.smartplayer.player.data.database;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomMasterTable;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.hdvideoplayer.smartplayer.player.data.dao.music.MusicHistoryDAO;
import com.hdvideoplayer.smartplayer.player.data.dao.music.MusicHistoryDAO_Impl;
import com.hdvideoplayer.smartplayer.player.data.dao.music.MusicPlaylistDAO;
import com.hdvideoplayer.smartplayer.player.data.dao.music.MusicPlaylistDAO_Impl;
import com.hdvideoplayer.smartplayer.player.data.dao.video.VideoHistoryDAO;
import com.hdvideoplayer.smartplayer.player.data.dao.video.VideoHistoryDAO_Impl;
import com.hdvideoplayer.smartplayer.player.data.dao.video.VideoPlaylistDAO;
import com.hdvideoplayer.smartplayer.player.data.dao.video.VideoPlaylistDAO_Impl;

public final class MyDatabase_Impl extends MyDatabase {
    private volatile MusicHistoryDAO _musicHistoryDAO;
    private volatile MusicPlaylistDAO _musicPlaylistDAO;
    private volatile VideoHistoryDAO _videoHistoryDAO;
    private volatile VideoPlaylistDAO _videoPlaylistDAO;

  
    public SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration databaseConfiguration) {
        return databaseConfiguration.sqliteOpenHelperFactory.create(SupportSQLiteOpenHelper.Configuration.builder(databaseConfiguration.context).name(databaseConfiguration.name).callback(new RoomOpenHelper(databaseConfiguration, new RoomOpenHelper.Delegate(136) {
            public void onPostMigrate(SupportSQLiteDatabase supportSQLiteDatabase) {
            }

            public void createAllTables(SupportSQLiteDatabase supportSQLiteDatabase) {
                supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `playlist_video_table` (`_playlist_video_name` TEXT, `_playlist_date_added` INTEGER NOT NULL, `_playlist_all_video` TEXT, PRIMARY KEY(`_playlist_date_added`))");
                supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `history_video_table` (`_video_id` INTEGER NOT NULL, `_current_position` INTEGER NOT NULL, `_date_added` INTEGER NOT NULL, PRIMARY KEY(`_video_id`))");
                supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `playlist_music_table` (`_playlist_date_added` INTEGER NOT NULL, `_playlist_music_name` TEXT, `_playlist_all_music` TEXT, PRIMARY KEY(`_playlist_date_added`))");
                supportSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `history_music_table` (`_music_id` INTEGER NOT NULL, `_current_position` INTEGER NOT NULL, `_date_added` INTEGER NOT NULL, PRIMARY KEY(`_music_id`))");
                supportSQLiteDatabase.execSQL(RoomMasterTable.CREATE_QUERY);
                supportSQLiteDatabase.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8f4ebddd4867813c32cd669682c8ad81')");
            }

            public void dropAllTables(SupportSQLiteDatabase supportSQLiteDatabase) {
                supportSQLiteDatabase.execSQL("DROP TABLE IF EXISTS `playlist_video_table`");
                supportSQLiteDatabase.execSQL("DROP TABLE IF EXISTS `history_video_table`");
                supportSQLiteDatabase.execSQL("DROP TABLE IF EXISTS `playlist_music_table`");
                supportSQLiteDatabase.execSQL("DROP TABLE IF EXISTS `history_music_table`");
                if (MyDatabase_Impl.this.mCallbacks != null) {
                    int size = MyDatabase_Impl.this.mCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        ((RoomDatabase.Callback) MyDatabase_Impl.this.mCallbacks.get(i)).onDestructiveMigration(supportSQLiteDatabase);
                    }
                }
            }

            public void onCreate(SupportSQLiteDatabase supportSQLiteDatabase) {
                if (MyDatabase_Impl.this.mCallbacks != null) {
                    int size = MyDatabase_Impl.this.mCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        ((RoomDatabase.Callback) MyDatabase_Impl.this.mCallbacks.get(i)).onCreate(supportSQLiteDatabase);
                    }
                }
            }

            public void onOpen(SupportSQLiteDatabase supportSQLiteDatabase) {
                SupportSQLiteDatabase unused = MyDatabase_Impl.this.mDatabase = supportSQLiteDatabase;
                MyDatabase_Impl.this.internalInitInvalidationTracker(supportSQLiteDatabase);
                if (MyDatabase_Impl.this.mCallbacks != null) {
                    int size = MyDatabase_Impl.this.mCallbacks.size();
                    for (int i = 0; i < size; i++) {
                        ((RoomDatabase.Callback) MyDatabase_Impl.this.mCallbacks.get(i)).onOpen(supportSQLiteDatabase);
                    }
                }
            }

            public void onPreMigrate(SupportSQLiteDatabase supportSQLiteDatabase) {
                DBUtil.dropFtsSyncTriggers(supportSQLiteDatabase);
            }

            public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase supportSQLiteDatabase) {
                SupportSQLiteDatabase supportSQLiteDatabase2 = supportSQLiteDatabase;
                HashMap hashMap = new HashMap(3);
                hashMap.put("_playlist_video_name", new TableInfo.Column("_playlist_video_name", "TEXT", false, 0, (String) null, 1));
                hashMap.put("_playlist_date_added", new TableInfo.Column("_playlist_date_added", "INTEGER", true, 1, (String) null, 1));
                hashMap.put("_playlist_all_video", new TableInfo.Column("_playlist_all_video", "TEXT", false, 0, (String) null, 1));
                TableInfo tableInfo = new TableInfo("playlist_video_table", hashMap, new HashSet(0), new HashSet(0));
                TableInfo read = TableInfo.read(supportSQLiteDatabase2, "playlist_video_table");
                if (!tableInfo.equals(read)) {
                    return new RoomOpenHelper.ValidationResult(false, "playlist_video_table(net.hd.videodownlaoder.free.data.entity.video.VideoPlaylist).\n Expected:\n" + tableInfo + "\n Found:\n" + read);
                }
                HashMap hashMap2 = new HashMap(3);
                hashMap2.put("_video_id", new TableInfo.Column("_video_id", "INTEGER", true, 1, (String) null, 1));
                hashMap2.put("_current_position", new TableInfo.Column("_current_position", "INTEGER", true, 0, (String) null, 1));
                hashMap2.put("_date_added", new TableInfo.Column("_date_added", "INTEGER", true, 0, (String) null, 1));
                TableInfo tableInfo2 = new TableInfo("history_video_table", hashMap2, new HashSet(0), new HashSet(0));
                TableInfo read2 = TableInfo.read(supportSQLiteDatabase2, "history_video_table");
                if (!tableInfo2.equals(read2)) {
                    return new RoomOpenHelper.ValidationResult(false, "history_video_table(net.hd.videodownlaoder.free.data.entity.video.VideoHistory).\n Expected:\n" + tableInfo2 + "\n Found:\n" + read2);
                }
                HashMap hashMap3 = new HashMap(3);
                hashMap3.put("_playlist_date_added", new TableInfo.Column("_playlist_date_added", "INTEGER", true, 1, (String) null, 1));
                hashMap3.put("_playlist_music_name", new TableInfo.Column("_playlist_music_name", "TEXT", false, 0, (String) null, 1));
                hashMap3.put("_playlist_all_music", new TableInfo.Column("_playlist_all_music", "TEXT", false, 0, (String) null, 1));
                TableInfo tableInfo3 = new TableInfo("playlist_music_table", hashMap3, new HashSet(0), new HashSet(0));
                TableInfo read3 = TableInfo.read(supportSQLiteDatabase2, "playlist_music_table");
                if (!tableInfo3.equals(read3)) {
                    return new RoomOpenHelper.ValidationResult(false, "playlist_music_table(net.hd.videodownlaoder.free.data.entity.music.MusicPlaylist).\n Expected:\n" + tableInfo3 + "\n Found:\n" + read3);
                }
                HashMap hashMap4 = new HashMap(3);
                hashMap4.put("_music_id", new TableInfo.Column("_music_id", "INTEGER", true, 1, (String) null, 1));
                hashMap4.put("_current_position", new TableInfo.Column("_current_position", "INTEGER", true, 0, (String) null, 1));
                hashMap4.put("_date_added", new TableInfo.Column("_date_added", "INTEGER", true, 0, (String) null, 1));
                TableInfo tableInfo4 = new TableInfo("history_music_table", hashMap4, new HashSet(0), new HashSet(0));
                TableInfo read4 = TableInfo.read(supportSQLiteDatabase2, "history_music_table");
                if (!tableInfo4.equals(read4)) {
                    return new RoomOpenHelper.ValidationResult(false, "history_music_table(net.hd.videodownlaoder.free.data.entity.music.MusicHistory).\n Expected:\n" + tableInfo4 + "\n Found:\n" + read4);
                }
                return new RoomOpenHelper.ValidationResult(true, (String) null);
            }
        }, "8f4ebddd4867813c32cd669682c8ad81", "8bbea07ab75fc14e82a50416e4054d73")).build());
    }

  
    public InvalidationTracker createInvalidationTracker() {
        return new InvalidationTracker(this, new HashMap(0), new HashMap(0), "playlist_video_table", "history_video_table", "playlist_music_table", "history_music_table");
    }

    public void clearAllTables() {
        super.assertNotMainThread();
        SupportSQLiteDatabase writableDatabase = super.getOpenHelper().getWritableDatabase();
        try {
            super.beginTransaction();
            writableDatabase.execSQL("DELETE FROM `playlist_video_table`");
            writableDatabase.execSQL("DELETE FROM `history_video_table`");
            writableDatabase.execSQL("DELETE FROM `playlist_music_table`");
            writableDatabase.execSQL("DELETE FROM `history_music_table`");
            super.setTransactionSuccessful();
        } finally {
            super.endTransaction();
            writableDatabase.query("PRAGMA wal_checkpoint(FULL)").close();
            if (!writableDatabase.inTransaction()) {
                writableDatabase.execSQL("VACUUM");
            }
        }
    }

  
    public Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
        HashMap hashMap = new HashMap();
        hashMap.put(VideoPlaylistDAO.class, VideoPlaylistDAO_Impl.getRequiredConverters());
        hashMap.put(VideoHistoryDAO.class, VideoHistoryDAO_Impl.getRequiredConverters());
        hashMap.put(MusicHistoryDAO.class, MusicHistoryDAO_Impl.getRequiredConverters());
        hashMap.put(MusicPlaylistDAO.class, MusicPlaylistDAO_Impl.getRequiredConverters());
        return hashMap;
    }

    public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
        return new HashSet();
    }

    public List<Migration> getAutoMigrations(Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> map) {
        return Arrays.asList(new Migration[0]);
    }

    public VideoPlaylistDAO videoPlaylistDAO() {
        VideoPlaylistDAO videoPlaylistDAO;
        if (this._videoPlaylistDAO != null) {
            return this._videoPlaylistDAO;
        }
        synchronized (this) {
            if (this._videoPlaylistDAO == null) {
                this._videoPlaylistDAO = new VideoPlaylistDAO_Impl(this);
            }
            videoPlaylistDAO = this._videoPlaylistDAO;
        }
        return videoPlaylistDAO;
    }

    public VideoHistoryDAO videoHistoryDAO() {
        VideoHistoryDAO videoHistoryDAO;
        if (this._videoHistoryDAO != null) {
            return this._videoHistoryDAO;
        }
        synchronized (this) {
            if (this._videoHistoryDAO == null) {
                this._videoHistoryDAO = new VideoHistoryDAO_Impl(this);
            }
            videoHistoryDAO = this._videoHistoryDAO;
        }
        return videoHistoryDAO;
    }

    public MusicHistoryDAO musicHistoryDAO() {
        MusicHistoryDAO musicHistoryDAO;
        if (this._musicHistoryDAO != null) {
            return this._musicHistoryDAO;
        }
        synchronized (this) {
            if (this._musicHistoryDAO == null) {
                this._musicHistoryDAO = new MusicHistoryDAO_Impl(this);
            }
            musicHistoryDAO = this._musicHistoryDAO;
        }
        return musicHistoryDAO;
    }

    public MusicPlaylistDAO musicPlaylistDAO() {
        MusicPlaylistDAO musicPlaylistDAO;
        if (this._musicPlaylistDAO != null) {
            return this._musicPlaylistDAO;
        }
        synchronized (this) {
            if (this._musicPlaylistDAO == null) {
                this._musicPlaylistDAO = new MusicPlaylistDAO_Impl(this);
            }
            musicPlaylistDAO = this._musicPlaylistDAO;
        }
        return musicPlaylistDAO;
    }
}
