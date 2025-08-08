package com.hdvideoplayer.smartplayer.player.data.entity.music;

import android.text.TextUtils;
import java.io.Serializable;

public class MusicInfo implements Serializable {
    private String mAlbum;
    private String mArtist;
    private long mDateCreated;
    private String mDisplayName;
    private long mDuration;
    private long mId;
    private String mPath;
    private long mSize;
    private String uri;

    public String getDisplayName() {
        String str = this.mDisplayName;
        return str == null ? "" : str;
    }

    public void setDisplayName(String str) {
        this.mDisplayName = str;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public void setDuration(long j) {
        this.mDuration = j;
    }

    public long getId() {
        return this.mId;
    }

    public void setId(long j) {
        this.mId = j;
    }

    public long getDate() {
        return this.mDateCreated;
    }

    public void setDate(long j) {
        this.mDateCreated = j;
    }

    public String getPath() {
        String str = this.mPath;
        return str == null ? "" : str;
    }

    public void setPath(String str) {
        this.mPath = str;
    }

    public String getUri() {
        String str = this.uri;
        return str == null ? "" : str;
    }

    public void setUri(String str) {
        this.uri = str;
    }

    public String getArtist() {
        return TextUtils.isEmpty(this.mArtist) ? "<unknown>" : this.mArtist;
    }

    public void setArtist(String str) {
        this.mArtist = str;
    }

    public String getAlbum() {
        return TextUtils.isEmpty(this.mAlbum) ? "<unknown>" : this.mAlbum;
    }

    public void setAlbum(String str) {
        this.mAlbum = str;
    }

    public long getSize() {
        return this.mSize;
    }

    public void setSize(long j) {
        this.mSize = j;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && ((MusicInfo) obj).getId() == this.mId;
    }

    public String toString() {
        return "MusicInfo{mId=" + this.mId + ", mDisplayName='" + this.mDisplayName + "', mDuration=" + this.mDuration + ", mDateAdded=" + this.mDateCreated + ", mPath='" + this.mPath + "', mArtist='" + this.mArtist + "', mAlbum='" + this.mAlbum + "', mSize=" + this.mSize + '}';
    }
}
