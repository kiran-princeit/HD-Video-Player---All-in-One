package com.hdvideoplayer.smartplayer.player.view;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;

public interface MusicDialogView extends BaseView {
    void onMusicLoader(List<MusicInfo> list);
}
