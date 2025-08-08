package com.hdvideoplayer.smartplayer.player.base.entity.util;

import android.util.SparseIntArray;
import java.util.List;

public abstract class MultiTypeDelegate<T> {
    private static final int DEFAULT_VIEW_TYPE = -255;
    private boolean autoMode;
    private SparseIntArray layouts;
    private boolean selfMode;

    public abstract int getItemType(T t);

    public MultiTypeDelegate(SparseIntArray sparseIntArray) {
        this.layouts = sparseIntArray;
    }

    public final int getDefItemViewType(List<T> list, int i) {
        Object obj = list.get(i);
        return obj != null ? getItemType((T) obj) : DEFAULT_VIEW_TYPE;
    }

    public final int getLayoutId(int i) {
        return this.layouts.get(i, -404);
    }

}
