package com.hdvideoplayer.smartplayer.player.data.entity.music;

import android.os.Parcel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.data.entity.MultiItemEntity;

public class MusicPlaylist implements Serializable, MultiItemEntity, com.hdvideoplayer.smartplayer.player.base.entity.MultiItemEntity {
    public static int viewMode = 1;
    private long mDateAdded;
    private List<Long> mMusicIdList;
    private String mPlaylistName;

    public MusicPlaylist() {
    }

    public MusicPlaylist(String str) {
        this.mPlaylistName = str;
    }

    protected MusicPlaylist(Parcel parcel) {
        this.mPlaylistName = parcel.readString();
        this.mDateAdded = parcel.readLong();
    }

    public long getDateAdded() {
        return this.mDateAdded;
    }

    public void setDateAdded(long j) {
        this.mDateAdded = j;
    }

    public String getPlaylistName() {
        return this.mPlaylistName;
    }

    public void setPlaylistName(String str) {
        this.mPlaylistName = str;
    }

    public List<Long> getMusicIdList() {
        List<Long> list = this.mMusicIdList;
        return list == null ? new ArrayList() : list;
    }

    public void setMusicIdList(List<Long> list) {
        this.mMusicIdList = list;
    }

    public int getItemType() {
        return viewMode;
    }

    public void addMusicList(List<MusicInfo> newSongs) {
        for (MusicInfo song : newSongs) {
            if (!mMusicIdList.contains(song.getId())) {
                mMusicIdList.add(song.getId());
            }
        }
    }
}
