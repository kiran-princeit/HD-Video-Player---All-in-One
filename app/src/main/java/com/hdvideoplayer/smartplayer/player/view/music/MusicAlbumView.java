package com.hdvideoplayer.smartplayer.player.view.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicAlbum;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface MusicAlbumView extends BaseView {
    void onUpdateAlbum(List<MusicAlbum> list);

    void onUpdateMusicList(List<MusicInfo> list);
}
