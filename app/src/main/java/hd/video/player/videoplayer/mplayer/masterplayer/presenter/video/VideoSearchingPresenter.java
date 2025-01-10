package hd.video.player.videoplayer.mplayer.masterplayer.presenter.video;

import android.content.Context;
import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.ILoaderRepository.LoadDataListener;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.VideoDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.view.video.VideoSearchView;

public class VideoSearchingPresenter extends BasePresenter<VideoSearchView> {
    private Context mContext;
    private VideoDataRepository mVideoRepository;

    public VideoSearchingPresenter(Context context, VideoSearchView videoSearchView, VideoDataRepository videoDataRepository) {
        super(videoSearchView);
        this.mVideoRepository = videoDataRepository;
        this.mContext = context;
    }

    public void searchVideoByVideoName(String str) {
        this.mVideoRepository.searchVideosByName(new LoadDataListener<VideoInfo>() {
            public void onError() {
            }

            public void onSuccess(List<VideoInfo> list) {
                if (VideoSearchingPresenter.this.mView != null) {
                    ((VideoSearchView) VideoSearchingPresenter.this.mView).onSearchVideo(list);
                }
            }
        }, str);
    }
}
