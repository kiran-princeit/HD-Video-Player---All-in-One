package com.hdvideoplayer.smartplayer.player.view.video;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoFolder;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface VideoFolderView extends BaseView {
    void updateFolderList(List<VideoFolder> list);
}
