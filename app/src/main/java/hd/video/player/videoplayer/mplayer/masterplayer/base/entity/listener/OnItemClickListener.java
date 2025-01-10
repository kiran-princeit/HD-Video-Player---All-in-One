package hd.video.player.videoplayer.mplayer.masterplayer.base.entity.listener;

import android.view.View;
import hd.video.player.videoplayer.mplayer.masterplayer.base.entity.BaseQuickAdapter;

public abstract class OnItemClickListener extends SimpleClickListener {
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
    }

    public void onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
    }

    public void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
    }

    public abstract void onSimpleItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i);

    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        onSimpleItemClick(baseQuickAdapter, view, i);
    }
}
