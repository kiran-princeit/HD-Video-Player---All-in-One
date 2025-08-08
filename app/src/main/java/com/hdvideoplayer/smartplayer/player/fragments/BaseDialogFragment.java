package com.hdvideoplayer.smartplayer.player.fragments;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import com.hdvideoplayer.smartplayer.player.presenter.BasePresenter;

public abstract class BaseDialogFragment<Presenter extends BasePresenter> extends DialogFragment {
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
