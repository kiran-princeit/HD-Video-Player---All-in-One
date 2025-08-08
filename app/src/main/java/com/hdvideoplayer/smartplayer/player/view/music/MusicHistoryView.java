package com.hdvideoplayer.smartplayer.player.view.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicHistory;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface MusicHistoryView extends BaseView {
    void updateMusicHistoryList(List<MusicHistory> list);
}
