package hd.video.player.videoplayer.mplayer.masterplayer.presenter.video;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.video.VideoFolderView;

public class VideoFolderPresenter extends BasePresenter<VideoFolderView> {
    private VideoDataRepository mVideoRepository;

    public VideoFolderPresenter(VideoFolderView videoFolderView, VideoDataRepository videoDataRepository) {
        super(videoFolderView);
        this.mVideoRepository = videoDataRepository;
    }

    public void openFoldersTab() {
        this.mVideoRepository.fetchAllVideoFolders(new LoadDataListener<VideoFolder>() {
            public void onError() {
            }

            public void onSuccess(List<VideoFolder> list) {
                if (VideoFolderPresenter.this.mView != null) {
                    ((VideoFolderView) VideoFolderPresenter.this.mView).updateFolderList(list);
                }
            }
        });
    }
}
