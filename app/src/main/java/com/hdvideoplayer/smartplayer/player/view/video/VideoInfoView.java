package com.hdvideoplayer.smartplayer.player.view.video;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface VideoInfoView extends BaseView {
    void updateVideoList(List<VideoInfo> list);
}
