package com.hdvideoplayer.smartplayer.player.view.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicArtist;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface MusicArtistView extends BaseView {
    void onOpenArtist(List<MusicArtist> list);

    void onUpdateMusicOfArtist(List<MusicInfo> list);
}
