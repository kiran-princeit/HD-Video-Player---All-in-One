package com.hdvideoplayer.smartplayer.player.data.entity.video;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    private long mDateCreated;
    private String mDisplayName;
    private long mDuration;
    private long mId;
    private String mPath;
    private String mResolution;
    private long mSize;
    private String mUri;
    private String mimeType;

    String folder;

    public VideoInfo() {
    }

    public VideoInfo(long j) {
        this.mId = j;
    }

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

    public String getPath() {
        String str = this.mPath;
        return str == null ? "" : str;
    }

    public void setPath(String str) {
        this.mPath = str;
    }

    public  long getDate() {
        return this.mDateCreated;
    }

    public void setDate(long j) {
        this.mDateCreated = j;
    }

    public String getUri() {
        String str = this.mUri;
        return str == null ? "" : str;
    }

    public void setUri(String str) {
        this.mUri = str;
    }

    public String getMimeType() {
        String str = this.mimeType;
        return str == null ? "" : str;
    }

    public void setMimeType(String str) {
        this.mimeType = str;
    }

    public String getResolution() {
        String str = this.mResolution;
        return str == null ? "" : str;
    }

    public void setResolution(String str) {
        this.mResolution = str;
    }

    public long getSize() {
        return this.mSize;
    }

    public void setSize(long j) {
        this.mSize = j;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass() && ((VideoInfo) obj).getId() == this.mId;
    }

    public String getFolder() {
        return this.folder;
    }

    public void setFolder(String str) {
        this.folder = str;
    }

    public String toString() {
        return "VideoInfo{mId=" + this.mId + ", mPath='" + this.mPath + "', folder='" + this.folder + "', mDisplayName='" + this.mDisplayName + "', mDuration=" + this.mDuration + ", mDateCreated=" + this.mDateCreated + ", mUri='" + this.mUri + "', mimeType='" + this.mimeType + "', mResolution='" + this.mResolution + "', mSize=" + this.mSize + '}';
    }
}
