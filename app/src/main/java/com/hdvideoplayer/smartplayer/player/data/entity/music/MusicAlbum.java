package com.hdvideoplayer.smartplayer.player.data.entity.music;

import java.io.Serializable;

public class MusicAlbum implements Serializable {
    private long albumId;
    private String albumName;
    private String artistName;
    private long lastYear;
    private long numberOfSongs;

    public long getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(long j) {
        this.albumId = j;
    }

    public String getAlbumName() {
        String str = this.albumName;
        return str == null ? "" : str;
    }

    public void setAlbumName(String str) {
        this.albumName = str;
    }

    public String getArtistName() {
        String str = this.artistName;
        return str == null ? "" : str;
    }

    public void setArtistName(String str) {
        this.artistName = str;
    }

    public long getNumberOfSongs() {
        return this.numberOfSongs;
    }

    public void setNumberOfSongs(long j) {
        this.numberOfSongs = j;
    }

    public long getLastYear() {
        return this.lastYear;
    }

    public void setLastYear(long j) {
        this.lastYear = j;
    }

    public String toString() {
        return "MusicAlbum{albumId=" + this.albumId + ", albumName='" + this.albumName + "', artistName='" + this.artistName + "', numberOfSongs=" + this.numberOfSongs + ", lastYear=" + this.lastYear + '}';
    }
}
