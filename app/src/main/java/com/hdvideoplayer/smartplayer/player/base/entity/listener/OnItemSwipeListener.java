package com.hdvideoplayer.smartplayer.player.base.entity.listener;

import android.graphics.Canvas;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public interface OnItemSwipeListener {
    void clearView(ViewHolder viewHolder, int i);

    void onItemSwipeMoving(Canvas canvas, ViewHolder viewHolder, float f, float f2, boolean z);

    void onItemSwipeStart(ViewHolder viewHolder, int i);

    void onItemSwiped(ViewHolder viewHolder, int i);
}
