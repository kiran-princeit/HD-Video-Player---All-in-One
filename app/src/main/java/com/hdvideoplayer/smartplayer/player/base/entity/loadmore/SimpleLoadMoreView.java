package com.hdvideoplayer.smartplayer.player.base.entity.loadmore;

import com.hdvideoplayer.smartplayer.player.R;

public final class SimpleLoadMoreView extends LoadMoreView {
    public int getLayoutId() {
        return R.layout.brvah_quick_view_load_more;
    }

  
    public int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

  
    public int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

  
    public int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
