package com.hdvideoplayer.smartplayer.player.base.entity.listener;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hdvideoplayer.smartplayer.player.base.entity.BaseViewHolder;
import com.hdvideoplayer.smartplayer.player.base.entity.BaseQuickAdapter;

public abstract class SimpleClickListener implements OnItemTouchListener {
    public static String TAG = "SimpleClickListener";
    protected BaseQuickAdapter baseQuickAdapter;
    private GestureDetectorCompat mGestureDetector;
    private boolean mIsPrepressed = false;
    private boolean mIsShowPress = false;
    private View mPressedView = null;
    private RecyclerView recyclerView;

    private class ItemTouchHelperGestureListener implements OnGestureListener {
        private RecyclerView recyclerView;

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return false;
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            return false;
        }

        ItemTouchHelperGestureListener(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        public boolean onDown(MotionEvent motionEvent) {
            SimpleClickListener.this.mIsPrepressed = true;
            SimpleClickListener.this.mPressedView = this.recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            return false;
        }

        public void onShowPress(MotionEvent motionEvent) {
            if (SimpleClickListener.this.mIsPrepressed && SimpleClickListener.this.mPressedView != null) {
                SimpleClickListener.this.mIsShowPress = true;
            }
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (SimpleClickListener.this.mIsPrepressed && SimpleClickListener.this.mPressedView != null) {
                if (this.recyclerView.getScrollState() != 0) {
                    return false;
                }
                View access$100 = SimpleClickListener.this.mPressedView;
                BaseViewHolder baseViewHolder = (BaseViewHolder) this.recyclerView.getChildViewHolder(access$100);
                int adapterPosition = baseViewHolder.getAdapterPosition();
                if (adapterPosition == -1 || SimpleClickListener.this.isHeaderOrFooterPosition(adapterPosition)) {
                    return false;
                }
                adapterPosition -= SimpleClickListener.this.baseQuickAdapter.getHeaderLayoutCount();
                HashSet childClickViewIds = baseViewHolder.getChildClickViewIds();
                Set nestViews = baseViewHolder.getNestViews();
                Iterator it;
                View findViewById;
                SimpleClickListener simpleClickListener;
                if (childClickViewIds == null || childClickViewIds.size() <= 0) {
                    SimpleClickListener.this.setPressViewHotSpot(motionEvent, access$100);
                    SimpleClickListener.this.mPressedView.setPressed(true);
                    if (childClickViewIds != null && childClickViewIds.size() > 0) {
                        it = childClickViewIds.iterator();
                        while (it.hasNext()) {
                            findViewById = access$100.findViewById(((Integer) it.next()).intValue());
                            if (findViewById != null) {
                                findViewById.setPressed(false);
                            }
                        }
                    }
                    simpleClickListener = SimpleClickListener.this;
                    simpleClickListener.onItemClick(simpleClickListener.baseQuickAdapter, access$100, adapterPosition);
                } else {
                    Iterator it2 = childClickViewIds.iterator();
                    while (it2.hasNext()) {
                        Integer num = (Integer) it2.next();
                        View findViewById2 = access$100.findViewById(num.intValue());
                        if (findViewById2 != null) {
                            if (!SimpleClickListener.this.inRangeOfView(findViewById2, motionEvent) || !findViewById2.isEnabled()) {
                                findViewById2.setPressed(false);
                            } else if (nestViews != null && nestViews.contains(num)) {
                                return false;
                            } else {
                                SimpleClickListener.this.setPressViewHotSpot(motionEvent, findViewById2);
                                findViewById2.setPressed(true);
                                simpleClickListener = SimpleClickListener.this;
                                simpleClickListener.onItemChildClick(simpleClickListener.baseQuickAdapter, findViewById2, adapterPosition);
                                resetPressedView(findViewById2);
                                return true;
                            }
                        }
                    }
                    SimpleClickListener.this.setPressViewHotSpot(motionEvent, access$100);
                    SimpleClickListener.this.mPressedView.setPressed(true);
                    it = childClickViewIds.iterator();
                    while (it.hasNext()) {
                        findViewById = access$100.findViewById(((Integer) it.next()).intValue());
                        if (findViewById != null) {
                            findViewById.setPressed(false);
                        }
                    }
                    simpleClickListener = SimpleClickListener.this;
                    simpleClickListener.onItemClick(simpleClickListener.baseQuickAdapter, access$100, adapterPosition);
                }
                resetPressedView(access$100);
            }
            return true;
        }

        private void resetPressedView(final View view) {
            if (view != null) {
                view.postDelayed(new Runnable() {
                    public void run() {
                        View view1 = view;
                        if (view1 != null) {
                            view1.setPressed(false);
                        }
                    }
                }, 50);
            }
            SimpleClickListener.this.mIsPrepressed = false;
            SimpleClickListener.this.mPressedView = null;
        }

        public void onLongPress(MotionEvent motionEvent) {
            if (this.recyclerView.getScrollState() == 0 && SimpleClickListener.this.mIsPrepressed && SimpleClickListener.this.mPressedView != null) {
                SimpleClickListener.this.mPressedView.performHapticFeedback(0);
                BaseViewHolder baseViewHolder = (BaseViewHolder) this.recyclerView.getChildViewHolder(SimpleClickListener.this.mPressedView);
                int adapterPosition = baseViewHolder.getAdapterPosition();
                if (adapterPosition != -1 && !SimpleClickListener.this.isHeaderOrFooterPosition(adapterPosition)) {
                    HashSet itemChildLongClickViewIds = baseViewHolder.getItemChildLongClickViewIds();
                    Set nestViews = baseViewHolder.getNestViews();
                    if (itemChildLongClickViewIds != null && itemChildLongClickViewIds.size() > 0) {
                        Iterator it = itemChildLongClickViewIds.iterator();
                        while (it.hasNext()) {
                            Integer num = (Integer) it.next();
                            View findViewById = SimpleClickListener.this.mPressedView.findViewById(num.intValue());
                            if (SimpleClickListener.this.inRangeOfView(findViewById, motionEvent) && findViewById.isEnabled()) {
                                SimpleClickListener simpleClickListener;
                                if (nestViews == null || !nestViews.contains(num)) {
                                    SimpleClickListener.this.setPressViewHotSpot(motionEvent, findViewById);
                                    simpleClickListener = SimpleClickListener.this;
                                    simpleClickListener.onItemChildLongClick(simpleClickListener.baseQuickAdapter, findViewById, adapterPosition - SimpleClickListener.this.baseQuickAdapter.getHeaderLayoutCount());
                                    findViewById.setPressed(true);
                                    SimpleClickListener.this.mIsShowPress = true;
                                }
                                simpleClickListener = SimpleClickListener.this;
                                simpleClickListener.onItemLongClick(simpleClickListener.baseQuickAdapter, SimpleClickListener.this.mPressedView, adapterPosition - SimpleClickListener.this.baseQuickAdapter.getHeaderLayoutCount());
                                simpleClickListener = SimpleClickListener.this;
                                simpleClickListener.setPressViewHotSpot(motionEvent, simpleClickListener.mPressedView);
                                SimpleClickListener.this.mPressedView.setPressed(true);
                                if (itemChildLongClickViewIds != null) {
                                    Iterator it2 = itemChildLongClickViewIds.iterator();
                                    while (it2.hasNext()) {
                                        View findViewById2 = SimpleClickListener.this.mPressedView.findViewById(((Integer) it2.next()).intValue());
                                        if (findViewById2 != null) {
                                            findViewById2.setPressed(false);
                                        }
                                    }
                                }
                                SimpleClickListener.this.mIsShowPress = true;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isHeaderOrFooterView(int i) {
        return i == 1365 || i == 273 || i == 819 || i == 546;
    }

    public abstract void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i);

    public abstract void onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i);

    public abstract void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i);

    public abstract void onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i);

    public void onRequestDisallowInterceptTouchEvent(boolean z) {
    }

    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        RecyclerView recyclerView2 = this.recyclerView;
        if (recyclerView2 == null) {
            this.recyclerView = recyclerView;
            this.baseQuickAdapter = (BaseQuickAdapter) recyclerView.getAdapter();
            this.mGestureDetector = new GestureDetectorCompat(this.recyclerView.getContext(), new ItemTouchHelperGestureListener(this.recyclerView));
        } else if (recyclerView2 != recyclerView) {
            this.recyclerView = recyclerView;
            this.baseQuickAdapter = (BaseQuickAdapter) recyclerView.getAdapter();
            this.mGestureDetector = new GestureDetectorCompat(this.recyclerView.getContext(), new ItemTouchHelperGestureListener(this.recyclerView));
        }
        if (!this.mGestureDetector.onTouchEvent(motionEvent) && motionEvent.getActionMasked() == 1 && this.mIsShowPress) {
            View view = this.mPressedView;
            if (view != null) {
                BaseViewHolder baseViewHolder = (BaseViewHolder) this.recyclerView.getChildViewHolder(view);
                if (baseViewHolder == null || !isHeaderOrFooterView(baseViewHolder.getItemViewType())) {
                    this.mPressedView.setPressed(false);
                }
            }
            this.mIsShowPress = false;
            this.mIsPrepressed = false;
        }
        return false;
    }

    public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
        this.mGestureDetector.onTouchEvent(motionEvent);
    }

    public void setPressViewHotSpot(MotionEvent motionEvent, View view) {
        if (view != null && view.getBackground() != null) {
            view.getBackground().setHotspot(motionEvent.getRawX(), motionEvent.getY() - view.getY());
        }
    }

    public boolean inRangeOfView(View view, MotionEvent motionEvent) {
        int[] iArr = new int[2];
        if (view != null && view.isShown()) {
            view.getLocationOnScreen(iArr);
            int i = iArr[0];
            int i2 = iArr[1];
            if (motionEvent.getRawX() < ((float) i) || motionEvent.getRawX() > ((float) (i + view.getWidth())) || motionEvent.getRawY() < ((float) i2) || motionEvent.getRawY() > ((float) (i2 + view.getHeight()))) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isHeaderOrFooterPosition(int i) {
        boolean z = false;
        if (this.baseQuickAdapter == null) {
            RecyclerView recyclerView = this.recyclerView;
            if (recyclerView == null) {
                return false;
            }
            this.baseQuickAdapter = (BaseQuickAdapter) recyclerView.getAdapter();
        }
        i = this.baseQuickAdapter.getItemViewType(i);
        if (i == 1365 || i == 273 || i == 819 || i == 546) {
            z = true;
        }
        return z;
    }
}
