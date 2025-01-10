package hd.video.player.videoplayer.mplayer.masterplayer.presenter.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicSearchView;

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
