package com.hdvideoplayer.smartplayer.player.view.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface MusicInfoView extends BaseView {
    void updateMusicList(List<MusicInfo> list);
}
