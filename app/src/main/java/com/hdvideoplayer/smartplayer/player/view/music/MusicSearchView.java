package com.hdvideoplayer.smartplayer.player.view.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface MusicSearchView extends BaseView {
    void onSearchMusic(List<MusicInfo> list);
}
