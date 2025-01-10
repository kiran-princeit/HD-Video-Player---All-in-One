package hd.video.player.videoplayer.mplayer.masterplayer.presenter.music;

import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;

public class MusicPlayerPresenter {
    private MusicDataRepository mMusicRepository;

    public MusicPlayerPresenter(MusicDataRepository musicDataRepository) {
        this.mMusicRepository = musicDataRepository;
    }

    public void updateMusicHistoryData(MusicInfo musicInfo) {
        this.mMusicRepository.updateMusicHistory(musicInfo);
    }
}
