package com.hdvideoplayer.smartplayer.player.data.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.hdvideoplayer.smartplayer.player.data.dao.music.MusicHistoryDAO;
import com.hdvideoplayer.smartplayer.player.data.dao.music.MusicPlaylistDAO;
import com.hdvideoplayer.smartplayer.player.data.dao.video.VideoHistoryDAO;
import com.hdvideoplayer.smartplayer.player.data.dao.video.VideoPlaylistDAO;

public abstract class MyDatabase extends RoomDatabase {
    private static volatile MyDatabase sInstance;

    public abstract MusicHistoryDAO musicHistoryDAO();

    public abstract MusicPlaylistDAO musicPlaylistDAO();

    public abstract VideoHistoryDAO videoHistoryDAO();

    public abstract VideoPlaylistDAO videoPlaylistDAO();

    public static MyDatabase getInstance(Context context) {
        if (context == null) {
            Log.e("MyDatabase", "Context is null!");
            return null; // or handle the error as needed
        }
        if (sInstance == null) {
            synchronized (MyDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "MyDatabase.db")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sInstance;
    }
}
