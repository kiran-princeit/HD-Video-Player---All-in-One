package com.hdvideoplayer.smartplayer.player.presenter.music;

import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;

public class MusicPlayerPresenter {
    private MusicDataRepository mMusicRepository;

    public MusicPlayerPresenter(MusicDataRepository musicDataRepository) {
        this.mMusicRepository = musicDataRepository;
    }

    public void updateMusicHistoryData(MusicInfo musicInfo) {
        this.mMusicRepository.updateMusicHistory(musicInfo);
    }
}
