package hd.video.player.videoplayer.mplayer.masterplayer.presenter.music;

import android.content.Context;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicArtistView;



public class MusicArtistPresenter extends BasePresenter<MusicArtistView> {
    private Context context;
    private MusicDataRepository musicRepository;

    public MusicArtistPresenter(Context context, MusicArtistView musicArtistView, MusicDataRepository musicDataRepository) {
        super(musicArtistView);
        this.musicRepository = musicDataRepository;
        this.context = context;
    }

    /**
     * Loads the music artists and updates the view.
     */
    public void loadArtists() {
        this.musicRepository.fetchAllArtists(new LoadDataListener<MusicArtist>() {
            @Override
            public void onError() {
                // Handle error if needed
            }

            @Override
            public void onSuccess(List<MusicArtist> artists) {
                if (MusicArtistPresenter.this.mView != null) {
                    ((MusicArtistView) MusicArtistPresenter.this.mView).onOpenArtist(artists);
                }
            }
        });
    }
}

