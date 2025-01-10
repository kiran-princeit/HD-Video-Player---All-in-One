package hd.video.player.videoplayer.mplayer.masterplayer.presenter.video;

import android.content.Context;
import android.util.Log;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.VideoDialogView;

public class VideoDialogPresenter extends BasePresenter<VideoDialogView> {
    private Context mContext;
    private VideoDataRepository mVideoRepository;

    public VideoDialogPresenter(Context context, VideoDialogView videoDialogView, VideoDataRepository videoDataRepository) {
        super(videoDialogView);
        this.mVideoRepository = videoDataRepository;
        this.mContext = context;
    }

    public void getAllFavoriteVideo() {
        Log.d("Presenter", "Fetching favorite videos...");
        this.mVideoRepository.fetchAllFavoriteVideos(new LoadDataListener<VideoInfo>() {
            public void onError() {
                Log.d("VideoDialogPresenter", "Error fetching favorite videos");
            }

            public void onSuccess(List<VideoInfo> list) {
                Log.d("VideoDialogPresenter", "Fetched favorite videos: " + list.size());
                if (VideoDialogPresenter.this.mView != null) {
                    ((VideoDialogView) VideoDialogPresenter.this.mView).onVideoLoader(list);
                }
            }
        });
    }

    public void getAllRecentlyVideo() {
        this.mVideoRepository.fetchAllRecentlyWatchedVideos(new LoadDataListener<VideoInfo>() {
            public void onError() {
            }

            public void onSuccess(List<VideoInfo> list) {
                if (VideoDialogPresenter.this.mView != null) {
                    ((VideoDialogView) VideoDialogPresenter.this.mView).onVideoLoader(list);
                }
            }
        });
    }

    public void getAllVideoOfPlaylist(Playlist playlist) {
        this.mVideoRepository.fetchVideosInPlaylist(playlist, new LoadDataListener<VideoInfo>() {
            public void onError() {
            }

            public void onSuccess(List<VideoInfo> list) {
                if (VideoDialogPresenter.this.mView != null) {
                    ((VideoDialogView) VideoDialogPresenter.this.mView).onVideoLoader(list);
                }
            }
        });
    }

    public void getAllVideoOfFolder(VideoFolder videoFolder) {
        if (this.mView != null) {
            ((VideoDialogView) this.mView).onVideoLoader(videoFolder.getVideoList());
        }
    }
}
