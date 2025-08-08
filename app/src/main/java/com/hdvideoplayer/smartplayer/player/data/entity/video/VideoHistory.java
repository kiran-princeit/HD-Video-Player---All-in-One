package com.hdvideoplayer.smartplayer.player.data.entity.video;

public class VideoHistory {
    private long mCurrentPosition;
    private long mDateAdded;
    private long mId;
    private VideoInfo video;

    public long getId() {
        return this.mId;
    }

    public void setId(long j) {
        this.mId = j;
    }

    public VideoInfo getVideo() {
        return this.video;
    }

    public void setVideo(VideoInfo videoInfo) {
        this.video = videoInfo;
    }

    public long getCurrentPosition() {
        return this.mCurrentPosition;
    }

    public void setCurrentPosition(long j) {
        this.mCurrentPosition = j;
    }

    public long getDateAdded() {
        return this.mDateAdded;
    }

    public void setDateAdded(long j) {
        this.mDateAdded = j;
    }
}
