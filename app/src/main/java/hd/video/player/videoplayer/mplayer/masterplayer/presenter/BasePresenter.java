package hd.video.player.videoplayer.mplayer.masterplayer.presenter;

import hd.video.player.videoplayer.mplayer.masterplayer.view.BaseView;

public class BasePresenter<View extends BaseView> {
    protected View mView;

    public BasePresenter(View view) {
        this.mView = view;
    }

    public void onDetach() {
        this.mView = null;
    }
}
