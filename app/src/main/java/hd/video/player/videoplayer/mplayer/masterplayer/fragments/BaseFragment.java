package hd.video.player.videoplayer.mplayer.masterplayer.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.BasePresenter;

public abstract class BaseFragment<Presenter extends BasePresenter> extends Fragment {
    protected Presenter mPresenter;

    public abstract Presenter createPresenter();

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPresenter = createPresenter();
    }

    public void onDestroy() {
        super.onDestroy();
        BasePresenter basePresenter = this.mPresenter;
        if (basePresenter != null) {
            basePresenter.onDetach();
        }
    }
}
