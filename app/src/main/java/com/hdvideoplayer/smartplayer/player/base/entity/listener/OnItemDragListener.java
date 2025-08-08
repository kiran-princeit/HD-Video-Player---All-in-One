package com.hdvideoplayer.smartplayer.player.base.entity.listener;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public interface OnItemDragListener {
    void onItemDragEnd(ViewHolder viewHolder, int i);

    void onItemDragMoving(ViewHolder viewHolder, int i, ViewHolder viewHolder2, int i2);

    void onItemDragStart(ViewHolder viewHolder, int i);
}
