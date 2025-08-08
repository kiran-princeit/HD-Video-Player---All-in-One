package com.hdvideoplayer.smartplayer.player.base;

import android.util.SparseIntArray;
import android.view.ViewGroup;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.base.entity.IExpandable;
import com.hdvideoplayer.smartplayer.player.base.entity.MultiItemEntity;

public abstract class BaseMultiItemQuickAdapter<T extends MultiItemEntity, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {
    private static final int DEFAULT_VIEW_TYPE = -255;
    private SparseIntArray layouts;

    public BaseMultiItemQuickAdapter(List<T> list) {
        super((List) list);
    }

  
    public int getDefItemViewType(int i) {
        MultiItemEntity multiItemEntity = (MultiItemEntity) this.mData.get(i);
        return multiItemEntity != null ? multiItemEntity.getItemType() : DEFAULT_VIEW_TYPE;
    }
    public K onCreateDefViewHolder(ViewGroup viewGroup, int i) {
        return createBaseViewHolder(viewGroup, getLayoutId(i));
    }

    private int getLayoutId(int i) {
        return this.layouts.get(i, -404);
    }

    public void addItemType(int i, int i2) {
        if (this.layouts == null) {
            this.layouts = new SparseIntArray();
        }
        this.layouts.put(i, i2);
    }

    public void remove(int i) {
        if (this.mData != null && i >= 0 && i < this.mData.size()) {
            MultiItemEntity multiItemEntity = (MultiItemEntity) this.mData.get(i);
            if (multiItemEntity instanceof IExpandable) {
                removeAllChild((IExpandable) multiItemEntity, i);
            }
            removeDataFromParent((T) multiItemEntity);
            super.remove(i);
        }
    }

  
    public void removeAllChild(IExpandable iExpandable, int i) {
        if (iExpandable.isExpanded()) {
            List subItems = iExpandable.getSubItems();
            if (subItems != null && subItems.size() != 0) {
                int size = subItems.size();
                for (int i2 = 0; i2 < size; i2++) {
                    remove(i + 1);
                }
            }
        }
    }

    public void removeDataFromParent(T t) {
        int parentPosition = getParentPosition(t);
        if (parentPosition >= 0) {
            ((IExpandable) this.mData.get(parentPosition)).getSubItems().remove(t);
        }
    }
    public boolean isExpandable(MultiItemEntity multiItemEntity) {
        return multiItemEntity != null && (multiItemEntity instanceof IExpandable);
    }
}
