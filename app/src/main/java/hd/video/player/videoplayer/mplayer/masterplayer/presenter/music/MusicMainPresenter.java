package hd.video.player.videoplayer.mplayer.masterplayer.presenter.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicInfoView;

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
