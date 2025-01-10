package hd.video.player.videoplayer.mplayer.masterplayer.presenter.music;

import android.content.Context;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicAlbum;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicAlbumView;

public class MusicAlbumPresenter extends BasePresenter<MusicAlbumView> {
    private Context context;
    private MusicDataRepository musicRepository;

    public MusicAlbumPresenter(Context context, MusicAlbumView musicAlbumView, MusicDataRepository musicDataRepository) {
        super(musicAlbumView);
        this.musicRepository = musicDataRepository;
        this.context = context;
    }

    // Method to load all music albums
    public void loadMusicAlbumList() {
        musicRepository.fetchAllMusicAlbums(new LoadDataListener<MusicAlbum>() {
            @Override
            public void onError() {
                // Handle error
            }

            @Override
            public void onSuccess(List<MusicAlbum> albumList) {
                if (mView != null) {
                    ((MusicAlbumView) MusicAlbumPresenter.this.mView).onUpdateAlbum(albumList);
                }
            }
        });
    }

    // Method to load all music tracks for a specific album
    public void loadMusicListForAlbum(MusicAlbum musicAlbum) {
        musicRepository.fetchMusicOfAlbum(new LoadDataListener<MusicInfo>() {
            @Override
            public void onError() {
                // Handle error
            }

            @Override
            public void onSuccess(List<MusicInfo> musicList) {
                if (mView != null) {
                    ((MusicAlbumView) MusicAlbumPresenter.this.mView).onUpdateMusicList(musicList);
//                    mView.onUpdateMusicList(musicList);
                }
            }
        }, musicAlbum);
    }


}
