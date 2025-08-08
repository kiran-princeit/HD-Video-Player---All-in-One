package com.hdvideoplayer.smartplayer.player.presenter.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.music.MusicInfoView;

public class MusicMainPresenter extends BasePresenter<MusicInfoView> {
    private final MusicDataRepository mMusicRepository;

    public MusicMainPresenter(MusicInfoView musicInfoView, MusicDataRepository musicDataRepository) {
        super(musicInfoView);
        this.mMusicRepository = musicDataRepository;
    }

    public void openMusicsTab() {
        this.mMusicRepository.fetchAllMusic(new LoadDataListener<MusicInfo>() {
            public void onError() {
            }

            public void onSuccess(List<MusicInfo> list) {
                if (MusicMainPresenter.this.mView != null) {
                    ((MusicInfoView) MusicMainPresenter.this.mView).updateMusicList(list);
                }
            }
        });
    }
}
