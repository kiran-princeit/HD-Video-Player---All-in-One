package com.hdvideoplayer.smartplayer.player.presenter.music;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.music.MusicSearchView;

public class MusicSearchingPresenter extends BasePresenter<MusicSearchView> {
    private final MusicDataRepository mMusicRepository;

    public MusicSearchingPresenter(MusicSearchView musicSearchView, MusicDataRepository musicDataRepository) {
        super(musicSearchView);
        this.mMusicRepository = musicDataRepository;
    }

    public void searchMusicByMusicName(String str) {
        this.mMusicRepository.searchMusicByMusicName(new LoadDataListener<MusicInfo>() {
            public void onError() {
            }

            public void onSuccess(List<MusicInfo> list) {
                if (MusicSearchingPresenter.this.mView != null) {
                    ((MusicSearchView) MusicSearchingPresenter.this.mView).onSearchMusic(list);
                }
            }
        }, str);
    }
}
