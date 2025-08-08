package com.hdvideoplayer.smartplayer.player.view;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;

public interface VideoDialogView extends BaseView {
    void onVideoLoader(List<VideoInfo> list);
}
