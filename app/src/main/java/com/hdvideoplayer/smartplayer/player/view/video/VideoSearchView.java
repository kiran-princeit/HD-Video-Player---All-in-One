package com.hdvideoplayer.smartplayer.player.view.video;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface VideoSearchView extends BaseView {
    void onSearchVideo(List<VideoInfo> list);
}
