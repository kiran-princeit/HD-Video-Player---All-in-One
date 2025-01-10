package hd.video.player.videoplayer.mplayer.masterplayer.base.entity;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class BaseViewHolder extends ViewHolder {
    private BaseQuickAdapter adapter;
    private Object associatedObject;
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

    public Set<Integer> getNestViews() {
        return this.nestViews;
    }

    public HashSet<Integer> getItemChildLongClickViewIds() {
        return this.itemChildLongClickViewIds;
    }

    public HashSet<Integer> getChildClickViewIds() {
        return this.childClickViewIds;
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

    public BaseViewHolder setBackgroundColor(int i, int i2) {
        getView(i).setBackgroundColor(i2);
        return this;
    }

    public BaseViewHolder setBackgroundRes(int i, int i2) {
        getView(i).setBackgroundResource(i2);
        return this;
    }

    public BaseViewHolder setTextColor(int i, int i2) {
        ((TextView) getView(i)).setTextColor(i2);
        return this;
    }

    public BaseViewHolder setImageDrawable(int i, Drawable drawable) {
        ((ImageView) getView(i)).setImageDrawable(drawable);
        return this;
    }

    public BaseViewHolder setImageBitmap(int i, Bitmap bitmap) {
        ((ImageView) getView(i)).setImageBitmap(bitmap);
        return this;
    }

    public BaseViewHolder setAlpha(int i, float f) {
        getView(i).setAlpha(f);
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

    public BaseViewHolder linkify(int i) {
        Linkify.addLinks((TextView) getView(i), 15);
        return this;
    }

    public BaseViewHolder setTypeface(int i, Typeface typeface) {
        TextView textView = (TextView) getView(i);
        textView.setTypeface(typeface);
        textView.setPaintFlags(textView.getPaintFlags() | 128);
        return this;
    }

    public BaseViewHolder setTypeface(Typeface typeface, int... iArr) {
        for (int view : iArr) {
            TextView textView = (TextView) getView(view);
            textView.setTypeface(typeface);
            textView.setPaintFlags(textView.getPaintFlags() | 128);
        }
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

    public BaseViewHolder setMax(int i, int i2) {
        ((ProgressBar) getView(i)).setMax(i2);
        return this;
    }

    public BaseViewHolder setRating(int i, float f) {
        ((RatingBar) getView(i)).setRating(f);
        return this;
    }

    public BaseViewHolder setRating(int i, float f, int i2) {
        RatingBar ratingBar = (RatingBar) getView(i);
        ratingBar.setMax(i2);
        ratingBar.setRating(f);
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

    public BaseViewHolder setNestView(int... iArr) {
        for (int valueOf : iArr) {
            this.nestViews.add(Integer.valueOf(valueOf));
        }
        addOnClickListener(iArr);
        addOnLongClickListener(iArr);
        return this;
    }

    public BaseViewHolder addOnLongClickListener(int... iArr) {
        for (int i : iArr) {
            this.itemChildLongClickViewIds.add(Integer.valueOf(i));
            View view = getView(i);
            if (view != null) {
                if (!view.isLongClickable()) {
                    view.setLongClickable(true);
                }
                view.setOnLongClickListener(new OnLongClickListener() {
                    public boolean onLongClick(View view) {
                        if (BaseViewHolder.this.adapter.getOnItemChildLongClickListener() != null) {
                            int adapterPosition = BaseViewHolder.this.getAdapterPosition();
                            if (adapterPosition != -1) {
                                return BaseViewHolder.this.adapter.getOnItemChildLongClickListener().onItemChildLongClick(BaseViewHolder.this.adapter, view, adapterPosition - BaseViewHolder.this.adapter.getHeaderLayoutCount());
                            }
                        }
                        return false;
                    }
                });
            }
        }
        return this;
    }

    @Deprecated
    public BaseViewHolder setOnTouchListener(int i, OnTouchListener onTouchListener) {
        getView(i).setOnTouchListener(onTouchListener);
        return this;
    }

    @Deprecated
    public BaseViewHolder setOnLongClickListener(int i, OnLongClickListener onLongClickListener) {
        getView(i).setOnLongClickListener(onLongClickListener);
        return this;
    }

    @Deprecated
    public BaseViewHolder setOnItemClickListener(int i, OnItemClickListener onItemClickListener) {
        ((AdapterView) getView(i)).setOnItemClickListener(onItemClickListener);
        return this;
    }

    public BaseViewHolder setOnItemLongClickListener(int i, OnItemLongClickListener onItemLongClickListener) {
        ((AdapterView) getView(i)).setOnItemLongClickListener(onItemLongClickListener);
        return this;
    }

    public BaseViewHolder setOnItemSelectedClickListener(int i, OnItemSelectedListener onItemSelectedListener) {
        ((AdapterView) getView(i)).setOnItemSelectedListener(onItemSelectedListener);
        return this;
    }

    public BaseViewHolder setOnCheckedChangeListener(int i, OnCheckedChangeListener onCheckedChangeListener) {
        ((CompoundButton) getView(i)).setOnCheckedChangeListener(onCheckedChangeListener);
        return this;
    }

    public BaseViewHolder setTag(int i, Object obj) {
        getView(i).setTag(obj);
        return this;
    }

    public BaseViewHolder setTag(int i, int i2, Object obj) {
        getView(i).setTag(i2, obj);
        return this;
    }

    public BaseViewHolder setChecked(int i, boolean z) {
        View view = getView(i);
        if (view instanceof Checkable) {
            ((Checkable) view).setChecked(z);
        }
        return this;
    }

    public BaseViewHolder setEnabled(int i, boolean z) {
        getView(i).setEnabled(z);
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

    public Object getAssociatedObject() {
        return this.associatedObject;
    }

    public void setAssociatedObject(Object obj) {
        this.associatedObject = obj;
    }
}
