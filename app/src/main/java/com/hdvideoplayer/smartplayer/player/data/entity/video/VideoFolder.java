package com.hdvideoplayer.smartplayer.player.data.entity.video;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VideoFolder implements Serializable {
    private String mName;
    private List<VideoInfo> mVideosList = new ArrayList();

    public String getFolderName() {
        String str = this.mName;
        return str == null ? "Root" : str;
    }

    public void setFolderName(String str) {
        this.mName = str;
    }

    public String getPath() {
        List list = this.mVideosList;
        String str = "";
        if (list == null || list.isEmpty()) {
            return str;
        }
        VideoInfo videoInfo = (VideoInfo) this.mVideosList.get(0);
        String path = videoInfo.getPath();
        int lastIndexOf = path.lastIndexOf(videoInfo.getDisplayName()) - 1;
        return lastIndexOf > -1 ? path.substring(0, lastIndexOf) : str;
    }

    public List<VideoInfo> getVideoList() {
        List<VideoInfo> list = this.mVideosList;
        return list == null ? new ArrayList() : list;
    }

    public void addVideo(VideoInfo videoInfo) {
        this.mVideosList.add(videoInfo);
    }

    public void setVideosList(List<VideoInfo> list) {
        this.mVideosList = list;
    }

    public long getSize() {
        List<VideoInfo> list = this.mVideosList;
        long j = 0;
        if (list == null) {
            return 0;
        }
        for (VideoInfo size : list) {
            j += size.getSize();
        }
        return j;
    }
}
