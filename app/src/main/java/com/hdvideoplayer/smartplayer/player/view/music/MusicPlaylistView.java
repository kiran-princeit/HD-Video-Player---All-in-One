package com.hdvideoplayer.smartplayer.player.view.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicPlaylist;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface MusicPlaylistView extends BaseView {
    void onCreatePlaylist(boolean z, MusicPlaylist musicPlaylist);

    void onDuplicationPlaylist(MusicPlaylist musicPlaylist);

    void onUpdatePlaylistName(int i, String str, boolean z);

    void updateMusicPlaylist(List<MusicPlaylist> list);
}
