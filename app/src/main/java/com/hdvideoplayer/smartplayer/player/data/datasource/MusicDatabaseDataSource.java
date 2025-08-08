package com.hdvideoplayer.smartplayer.player.data.datasource;

import android.content.Context;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.database.MyDatabase;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicHistory;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicPlaylist;
import com.hdvideoplayer.smartplayer.player.data.utils.MusicFavoriteUtil;
import com.hdvideoplayer.smartplayer.player.util.thread.ThreadExecutor;
public class MusicDatabaseDataSource {
    Context mContext;

    public void updateMusicListForPlaylist(MusicPlaylist musicPlaylist, List<MusicInfo> list) {
    }

    public MusicDatabaseDataSource(Context context) {
        this.mContext = context;
    }

    public List<MusicPlaylist> getAllPlaylistMusics() {
        List<MusicPlaylist> allPlaylist = MyDatabase.getInstance(this.mContext).musicPlaylistDAO().getAllPlaylist();
        for (MusicPlaylist musicPlaylist : allPlaylist) {
            List<Long> musicIdList = musicPlaylist.getMusicIdList();
            ArrayList<Long> validMusicIds = new ArrayList<>();
            for (Long id : musicIdList) {
                if (MusicDatabaseControl.getInstance().getMusicById(id) != null) {
                    validMusicIds.add(id);
                }
            }
            musicPlaylist.setMusicIdList(validMusicIds);
        }
        return allPlaylist;
    }

    private List<MusicInfo> removeNonExistentMusic(List<MusicInfo> list) {
        ArrayList<MusicInfo> validMusicList = new ArrayList<>();
        for (MusicInfo musicInfo : list) {
            if (MusicDatabaseControl.getInstance().getMusicById(musicInfo.getId()) != null) {
                validMusicList.add(musicInfo);
            }
        }
        return validMusicList;
    }

    public boolean createMusicPlaylist(final MusicPlaylist musicPlaylist) {
        if (isPlaylistNameExistent(musicPlaylist.getPlaylistName())) {
            return false;
        }
        ThreadExecutor.runOnDatabaseThread(() -> insertNewPlaylist(musicPlaylist));
        return true;
    }

    public void insertNewPlaylist(MusicPlaylist musicPlaylist) {
        MyDatabase.getInstance(this.mContext).musicPlaylistDAO().insertNewPlaylist(musicPlaylist);
    }

    public List<MusicInfo> getAllFavoriteMusic() {
        ArrayList<MusicInfo> favoriteMusicList = new ArrayList<>();
        HashSet<Long> validFavoriteIds = new HashSet<>();
        for (Long id : MusicFavoriteUtil.getAllFavoriteMusicId(this.mContext)) {
            MusicInfo musicById = MusicDatabaseControl.getInstance().getMusicById(id);
            if (musicById != null) {
                favoriteMusicList.add(musicById);
                validFavoriteIds.add(id);
            }
        }
        MusicFavoriteUtil.setFavoriteMusicId(this.mContext, validFavoriteIds);
        return favoriteMusicList;
    }

    public List<MusicInfo> getAllMusicOfPlaylist(MusicPlaylist musicPlaylist) {
        ArrayList<MusicInfo> musicList = new ArrayList<>();
        for (Long id : new HashSet<>(MyDatabase.getInstance(this.mContext)
                .musicPlaylistDAO()
                .getPlaylistByDateAdded(musicPlaylist.getDateAdded())
                .getMusicIdList())) {
            MusicInfo musicById = MusicDatabaseControl.getInstance().getMusicById(id);
            if (musicById != null) {
                musicList.add(musicById);
            }
        }
        return musicList;
    }

    public List<MusicHistory> getHistoryMusics() {
        ArrayList<MusicHistory> historyList = new ArrayList<>();
        for (MusicHistory musicHistory : MyDatabase.getInstance(this.mContext).musicHistoryDAO().getAllHistoryMusic()) {
            MusicInfo musicById = MusicDatabaseControl.getInstance().getMusicById(musicHistory.getId());
            if (musicById != null) {
                musicHistory.setMusic(musicById);
                historyList.add(musicHistory);
            } else {
                MyDatabase.getInstance(this.mContext).musicHistoryDAO().deleteMusicHistoryById(musicHistory.getId());
            }
        }
        return historyList;
    }

    public void deleteMusicHistoryById(final long id) {
        ThreadExecutor.runOnDatabaseThread(() -> removeMusicHistoryById(id));
    }

    public void removeMusicHistoryById(long id) {
        MyDatabase.getInstance(this.mContext).musicHistoryDAO().deleteMusicHistoryById(id);
    }

    public void deleteAllMusicHistory() {
        ThreadExecutor.runOnDatabaseThread(this::clearAllMusicHistory);
    }

    public void clearAllMusicHistory() {
        MyDatabase.getInstance(this.mContext).musicHistoryDAO().deleteAllMusicHistory();
    }

    public void updateMusicHistoryData(final MusicInfo musicInfo) {
        ThreadExecutor.runOnDatabaseThread(() -> addMusicHistory(musicInfo));
    }

    public void addMusicHistory(MusicInfo musicInfo) {
        MusicHistory musicHistory = new MusicHistory();
        musicHistory.setId(musicInfo.getId());
        musicHistory.setMusic(musicInfo);
        musicHistory.setDateAdded(System.currentTimeMillis());
        MyDatabase.getInstance(this.mContext).musicHistoryDAO().insertNewHistoryMusic(musicHistory);
    }

    private boolean isPlaylistNameExistent(String playlistName) {
        String existingPlaylistName = MyDatabase.getInstance(this.mContext).musicPlaylistDAO().getPlaylistContainingSpecificName(playlistName);
        return !TextUtils.isEmpty(existingPlaylistName) && existingPlaylistName.equals(playlistName);
    }

    public boolean updatePlaylistName(final MusicPlaylist musicPlaylist, final String newName) {
        if (isPlaylistNameExistent(newName)) {
            return false;
        }
        ThreadExecutor.runOnDatabaseThread(() -> renamePlaylist(musicPlaylist, newName));
        return true;
    }

    public void renamePlaylist(MusicPlaylist musicPlaylist, String newName) {
        MyDatabase.getInstance(this.mContext).musicPlaylistDAO().updatePlaylistName(musicPlaylist.getDateAdded(), newName);
    }

    public boolean duplicateMusicPlaylist(final MusicPlaylist musicPlaylist) {
        if (isPlaylistNameExistent(musicPlaylist.getPlaylistName())) {
            return false;
        }
        ThreadExecutor.runOnDatabaseThread(() -> copyPlaylist(musicPlaylist));
        return true;
    }

    public void copyPlaylist(MusicPlaylist musicPlaylist) {
        MyDatabase.getInstance(this.mContext).musicPlaylistDAO().insertNewPlaylist(musicPlaylist);
    }

    public void deletePlaylist(final MusicPlaylist musicPlaylist) {
        ThreadExecutor.runOnDatabaseThread(() -> removePlaylist(musicPlaylist));
    }

    public void removePlaylist(MusicPlaylist musicPlaylist) {
        MyDatabase.getInstance(this.mContext).musicPlaylistDAO().deletePlaylist(musicPlaylist.getDateAdded());
    }

    public List<MusicInfo> searchMusicByMusicName(String query) {
        if (TextUtils.isEmpty(query.trim())) {
            return new ArrayList<>(MusicDatabaseControl.getInstance().getAllMusics());
        }
        return MusicDatabaseControl.getInstance().searchMusicByMusicName(query);
    }
}


