package com.hdvideoplayer.smartplayer.player.base.entity.diff;

import androidx.recyclerview.widget.DiffUtil.Callback;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseQuickDiffCallback<T> extends Callback {
    private List<T> newList;
    private List<T> oldList;

    public abstract boolean areContentsTheSame(T t, T t2);

    public abstract boolean areItemsTheSame(T t, T t2);

  
    public Object getChangePayload(T t, T t2) {
        return null;
    }

    public BaseQuickDiffCallback(List<T> list) {
        List list2 = null;
        if (list2 == null) {
            list2 = new ArrayList();
        }
        this.newList = list2;
    }

    public List<T> getNewList() {
        return this.newList;
    }

    public List<T> getOldList() {
        return this.oldList;
    }

    public void setOldList(List<T> list) {
        List list2 = null;
        if (list2 == null) {
            list2 = new ArrayList();
        }
        this.oldList = list2;
    }

    public int getOldListSize() {
        return this.oldList.size();
    }

    public int getNewListSize() {
        return this.newList.size();
    }

    public boolean areItemsTheSame(int i, int i2) {
        return areItemsTheSame(this.oldList.get(i), this.newList.get(i2));
    }

    public boolean areContentsTheSame(int i, int i2) {
        return areContentsTheSame(this.oldList.get(i), this.newList.get(i2));
    }

    public Object getChangePayload(int i, int i2) {
        return getChangePayload(this.oldList.get(i), this.newList.get(i2));
    }
}
