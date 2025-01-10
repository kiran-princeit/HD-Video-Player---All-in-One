package hd.video.player.videoplayer.mplayer.masterplayer.base;

import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class BaseViewHolder extends ViewHolder {
    private BaseQuickAdapter adapter;
    private final LinkedHashSet<Integer> childClickViewIds = new LinkedHashSet();
    @Deprecated
    public View convertView;
    private final LinkedHashSet<Integer> itemChildLongClickViewIds = new LinkedHashSet();
    private final HashSet<Integer> nestViews = new HashSet();
    private final SparseArray<View> views = new SparseArray();

    public BaseViewHolder(View view) {
        super(view);
        this.convertView = view;
    }

    @Deprecated
    public View getConvertView() {
        return this.convertView;
    }

    public BaseViewHolder setText(int i, CharSequence charSequence) {
        ((TextView) getView(i)).setText(charSequence);
        return this;
    }

    public BaseViewHolder setText(int i, int i2) {
        ((TextView) getView(i)).setText(i2);
        return this;
    }

    public BaseViewHolder setImageResource(int i, int i2) {
        ((ImageView) getView(i)).setImageResource(i2);
        return this;
    }

    public BaseViewHolder setGone(int i, boolean z) {
        getView(i).setVisibility(z ? 0 : 8);
        return this;
    }

    public BaseViewHolder setVisible(int i, boolean z) {
        getView(i).setVisibility(z ? 0 : 4);
        return this;
    }

    public BaseViewHolder setProgress(int i, int i2) {
        ((ProgressBar) getView(i)).setProgress(i2);
        return this;
    }

    public BaseViewHolder setProgress(int i, int i2, int i3) {
        ProgressBar progressBar = (ProgressBar) getView(i);
        progressBar.setMax(i3);
        progressBar.setProgress(i2);
        return this;
    }

    @Deprecated
    public BaseViewHolder setOnClickListener(int i, OnClickListener onClickListener) {
        getView(i).setOnClickListener(onClickListener);
        return this;
    }

    public BaseViewHolder addOnClickListener(int... iArr) {
        for (int i : iArr) {
            this.childClickViewIds.add(Integer.valueOf(i));
            View view = getView(i);
            if (view != null) {
                if (!view.isClickable()) {
                    view.setClickable(true);
                }
                view.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        if (BaseViewHolder.this.adapter.getOnItemChildClickListener() != null) {
                            int adapterPosition = BaseViewHolder.this.getAdapterPosition();
                            if (adapterPosition != -1) {
                                BaseViewHolder.this.adapter.getOnItemChildClickListener().onItemChildClick(BaseViewHolder.this.adapter, view, adapterPosition - BaseViewHolder.this.adapter.getHeaderLayoutCount());
                            }
                        }
                    }
                });
            }
        }
        return this;
    }


    public BaseViewHolder setAdapter(int i, Adapter adapter) {
        ((AdapterView) getView(i)).setAdapter(adapter);
        return this;
    }

    public BaseViewHolder setAdapter(BaseQuickAdapter baseQuickAdapter) {
        this.adapter = baseQuickAdapter;
        return this;
    }

    public <T extends View> T getView(int i) {
        View view = (View) this.views.get(i);
        if (view != null) {
            return (T) view;
        }
        view = this.itemView.findViewById(i);
        this.views.put(i, view);
        return (T) view;
    }
}
