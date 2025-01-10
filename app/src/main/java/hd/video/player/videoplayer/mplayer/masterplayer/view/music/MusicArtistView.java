package hd.video.player.videoplayer.mplayer.masterplayer.view.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public interface MusicArtistView extends BaseView {
    void onOpenArtist(List<MusicArtist> list);

    void onUpdateMusicOfArtist(List<MusicInfo> list);
}
