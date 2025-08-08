package com.hdvideoplayer.smartplayer.player.presenter.video;

import android.content.Context;
import android.util.Log;

import java.util.List;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoFolder;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.video.Playlist;
import com.hdvideoplayer.smartplayer.player.data.repository.ILoaderRepository.LoadDataListener;
import com.hdvideoplayer.smartplayer.player.data.repository.VideoDataRepository;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;
import com.hdvideoplayer.smartplayer.player.view.VideoDialogView;

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
