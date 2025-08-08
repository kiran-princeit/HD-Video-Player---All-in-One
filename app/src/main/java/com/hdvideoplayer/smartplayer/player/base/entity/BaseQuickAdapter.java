package com.hdvideoplayer.smartplayer.player.base.entity;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hdvideoplayer.smartplayer.player.base.entity.animation.AlphaInAnimation;
import com.hdvideoplayer.smartplayer.player.base.entity.animation.BaseAnimation;
import com.hdvideoplayer.smartplayer.player.base.entity.animation.ScaleInAnimation;
import com.hdvideoplayer.smartplayer.player.base.entity.animation.SlideInBottomAnimation;
import com.hdvideoplayer.smartplayer.player.base.entity.animation.SlideInLeftAnimation;
import com.hdvideoplayer.smartplayer.player.base.entity.animation.SlideInRightAnimation;
import com.hdvideoplayer.smartplayer.player.base.entity.diff.BaseQuickAdapterListUpdateCallback;
import com.hdvideoplayer.smartplayer.player.base.entity.diff.BaseQuickDiffCallback;
import com.hdvideoplayer.smartplayer.player.base.entity.loadmore.LoadMoreView;
import com.hdvideoplayer.smartplayer.player.base.entity.loadmore.SimpleLoadMoreView;
import com.hdvideoplayer.smartplayer.player.base.entity.util.MultiTypeDelegate;

public abstract class BaseQuickAdapter<T, K extends BaseViewHolder> extends Adapter<K> {
    public static final int ALPHAIN = 1;
    public static final int EMPTY_VIEW = 1365;
    public static final int FOOTER_VIEW = 819;
    public static final int HEADER_VIEW = 273;
    public static final int LOADING_VIEW = 546;
    public static final int SCALEIN = 2;
    public static final int SLIDEIN_BOTTOM = 3;
    public static final int SLIDEIN_LEFT = 4;
    public static final int SLIDEIN_RIGHT = 5;
    protected static final String TAG = "BaseQuickAdapter";
    private boolean footerViewAsFlow;
    private boolean headerViewAsFlow;
    protected Context mContext;
    private BaseAnimation mCustomAnimation;
    protected List<T> mData;
    private int mDuration;
    private FrameLayout mEmptyLayout;
    private boolean mEnableLoadMoreEndClick;
    private boolean mFirstOnlyEnable;
    private boolean mFootAndEmptyEnable;
    private LinearLayout mFooterLayout;
    private boolean mHeadAndEmptyEnable;
    private LinearLayout mHeaderLayout;
    private Interpolator mInterpolator;
    private boolean mIsUseEmpty;
    private int mLastPosition;
    protected LayoutInflater mLayoutInflater;
    protected int mLayoutResId;
    private boolean mLoadMoreEnable;
    private LoadMoreView mLoadMoreView;
    private boolean mLoading;
    private MultiTypeDelegate<T> mMultiTypeDelegate;
    private boolean mNextLoadEnable;
    private OnItemChildClickListener mOnItemChildClickListener;
    private OnItemChildLongClickListener mOnItemChildLongClickListener;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private boolean mOpenAnimationEnable;
    private int mPreLoadNumber;
    private RecyclerView mRecyclerView;
    private RequestLoadMoreListener mRequestLoadMoreListener;
    private BaseAnimation mSelectAnimation;
    private SpanSizeLookup mSpanSizeLookup;
    private int mStartUpFetchPosition;
    private boolean mUpFetchEnable;
    private UpFetchListener mUpFetchListener;
    private boolean mUpFetching;

    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType {
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i);
    }

    public interface OnItemChildLongClickListener {
        boolean onItemChildLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i);
    }

    public interface OnItemClickListener {
        void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(BaseQuickAdapter baseQuickAdapter, View view, int i);
    }

    public interface RequestLoadMoreListener {
        void onLoadMoreRequested();
    }

    public interface SpanSizeLookup {
        int getSpanSize(GridLayoutManager gridLayoutManager, int i);
    }

    public interface UpFetchListener {
        void onUpFetch();
    }

    public abstract void convert(K k, T t);

  
    public void convertPayloads(K k, T t, List<Object> list) {
    }

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean isFixedViewType(int i) {
        return i == 1365 || i == 273 || i == 819 || i == 546;
    }

    public BaseQuickAdapter(int i, List<T> list) {
        List list2 = null;
        this.mNextLoadEnable = false;
        this.mLoadMoreEnable = false;
        this.mLoading = false;
        this.mLoadMoreView = new SimpleLoadMoreView();
        this.mEnableLoadMoreEndClick = false;
        this.mFirstOnlyEnable = true;
        this.mOpenAnimationEnable = false;
        this.mInterpolator = new LinearInterpolator();
        this.mDuration = 300;
        this.mLastPosition = -1;
        this.mSelectAnimation = new AlphaInAnimation();
        this.mIsUseEmpty = true;
        this.mPreLoadNumber = 1;
        this.mStartUpFetchPosition = 1;
        if (list2 == null) {
            list2 = new ArrayList();
        }
        this.mData = list2;
        if (i != 0) {
            this.mLayoutResId = i;
        }
    }

    public BaseQuickAdapter(List<T> list) {
        this(0, list);
    }

    public BaseQuickAdapter(int i) {
        this(i, null);
    }

  
    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    private void setRecyclerView(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    private void checkNotNull() {
        if (getRecyclerView() == null) {
            throw new IllegalStateException("please bind recyclerView first!");
        }
    }

    public void bindToRecyclerView(RecyclerView recyclerView) {
        if (getRecyclerView() == null) {
            setRecyclerView(recyclerView);
            getRecyclerView().setAdapter(this);
            return;
        }
        throw new IllegalStateException("Don't bind twice");
    }

    @Deprecated
    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener) {
        openLoadMore(requestLoadMoreListener);
    }

    private void openLoadMore(RequestLoadMoreListener requestLoadMoreListener) {
        this.mRequestLoadMoreListener = requestLoadMoreListener;
        this.mNextLoadEnable = true;
        this.mLoadMoreEnable = true;
        this.mLoading = false;
    }

    public void setOnLoadMoreListener(RequestLoadMoreListener requestLoadMoreListener, RecyclerView recyclerView) {
        openLoadMore(requestLoadMoreListener);
        if (getRecyclerView() == null) {
            setRecyclerView(recyclerView);
        }
    }

    public void disableLoadMoreIfNotFullPage() {
        checkNotNull();
        disableLoadMoreIfNotFullPage(getRecyclerView());
    }

    public void disableLoadMoreIfNotFullPage(RecyclerView recyclerView) {
        setEnableLoadMore(false);
        if (recyclerView != null) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null) {
                if (layoutManager instanceof LinearLayoutManager) {
                    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    recyclerView.postDelayed(new Runnable() {
                        public void run() {
                            if (BaseQuickAdapter.this.isFullScreen(linearLayoutManager)) {
                                BaseQuickAdapter.this.setEnableLoadMore(true);
                            }
                        }
                    }, 50);
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    recyclerView.postDelayed(new Runnable() {
                        public void run() {
                            int[] iArr = new int[staggeredGridLayoutManager.getSpanCount()];
                            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(iArr);
                            if (BaseQuickAdapter.this.getTheBiggestNumber(iArr) + 1 != BaseQuickAdapter.this.getItemCount()) {
                                BaseQuickAdapter.this.setEnableLoadMore(true);
                            }
                        }
                    }, 50);
                }
            }
        }
    }

    public boolean isFullScreen(LinearLayoutManager linearLayoutManager) {
        return (linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1 == getItemCount() && linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) ? false : true;
    }

    public int getTheBiggestNumber(int[] iArr) {
        int i = -1;
        if (!(iArr == null || iArr.length == 0)) {
            for (int i2 : iArr) {
                if (i2 > i) {
                    i = i2;
                }
            }
        }
        return i;
    }

    public boolean isUpFetchEnable() {
        return this.mUpFetchEnable;
    }

    public void setUpFetchEnable(boolean z) {
        this.mUpFetchEnable = z;
    }

    public void setStartUpFetchPosition(int i) {
        this.mStartUpFetchPosition = i;
    }

    private void autoUpFetch(int i) {
        if (isUpFetchEnable() && !isUpFetching() && i <= this.mStartUpFetchPosition) {
            UpFetchListener upFetchListener = this.mUpFetchListener;
            if (upFetchListener != null) {
                upFetchListener.onUpFetch();
            }
        }
    }

    public boolean isUpFetching() {
        return this.mUpFetching;
    }

    public void setUpFetching(boolean z) {
        this.mUpFetching = z;
    }

    public void setUpFetchListener(UpFetchListener upFetchListener) {
        this.mUpFetchListener = upFetchListener;
    }

    public void setNotDoAnimationCount(int i) {
        this.mLastPosition = i;
    }

    public void setLoadMoreView(LoadMoreView loadMoreView) {
        this.mLoadMoreView = loadMoreView;
    }

    public int getLoadMoreViewCount() {
        if (this.mRequestLoadMoreListener == null || !this.mLoadMoreEnable) {
            return 0;
        }
        if ((this.mNextLoadEnable || !this.mLoadMoreView.isLoadEndMoreGone()) && this.mData.size() != 0) {
            return 1;
        }
        return 0;
    }

    public int getLoadMoreViewPosition() {
        return (getHeaderLayoutCount() + this.mData.size()) + getFooterLayoutCount();
    }

    public boolean isLoading() {
        return this.mLoading;
    }

    public void loadMoreEnd() {
        loadMoreEnd(false);
    }

    public void loadMoreEnd(boolean z) {
        if (getLoadMoreViewCount() != 0) {
            this.mLoading = false;
            this.mNextLoadEnable = false;
            this.mLoadMoreView.setLoadMoreEndGone(z);
            if (z) {
                notifyItemRemoved(getLoadMoreViewPosition());
                return;
            }
            this.mLoadMoreView.setLoadMoreStatus(4);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }

    public void loadMoreComplete() {
        if (getLoadMoreViewCount() != 0) {
            this.mLoading = false;
            this.mNextLoadEnable = true;
            this.mLoadMoreView.setLoadMoreStatus(1);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }

    public void loadMoreFail() {
        if (getLoadMoreViewCount() != 0) {
            this.mLoading = false;
            this.mLoadMoreView.setLoadMoreStatus(3);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }

    public void setEnableLoadMore(boolean z) {
        int loadMoreViewCount = getLoadMoreViewCount();
        this.mLoadMoreEnable = z;
        int loadMoreViewCount2 = getLoadMoreViewCount();
        if (loadMoreViewCount == 1) {
            if (loadMoreViewCount2 == 0) {
                notifyItemRemoved(getLoadMoreViewPosition());
            }
        } else if (loadMoreViewCount2 == 1) {
            this.mLoadMoreView.setLoadMoreStatus(1);
            notifyItemInserted(getLoadMoreViewPosition());
        }
    }

    public boolean isLoadMoreEnable() {
        return this.mLoadMoreEnable;
    }

    public void setDuration(int i) {
        this.mDuration = i;
    }

    public final void refreshNotifyItemChanged(int i) {
        notifyItemChanged(i + getHeaderLayoutCount());
    }

    public void setNewData(List<T> list) {
        List list2 = null;
        if (list2 == null) {
            list2 = new ArrayList();
        }
        this.mData = list2;
        if (this.mRequestLoadMoreListener != null) {
            this.mNextLoadEnable = true;
            this.mLoadMoreEnable = true;
            this.mLoading = false;
            this.mLoadMoreView.setLoadMoreStatus(1);
        }
        this.mLastPosition = -1;
        notifyDataSetChanged();
    }

    public void setNewDiffData(BaseQuickDiffCallback<T> baseQuickDiffCallback) {
        setNewDiffData(baseQuickDiffCallback, false);
    }

    public void setNewDiffData(BaseQuickDiffCallback<T> baseQuickDiffCallback, boolean z) {
        if (getEmptyViewCount() == 1) {
            setNewData(baseQuickDiffCallback.getNewList());
            return;
        }
        baseQuickDiffCallback.setOldList(getData());
        DiffUtil.calculateDiff(baseQuickDiffCallback, z).dispatchUpdatesTo(new BaseQuickAdapterListUpdateCallback(this));
        this.mData = baseQuickDiffCallback.getNewList();
    }

    @Deprecated
    public void add(int i, T t) {
        addData(i, (T) t);
    }

    public void addData(int i, T t) {
        this.mData.add(i, t);
        notifyItemInserted(i + getHeaderLayoutCount());
        compatibilityDataSizeChanged(1);
    }

    public void addData(T t) {
        this.mData.add(t);
        notifyItemInserted(this.mData.size() + getHeaderLayoutCount());
        compatibilityDataSizeChanged(1);
    }

    public void remove(int i) {
        this.mData.remove(i);
        i += getHeaderLayoutCount();
        notifyItemRemoved(i);
        compatibilityDataSizeChanged(0);
        notifyItemRangeChanged(i, this.mData.size() - i);
    }

    public void setData(int i, T t) {
        this.mData.set(i, t);
        notifyItemChanged(i + getHeaderLayoutCount());
    }

    public void addData(int i, Collection<? extends T> collection) {
        this.mData.addAll(i, collection);
        notifyItemRangeInserted(i + getHeaderLayoutCount(), collection.size());
        compatibilityDataSizeChanged(collection.size());
    }

    public void addData(Collection<? extends T> collection) {
        this.mData.addAll(collection);
        notifyItemRangeInserted((this.mData.size() - collection.size()) + getHeaderLayoutCount(), collection.size());
        compatibilityDataSizeChanged(collection.size());
    }

    public void replaceData(Collection<? extends T> collection) {
        Collection<? extends T> collection2 = this.mData;
        if (collection != collection2) {
            collection2.clear();
            this.mData.addAll(collection);
        }
        notifyDataSetChanged();
    }

    private void compatibilityDataSizeChanged(int i) {
        int i2;
        List list = this.mData;
        if (list == null) {
            i2 = 0;
        } else {
            i2 = list.size();
        }
        if (i2 == i) {
            notifyDataSetChanged();
        }
    }

    public List<T> getData() {
        return this.mData;
    }

    public T getItem(int i) {
        return (i < 0 || i >= this.mData.size()) ? null : this.mData.get(i);
    }

    @Deprecated
    public int getHeaderViewsCount() {
        return getHeaderLayoutCount();
    }

    @Deprecated
    public int getFooterViewsCount() {
        return getFooterLayoutCount();
    }

    public int getHeaderLayoutCount() {
        LinearLayout linearLayout = this.mHeaderLayout;
        return (linearLayout == null || linearLayout.getChildCount() == 0) ? 0 : 1;
    }

    public int getFooterLayoutCount() {
        LinearLayout linearLayout = this.mFooterLayout;
        return (linearLayout == null || linearLayout.getChildCount() == 0) ? 0 : 1;
    }

    public int getEmptyViewCount() {
        FrameLayout frameLayout = this.mEmptyLayout;
        return (frameLayout == null || frameLayout.getChildCount() == 0 || !this.mIsUseEmpty || this.mData.size() != 0) ? 0 : 1;
    }

    public int getItemCount() {
        int i = 1;
        if (1 != getEmptyViewCount()) {
            return ((getLoadMoreViewCount() + getHeaderLayoutCount()) + this.mData.size()) + getFooterLayoutCount();
        }
        if (this.mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
            i = 2;
        }
        if (this.mFootAndEmptyEnable && getFooterLayoutCount() != 0) {
            i++;
        }
        return i;
    }

    public int getItemViewType(int i) {
        int i2 = 819;
        if (getEmptyViewCount() == 1) {
            Object obj = (!this.mHeadAndEmptyEnable || getHeaderLayoutCount() == 0) ? null : 1;
            if (i != 0) {
                return i != 1 ? i2 : i2;
            } else if (obj != null) {
                return 273;
            } else {
                return 1365;
            }
        }
        int headerLayoutCount = getHeaderLayoutCount();
        if (i < headerLayoutCount) {
            return 273;
        }
        i -= headerLayoutCount;
        headerLayoutCount = this.mData.size();
        if (i < headerLayoutCount) {
            return getDefItemViewType(i);
        }
        if (i - headerLayoutCount >= getFooterLayoutCount()) {
            i2 = 546;
        }
        return i2;
    }

  
    public int getDefItemViewType(int i) {
        MultiTypeDelegate multiTypeDelegate = this.mMultiTypeDelegate;
        if (multiTypeDelegate != null) {
            return multiTypeDelegate.getDefItemViewType(this.mData, i);
        }
        return super.getItemViewType(i);
    }

    public K onCreateViewHolder(ViewGroup viewGroup, int i) {
        BaseViewHolder createBaseViewHolder;
        Context context = viewGroup.getContext();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        if (i == 273) {
            createBaseViewHolder = createBaseViewHolder(this.mHeaderLayout);
        } else if (i == 546) {
            createBaseViewHolder = getLoadingView(viewGroup);
        } else if (i == 819) {
            createBaseViewHolder = createBaseViewHolder(this.mFooterLayout);
        } else if (i == 1365) {
            createBaseViewHolder = createBaseViewHolder(this.mEmptyLayout);
        } else {
            createBaseViewHolder = onCreateDefViewHolder(viewGroup, i);
            bindViewClickListener(createBaseViewHolder);
        }
        createBaseViewHolder.setAdapter(this);
        return (K) createBaseViewHolder;
    }

    private K getLoadingView(ViewGroup viewGroup) {
        BaseViewHolder createBaseViewHolder = createBaseViewHolder(getItemView(this.mLoadMoreView.getLayoutId(), viewGroup));
        createBaseViewHolder.itemView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (BaseQuickAdapter.this.mLoadMoreView.getLoadMoreStatus() == 3) {
                    BaseQuickAdapter.this.notifyLoadMoreToLoading();
                }
                if (BaseQuickAdapter.this.mEnableLoadMoreEndClick && BaseQuickAdapter.this.mLoadMoreView.getLoadMoreStatus() == 4) {
                    BaseQuickAdapter.this.notifyLoadMoreToLoading();
                }
            }
        });
        return (K) createBaseViewHolder;
    }

    public void notifyLoadMoreToLoading() {
        if (this.mLoadMoreView.getLoadMoreStatus() != 2) {
            this.mLoadMoreView.setLoadMoreStatus(1);
            notifyItemChanged(getLoadMoreViewPosition());
        }
    }

    public void enableLoadMoreEndClick(boolean z) {
        this.mEnableLoadMoreEndClick = z;
    }

    public void onViewAttachedToWindow(K k) {
        super.onViewAttachedToWindow(k);
        int itemViewType = k.getItemViewType();
        if (itemViewType == 1365 || itemViewType == 273 || itemViewType == 819 || itemViewType == 546) {
            setFullSpan(k);
        } else {
            addAnimation(k);
        }
    }

    public void setFullSpan(ViewHolder viewHolder) {
        if (viewHolder.itemView.getLayoutParams() instanceof LayoutParams) {
            ((LayoutParams) viewHolder.itemView.getLayoutParams()).setFullSpan(true);
        }
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
                public int getSpanSize(int i) {
                    int itemViewType = BaseQuickAdapter.this.getItemViewType(i);
                    if (itemViewType == 273 && BaseQuickAdapter.this.isHeaderViewAsFlow()) {
                        return 1;
                    }
                    if (itemViewType == 819 && BaseQuickAdapter.this.isFooterViewAsFlow()) {
                        return 1;
                    }
                    if (BaseQuickAdapter.this.mSpanSizeLookup != null) {
                        return BaseQuickAdapter.this.isFixedViewType(itemViewType) ? gridLayoutManager.getSpanCount() : BaseQuickAdapter.this.mSpanSizeLookup.getSpanSize(gridLayoutManager, i - BaseQuickAdapter.this.getHeaderLayoutCount());
                    } else if (BaseQuickAdapter.this.isFixedViewType(itemViewType)) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    public boolean isHeaderViewAsFlow() {
        return this.headerViewAsFlow;
    }

    public void setHeaderViewAsFlow(boolean z) {
        this.headerViewAsFlow = z;
    }

    public boolean isFooterViewAsFlow() {
        return this.footerViewAsFlow;
    }

    public void setFooterViewAsFlow(boolean z) {
        this.footerViewAsFlow = z;
    }

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
        this.mSpanSizeLookup = spanSizeLookup;
    }

    public void onBindViewHolder(K k, int i) {
        autoUpFetch(i);
        autoLoadMore(i);
        int itemViewType = k.getItemViewType();
        if (itemViewType == 0) {
            convert(k, getItem(i - getHeaderLayoutCount()));
        } else if (itemViewType == 273) {
        } else {
            if (itemViewType == 546) {
                this.mLoadMoreView.convert(k);
            } else if (itemViewType != 819 && itemViewType != 1365) {
                convert(k, getItem(i - getHeaderLayoutCount()));
            }
        }
    }

    public void onBindViewHolder(K k, int i, List<Object> list) {
        if (list.isEmpty()) {
            onBindViewHolder((K) k, i);
            return;
        }
        autoUpFetch(i);
        autoLoadMore(i);
        int itemViewType = k.getItemViewType();
        if (itemViewType == 0) {
            convertPayloads(k, getItem(i - getHeaderLayoutCount()), list);
        } else if (itemViewType != 273) {
            if (itemViewType == 546) {
                this.mLoadMoreView.convert(k);
            } else if (!(itemViewType == 819 || itemViewType == 1365)) {
                convertPayloads(k, getItem(i - getHeaderLayoutCount()), list);
            }
        }
    }

    private void bindViewClickListener(final BaseViewHolder baseViewHolder) {
        if (baseViewHolder != null) {
            View view = baseViewHolder.itemView;
            if (getOnItemClickListener() != null) {
                view.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        int adapterPosition = baseViewHolder.getAdapterPosition();
                        if (adapterPosition != -1) {
                            BaseQuickAdapter baseQuickAdapter = BaseQuickAdapter.this;
                            baseQuickAdapter.setOnItemClick(view, adapterPosition - baseQuickAdapter.getHeaderLayoutCount());
                        }
                    }
                });
            }
            if (getOnItemLongClickListener() != null) {
                view.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        int adapterPosition = baseViewHolder.getAdapterPosition();
                        if (adapterPosition == -1) {
                            return false;
                        }
                        BaseQuickAdapter baseQuickAdapter = BaseQuickAdapter.this;
                        return baseQuickAdapter.setOnItemLongClick(view, adapterPosition - baseQuickAdapter.getHeaderLayoutCount());
                    }
                });
            }
        }
    }

    public void setOnItemClick(View view, int i) {
        getOnItemClickListener().onItemClick(this, view, i);
    }

    public boolean setOnItemLongClick(View view, int i) {
        return getOnItemLongClickListener().onItemLongClick(this, view, i);
    }

    public MultiTypeDelegate<T> getMultiTypeDelegate() {
        return this.mMultiTypeDelegate;
    }

    public void setMultiTypeDelegate(MultiTypeDelegate<T> multiTypeDelegate) {
        this.mMultiTypeDelegate = multiTypeDelegate;
    }

    public K onCreateDefViewHolder(ViewGroup viewGroup, int i) {
        int i2 = this.mLayoutResId;
        MultiTypeDelegate multiTypeDelegate = this.mMultiTypeDelegate;
        if (multiTypeDelegate != null) {
            i2 = multiTypeDelegate.getLayoutId(i);
        }
        return createBaseViewHolder(viewGroup, i2);
    }

    public K createBaseViewHolder(ViewGroup viewGroup, int i) {
        return createBaseViewHolder(getItemView(i, viewGroup));
    }

    public K createBaseViewHolder(View view) {
        K baseViewHolder;
        Class cls = getClass();
        Class cls2 = null;
        while (cls2 == null && cls != null) {
            cls2 = getInstancedGenericKClass(cls);
            cls = cls.getSuperclass();
        }
        if (cls2 == null) {
            baseViewHolder = (K) new BaseViewHolder(view);
        } else {
            baseViewHolder = createGenericKInstance(cls2, view);
        }
        return baseViewHolder != null ? baseViewHolder : (K) new BaseViewHolder(view);
    }

    private K createGenericKInstance(Class cls, View view) {
        try {
            Constructor declaredConstructor;
            if (!cls.isMemberClass() || Modifier.isStatic(cls.getModifiers())) {
                declaredConstructor = cls.getDeclaredConstructor(new Class[]{View.class});
                declaredConstructor.setAccessible(true);
                return (K) declaredConstructor.newInstance(new Object[]{view});
            }
            declaredConstructor = cls.getDeclaredConstructor(new Class[]{getClass(), View.class});
            declaredConstructor.setAccessible(true);
            return (K) declaredConstructor.newInstance(new Object[]{this, view});
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e2) {
            e2.printStackTrace();
            return null;
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
            return null;
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
            return null;
        }
    }

    private Class getInstancedGenericKClass(Class cls) {
        Type genericSuperclass = cls.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            for (Type type : ((ParameterizedType) genericSuperclass).getActualTypeArguments()) {
                Type type2 = null;
                Class cls2;
                if (type2 instanceof Class) {
                    cls2 = (Class) type2;
                    if (BaseViewHolder.class.isAssignableFrom(cls2)) {
                        return cls2;
                    }
                } else if (type2 instanceof ParameterizedType) {
                    type2 = ((ParameterizedType) type2).getRawType();
                    if (type2 instanceof Class) {
                        cls2 = (Class) type2;
                        if (BaseViewHolder.class.isAssignableFrom(cls2)) {
                            return cls2;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        return null;
    }

    public LinearLayout getHeaderLayout() {
        return this.mHeaderLayout;
    }

    public LinearLayout getFooterLayout() {
        return this.mFooterLayout;
    }

    public int addHeaderView(View view) {
        return addHeaderView(view, -1);
    }

    public int addHeaderView(View view, int i) {
        return addHeaderView(view, i, 1);
    }

    public int addHeaderView(View view, int i, int i2) {
        if (this.mHeaderLayout == null) {
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            this.mHeaderLayout = linearLayout;
            if (i2 == 1) {
                linearLayout.setOrientation(1);
                this.mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else {
                linearLayout.setOrientation(0);
                this.mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(-2, -1));
            }
        }
        i2 = this.mHeaderLayout.getChildCount();
        if (i < 0 || i > i2) {
            i = i2;
        }
        this.mHeaderLayout.addView(view, i);
        if (this.mHeaderLayout.getChildCount() == 1) {
            int headerViewPosition = getHeaderViewPosition();
            if (headerViewPosition != -1) {
                notifyItemInserted(headerViewPosition);
            }
        }
        return i;
    }

    public int setHeaderView(View view) {
        return setHeaderView(view, 0, 1);
    }

    public int setHeaderView(View view, int i) {
        return setHeaderView(view, i, 1);
    }

    public int setHeaderView(View view, int i, int i2) {
        LinearLayout linearLayout = this.mHeaderLayout;
        if (linearLayout == null || linearLayout.getChildCount() <= i) {
            return addHeaderView(view, i, i2);
        }
        this.mHeaderLayout.removeViewAt(i);
        this.mHeaderLayout.addView(view, i);
        return i;
    }

    public int addFooterView(View view) {
        return addFooterView(view, -1, 1);
    }

    public int addFooterView(View view, int i) {
        return addFooterView(view, i, 1);
    }

    public int addFooterView(View view, int i, int i2) {
        if (this.mFooterLayout == null) {
            LinearLayout linearLayout = new LinearLayout(view.getContext());
            this.mFooterLayout = linearLayout;
            if (i2 == 1) {
                linearLayout.setOrientation(1);
                this.mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            } else {
                linearLayout.setOrientation(0);
                this.mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(-2, -1));
            }
        }
        i2 = this.mFooterLayout.getChildCount();
        if (i < 0 || i > i2) {
            i = i2;
        }
        this.mFooterLayout.addView(view, i);
        if (this.mFooterLayout.getChildCount() == 1) {
            int footerViewPosition = getFooterViewPosition();
            if (footerViewPosition != -1) {
                notifyItemInserted(footerViewPosition);
            }
        }
        return i;
    }

    public int setFooterView(View view) {
        return setFooterView(view, 0, 1);
    }

    public int setFooterView(View view, int i) {
        return setFooterView(view, i, 1);
    }

    public int setFooterView(View view, int i, int i2) {
        LinearLayout linearLayout = this.mFooterLayout;
        if (linearLayout == null || linearLayout.getChildCount() <= i) {
            return addFooterView(view, i, i2);
        }
        this.mFooterLayout.removeViewAt(i);
        this.mFooterLayout.addView(view, i);
        return i;
    }

    public void removeHeaderView(View view) {
        if (getHeaderLayoutCount() != 0) {
            this.mHeaderLayout.removeView(view);
            if (this.mHeaderLayout.getChildCount() == 0) {
                int headerViewPosition = getHeaderViewPosition();
                if (headerViewPosition != -1) {
                    notifyItemRemoved(headerViewPosition);
                }
            }
        }
    }

    public void removeFooterView(View view) {
        if (getFooterLayoutCount() != 0) {
            this.mFooterLayout.removeView(view);
            if (this.mFooterLayout.getChildCount() == 0) {
                int footerViewPosition = getFooterViewPosition();
                if (footerViewPosition != -1) {
                    notifyItemRemoved(footerViewPosition);
                }
            }
        }
    }

    public void removeAllHeaderView() {
        if (getHeaderLayoutCount() != 0) {
            this.mHeaderLayout.removeAllViews();
            int headerViewPosition = getHeaderViewPosition();
            if (headerViewPosition != -1) {
                notifyItemRemoved(headerViewPosition);
            }
        }
    }

    public void removeAllFooterView() {
        if (getFooterLayoutCount() != 0) {
            this.mFooterLayout.removeAllViews();
            int footerViewPosition = getFooterViewPosition();
            if (footerViewPosition != -1) {
                notifyItemRemoved(footerViewPosition);
            }
        }
    }

    private int getHeaderViewPosition() {
        return (getEmptyViewCount() != 1 || this.mHeadAndEmptyEnable) ? 0 : -1;
    }

    private int getFooterViewPosition() {
        int i = 1;
        if (getEmptyViewCount() != 1) {
            return getHeaderLayoutCount() + this.mData.size();
        }
        if (this.mHeadAndEmptyEnable && getHeaderLayoutCount() != 0) {
            i = 2;
        }
        return this.mFootAndEmptyEnable ? i : -1;
    }

    public void setEmptyView(int i, ViewGroup viewGroup) {
        setEmptyView(LayoutInflater.from(viewGroup.getContext()).inflate(i, viewGroup, false));
    }

    public void setHeaderAndEmpty(boolean z) {
        setHeaderFooterEmpty(z, false);
    }

    public void setHeaderFooterEmpty(boolean z, boolean z2) {
        this.mHeadAndEmptyEnable = z;
        this.mFootAndEmptyEnable = z2;
    }

    public void isUseEmpty(boolean z) {
        this.mIsUseEmpty = z;
    }

    public View getEmptyView() {
        return this.mEmptyLayout;
    }

    @Deprecated
    public void setEmptyView(int i) {
        checkNotNull();
        setEmptyView(i, getRecyclerView());
    }

    public void setEmptyView(View view) {
        boolean z;
        int itemCount = getItemCount();
        if (this.mEmptyLayout == null) {
            this.mEmptyLayout = new FrameLayout(view.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(-1, -1);
            ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
            if (layoutParams2 != null) {
                layoutParams.width = layoutParams2.width;
                layoutParams.height = layoutParams2.height;
            }
            this.mEmptyLayout.setLayoutParams(layoutParams);
            z = true;
        } else {
            z = false;
        }
        this.mEmptyLayout.removeAllViews();
        this.mEmptyLayout.addView(view);
        this.mIsUseEmpty = true;
        if (z && getEmptyViewCount() == 1) {
            if (this.mHeadAndEmptyEnable) {
                int headerLayoutCount = getHeaderLayoutCount();
            }
            if (getItemCount() > itemCount) {
                notifyItemInserted(0);
            } else {
                notifyDataSetChanged();
            }
        }
    }

    @Deprecated
    public void setAutoLoadMoreSize(int i) {
        setPreLoadNumber(i);
    }

    public void setPreLoadNumber(int i) {
        if (i > 1) {
            this.mPreLoadNumber = i;
        }
    }

    private void autoLoadMore(int i) {
        if (getLoadMoreViewCount() != 0 && i >= getItemCount() - this.mPreLoadNumber && this.mLoadMoreView.getLoadMoreStatus() == 1) {
            this.mLoadMoreView.setLoadMoreStatus(2);
            if (!this.mLoading) {
                this.mLoading = true;
                if (getRecyclerView() != null) {
                    getRecyclerView().post(new Runnable() {
                        public void run() {
                            BaseQuickAdapter.this.mRequestLoadMoreListener.onLoadMoreRequested();
                        }
                    });
                } else {
                    this.mRequestLoadMoreListener.onLoadMoreRequested();
                }
            }
        }
    }

    private void addAnimation(ViewHolder viewHolder) {
        if (!this.mOpenAnimationEnable) {
            return;
        }
        if (!this.mFirstOnlyEnable || viewHolder.getLayoutPosition() > this.mLastPosition) {
            BaseAnimation baseAnimation = this.mCustomAnimation;
            if (baseAnimation == null) {
                baseAnimation = this.mSelectAnimation;
            }
            for (Animator startAnim : baseAnimation.getAnimators(viewHolder.itemView)) {
                startAnim(startAnim, viewHolder.getLayoutPosition());
            }
            this.mLastPosition = viewHolder.getLayoutPosition();
        }
    }

  
    public void startAnim(Animator animator, int i) {
        animator.setDuration((long) this.mDuration).start();
        animator.setInterpolator(this.mInterpolator);
    }

    public View getItemView(int i, ViewGroup viewGroup) {
        return this.mLayoutInflater.inflate(i, viewGroup, false);
    }

    public void openLoadAnimation(int i) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = null;
        if (i == 1) {
            this.mSelectAnimation = new AlphaInAnimation();
        } else if (i == 2) {
            this.mSelectAnimation = new ScaleInAnimation();
        } else if (i == 3) {
            this.mSelectAnimation = new SlideInBottomAnimation();
        } else if (i == 4) {
            this.mSelectAnimation = new SlideInLeftAnimation();
        } else if (i == 5) {
            this.mSelectAnimation = new SlideInRightAnimation();
        }
    }

    public void openLoadAnimation(BaseAnimation baseAnimation) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = baseAnimation;
    }

    public void openLoadAnimation() {
        this.mOpenAnimationEnable = true;
    }

    public void closeLoadAnimation() {
        this.mOpenAnimationEnable = false;
    }

    public void isFirstOnly(boolean z) {
        this.mFirstOnlyEnable = z;
    }

    public View getViewByPosition(int i, int i2) {
        checkNotNull();
        return getViewByPosition(getRecyclerView(), i, i2);
    }

    public View getViewByPosition(RecyclerView recyclerView, int i, int i2) {
        if (recyclerView != null) {
            BaseViewHolder baseViewHolder = (BaseViewHolder) recyclerView.findViewHolderForLayoutPosition(i);
            if (baseViewHolder != null) {
                return baseViewHolder.getView(i2);
            }
        }
        return null;
    }

    private int recursiveExpand(int i, List list) {
        int size = list.size();
        i = (i + list.size()) - 1;
        int size2 = list.size() - 1;
        while (size2 >= 0) {
            if (list.get(size2) instanceof IExpandable) {
                IExpandable iExpandable = (IExpandable) list.get(size2);
                if (iExpandable.isExpanded() && hasSubItems(iExpandable)) {
                    List subItems = iExpandable.getSubItems();
                    int i2 = i + 1;
                    this.mData.addAll(i2, subItems);
                    size += recursiveExpand(i2, subItems);
                }
            }
            size2--;
            i--;
        }
        return size;
    }

    public int expand(int i, boolean z, boolean z2) {
        i -= getHeaderLayoutCount();
        IExpandable expandableItem = getExpandableItem(i);
        int i2 = 0;
        if (expandableItem == null) {
            return 0;
        }
        if (hasSubItems(expandableItem)) {
            if (!expandableItem.isExpanded()) {
                List subItems = expandableItem.getSubItems();
                int i3 = i + 1;
                this.mData.addAll(i3, subItems);
                i2 = 0 + recursiveExpand(i3, subItems);
                expandableItem.setExpanded(true);
            }
            i += getHeaderLayoutCount();
            if (z2) {
                if (z) {
                    notifyItemChanged(i);
                    notifyItemRangeInserted(i + 1, i2);
                } else {
                    notifyDataSetChanged();
                }
            }
            return i2;
        }
        expandableItem.setExpanded(true);
        notifyItemChanged(i);
        return 0;
    }

    public int expand(int i, boolean z) {
        return expand(i, z, true);
    }

    public int expand(int i) {
        return expand(i, true, true);
    }

    public int expandAll(int i, boolean z, boolean z2) {
        i -= getHeaderLayoutCount();
        int i2 = i + 1;
        Object item = i2 < this.mData.size() ? getItem(i2) : null;
        IExpandable expandableItem = getExpandableItem(i);
        if (expandableItem == null) {
            return 0;
        }
        if (hasSubItems(expandableItem)) {
            int expand = expand(getHeaderLayoutCount() + i, false, false);
            while (i2 < this.mData.size()) {
                Object item2 = getItem(i2);
                if (item2 != null && item2.equals(item)) {
                    break;
                }
                if (isExpandable((T) item2)) {
                    expand += expand(getHeaderLayoutCount() + i2, false, false);
                }
                i2++;
            }
            if (z2) {
                if (z) {
                    notifyItemRangeInserted((i + getHeaderLayoutCount()) + 1, expand);
                } else {
                    notifyDataSetChanged();
                }
            }
            return expand;
        }
        expandableItem.setExpanded(true);
        notifyItemChanged(i);
        return 0;
    }

    public int expandAll(int i, boolean z) {
        return expandAll(i, true, z);
    }

    public void expandAll() {
        for (int size = (this.mData.size() - 1) + getHeaderLayoutCount(); size >= getHeaderLayoutCount(); size--) {
            expandAll(size, false, false);
        }
    }

    private int recursiveCollapse(int i) {
        Object item = getItem(i);
        int i2 = 0;
        if (isExpandable((T) item)) {
            IExpandable iExpandable = (IExpandable) item;
            if (iExpandable.isExpanded()) {
                List subItems = iExpandable.getSubItems();
                if (subItems == null) {
                    return 0;
                }
                for (int size = subItems.size() - 1; size >= 0; size--) {
                    Object obj = subItems.get(size);
                    int itemPosition = getItemPosition((T) obj);
                    if (itemPosition >= 0) {
                        if (itemPosition < i) {
                            itemPosition = (i + size) + 1;
                            if (itemPosition >= this.mData.size()) {
                            }
                        }
                        if (obj instanceof IExpandable) {
                            i2 += recursiveCollapse(itemPosition);
                        }
                        this.mData.remove(itemPosition);
                        i2++;
                    }
                }
            }
        }
        return i2;
    }

    public int collapse(int i, boolean z, boolean z2) {
        i -= getHeaderLayoutCount();
        IExpandable expandableItem = getExpandableItem(i);
        if (expandableItem == null) {
            return 0;
        }
        int recursiveCollapse = recursiveCollapse(i);
        expandableItem.setExpanded(false);
        i += getHeaderLayoutCount();
        if (z2) {
            if (z) {
                notifyItemChanged(i);
                notifyItemRangeRemoved(i + 1, recursiveCollapse);
            } else {
                notifyDataSetChanged();
            }
        }
        return recursiveCollapse;
    }

    public int collapse(int i) {
        return collapse(i, true, true);
    }

    public int collapse(int i, boolean z) {
        return collapse(i, z, true);
    }

    private int getItemPosition(T t) {
        if (t != null) {
            List list = this.mData;
            if (!(list == null || list.isEmpty())) {
                return this.mData.indexOf(t);
            }
        }
        return -1;
    }

    public boolean hasSubItems(IExpandable iExpandable) {
        if (iExpandable != null) {
            List subItems = iExpandable.getSubItems();
            if (subItems != null && subItems.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isExpandable(T t) {
        return t != null && (t instanceof IExpandable);
    }

    private IExpandable getExpandableItem(int i) {
        Object item = getItem(i);
        return isExpandable((T) item) ? (IExpandable) item : null;
    }

    public int getParentPosition(T t) {
        int itemPosition = getItemPosition(t);
        if (itemPosition == -1) {
            return -1;
        }
        int level = t instanceof IExpandable ? ((IExpandable) t).getLevel() : Integer.MAX_VALUE;
        if (level == 0) {
            return itemPosition;
        }
        if (level == -1) {
            return -1;
        }
        while (itemPosition >= 0) {
            Object obj = this.mData.get(itemPosition);
            if (obj instanceof IExpandable) {
                IExpandable iExpandable = (IExpandable) obj;
                if (iExpandable.getLevel() >= 0 && iExpandable.getLevel() < level) {
                    return itemPosition;
                }
            }
            itemPosition--;
        }
        return -1;
    }

    public final OnItemLongClickListener getOnItemLongClickListener() {
        return this.mOnItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public final OnItemClickListener getOnItemClickListener() {
        return this.mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public final OnItemChildClickListener getOnItemChildClickListener() {
        return this.mOnItemChildClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.mOnItemChildClickListener = onItemChildClickListener;
    }

    public final OnItemChildLongClickListener getOnItemChildLongClickListener() {
        return this.mOnItemChildLongClickListener;
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        this.mOnItemChildLongClickListener = onItemChildLongClickListener;
    }
}
