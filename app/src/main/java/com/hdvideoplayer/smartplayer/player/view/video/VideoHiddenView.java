package com.hdvideoplayer.smartplayer.player.view.video;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface VideoHiddenView extends BaseView {
    void updateVideoHiddenList(List<VideoInfo> list);
}
