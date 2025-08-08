package com.hdvideoplayer.smartplayer.player.view.video;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.Playlist;
import com.hdvideoplayer.smartplayer.player.view.BaseView;

public interface PlaylistView extends BaseView {
    void onCreatePlaylist(boolean z, Playlist playlist);

    void onDuplicationPlaylist(Playlist playlist);

    void onUpdatePlaylistName(int i, String str, boolean z);

    void updateVideoPlaylist(List<Playlist> list);
}
