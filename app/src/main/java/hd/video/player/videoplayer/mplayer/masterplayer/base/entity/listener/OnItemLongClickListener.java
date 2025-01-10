package hd.video.player.videoplayer.mplayer.masterplayer.base.entity.listener;

import android.view.View;
import hd.video.player.videoplayer.mplayer.masterplayer.base.entity.BaseQuickAdapter;

public abstract class OnItemLongClickListener extends SimpleClickListener {
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
    }

    public void onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
    }

    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
    }

    public abstract void onSimpleItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i);

    public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        onSimpleItemLongClick(baseQuickAdapter, view, i);
    }
}
