package com.hdvideoplayer.smartplayer.player.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.SeekBar;

import androidx.core.view.ViewCompat;

import com.hdvideoplayer.smartplayer.player.R;

public class VerticalSeekBar extends SeekBar {
    private int mRotationAngle = 90;
    private Drawable mThumb_;

    private static boolean isValidRotationAngle(int i) {
        return i == 90 || i == 270;
    }

    public VerticalSeekBar(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context, attributeSet, 0, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize(context, attributeSet, i, 0);
    }

    private void initialize(Context context, AttributeSet attributeSet, int i, int i2) {
        ViewCompat.setLayoutDirection(this, 0);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.seekBarRotation}, i, i2);
            int integer = obtainStyledAttributes.getInteger(0, 0);
            if (isValidRotationAngle(integer)) {
                this.mRotationAngle = integer;
            }
            obtainStyledAttributes.recycle();
        }
    }

    public void setThumb(Drawable drawable) {
        this.mThumb_ = drawable;
        super.setThumb(drawable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int progress = getMax() - (int) (getMax() * event.getY() / getHeight());
                setProgress(progress);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (isEnabled()) {
            switch (i) {
                case 21:
                case 22:
                    return false;
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public synchronized void setProgress(int i) {
        super.setProgress(i);
        if (!useViewRotation()) {
            refreshThumb();
        }
    }
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec); // Swap width and height
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw); // Swap width and height
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        // Rotate the canvas 90 degrees counter-clockwise
        canvas.rotate(-90);
        // Translate canvas to draw the SeekBar correctly
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
        canvas.restore();
    }
    public int getRotationAngle() {
        return this.mRotationAngle;
    }

    private void refreshThumb() {
        onSizeChanged(super.getWidth(), super.getHeight(), 0, 0);
    }

    public boolean useViewRotation() {
        return isInEditMode();
    }

}
