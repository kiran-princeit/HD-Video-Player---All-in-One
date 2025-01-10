package hd.video.player.videoplayer.mplayer.masterplayer.base.diff;

import androidx.recyclerview.widget.ListUpdateCallback;
import hd.video.player.videoplayer.mplayer.masterplayer.base.BaseQuickAdapter;

public final class BaseQuickAdapterListUpdateCallback implements ListUpdateCallback {
    private final BaseQuickAdapter mAdapter;

    public BaseQuickAdapterListUpdateCallback(BaseQuickAdapter baseQuickAdapter) {
        this.mAdapter = baseQuickAdapter;
    }

    public void onInserted(int i, int i2) {
        BaseQuickAdapter baseQuickAdapter = this.mAdapter;
        baseQuickAdapter.notifyItemRangeInserted(i + baseQuickAdapter.getHeaderLayoutCount(), i2);
    }

    public void onRemoved(int i, int i2) {
        BaseQuickAdapter baseQuickAdapter = this.mAdapter;
        baseQuickAdapter.notifyItemRangeRemoved(i + baseQuickAdapter.getHeaderLayoutCount(), i2);
    }

    public void onMoved(int i, int i2) {
        BaseQuickAdapter baseQuickAdapter = this.mAdapter;
        baseQuickAdapter.notifyItemMoved(i + baseQuickAdapter.getHeaderLayoutCount(), i2 + this.mAdapter.getHeaderLayoutCount());
    }

    public void onChanged(int i, int i2, Object obj) {
        BaseQuickAdapter baseQuickAdapter = this.mAdapter;
        baseQuickAdapter.notifyItemRangeChanged(i + baseQuickAdapter.getHeaderLayoutCount(), i2, obj);
    }
}
