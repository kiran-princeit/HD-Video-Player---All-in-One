package com.hdvideoplayer.smartplayer.player.data.dao.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicHistory;

public abstract class MusicHistoryDAO {
    public abstract void deleteAllMusicHistory();

    public abstract void deleteMusicHistoryById(long j);

    public abstract MusicHistory getAHistoryMusic(long j);

    public abstract List<MusicHistory> getAllHistoryMusic();

    public abstract void insertNewHistoryMusic(MusicHistory musicHistory);

    public abstract int updateHistoryMusic(MusicHistory musicHistory);

    public void updateMusicTimeData(long j, int i) {
        MusicHistory aHistoryMusic = getAHistoryMusic(j);
        aHistoryMusic.setCurrentPosition((long) i);
        updateHistoryMusic(aHistoryMusic);
    }
}
