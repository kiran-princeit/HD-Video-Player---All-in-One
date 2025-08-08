package com.hdvideoplayer.smartplayer.player.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.view.ViewCompat;

public class VerticalSeekBarWrapper extends FrameLayout {
    public VerticalSeekBarWrapper(Context context) {
        this(context, null, 0);
    }

    public VerticalSeekBarWrapper(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VerticalSeekBarWrapper(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

  
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        if (useViewRotation()) {
            onSizeChangedUseViewRotation(i, i2, i3, i4);
        } else {
            onSizeChangedTraditionalRotation(i, i2, i3, i4);
        }
    }

    private void onSizeChangedTraditionalRotation(int i, int i2, int i3, int i4) {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        if (childSeekBar != null) {
            int paddingLeft = getPaddingLeft() + getPaddingRight();
            int paddingTop = getPaddingTop() + getPaddingBottom();
            LayoutParams layoutParams = (LayoutParams) childSeekBar.getLayoutParams();
            layoutParams.width = -2;
            paddingTop = i2 - paddingTop;
            layoutParams.height = Math.max(0, paddingTop);
            childSeekBar.setLayoutParams(layoutParams);
            childSeekBar.measure(0, 0);
            int measuredWidth = childSeekBar.getMeasuredWidth();
            paddingLeft = i - paddingLeft;
            childSeekBar.measure(MeasureSpec.makeMeasureSpec(Math.max(0, paddingLeft), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(Math.max(0, paddingTop), 1073741824));
            layoutParams.gravity = 51;
            layoutParams.leftMargin = (Math.max(0, paddingLeft) - measuredWidth) / 2;
            childSeekBar.setLayoutParams(layoutParams);
        }
        super.onSizeChanged(i, i2, i3, i4);
    }

    private void onSizeChangedUseViewRotation(int i, int i2, int i3, int i4) {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        if (childSeekBar != null) {
            childSeekBar.measure(MeasureSpec.makeMeasureSpec(Math.max(0, i2 - (getPaddingTop() + getPaddingBottom())), 1073741824), MeasureSpec.makeMeasureSpec(Math.max(0, i - (getPaddingLeft() + getPaddingRight())), Integer.MIN_VALUE));
        }
        applyViewRotation(i, i2);
        super.onSizeChanged(i, i2, i3, i4);
    }

    public void onMeasure(int i, int i2) {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        if (childSeekBar == null || mode == 1073741824) {
            super.onMeasure(i, i2);
            return;
        }
        int measuredWidth;
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        mode = MeasureSpec.makeMeasureSpec(Math.max(0, size - paddingLeft), mode);
        mode2 = MeasureSpec.makeMeasureSpec(Math.max(0, size2 - paddingTop), mode2);
        if (useViewRotation()) {
            childSeekBar.measure(mode2, mode);
            mode = childSeekBar.getMeasuredHeight();
            measuredWidth = childSeekBar.getMeasuredWidth();
        } else {
            childSeekBar.measure(mode, mode2);
            mode = childSeekBar.getMeasuredWidth();
            measuredWidth = childSeekBar.getMeasuredHeight();
        }
        setMeasuredDimension(resolveSizeAndState(mode + paddingLeft, i, 0), resolveSizeAndState(measuredWidth + paddingTop, i2, 0));
    }

    public void applyViewRotation() {
        applyViewRotation(getWidth(), getHeight());
    }

//    private void applyViewRotation(int i, int i2) {
//        VerticalSeekBar childSeekBar = getChildSeekBar();
//        if (childSeekBar != null) {
//            float f;
//            int i3 = ViewCompat.getLayoutDirection(this) == 0 ? 1 : 0;
//            int rotationAngle = childSeekBar.getRotationAngle();
//            int measuredWidth = childSeekBar.getMeasuredWidth();
//            int measuredHeight = childSeekBar.getMeasuredHeight();
//            float max = ((float) (Math.max(0, i - (getPaddingLeft() + getPaddingRight())) - measuredHeight)) * 0.5f;
//            ViewGroup.LayoutParams layoutParams = childSeekBar.getLayoutParams();
//            i2 -= getPaddingTop() + getPaddingBottom();
//            layoutParams.width = Math.max(0, i2);
//            layoutParams.height = -2;
//            childSeekBar.setLayoutParams(layoutParams);
//            if (i3 != 0) {
//                f = 0.0f;
//            } else {
//                f = (float) Math.max(0, i2);
//            }
//            childSeekBar.setPivotX(f);
//            childSeekBar.setPivotY(0.0f);
//            if (rotationAngle == 90) {
//                childSeekBar.setRotation(90.0f);
//                if (i3 != 0) {
//                    childSeekBar.setTranslationX(((float) measuredHeight) + max);
//                    childSeekBar.setTranslationY(0.0f);
//                    return;
//                }
//                childSeekBar.setTranslationX(-max);
//                childSeekBar.setTranslationY((float) measuredWidth);
//            } else if (rotationAngle == 270) {
//                childSeekBar.setRotation(270.0f);
//                if (i3 != 0) {
//                    childSeekBar.setTranslationX(max);
//                    childSeekBar.setTranslationY((float) measuredWidth);
//                    return;
//                }
//                childSeekBar.setTranslationX(-(((float) measuredHeight) + max));
//                childSeekBar.setTranslationY(0.0f);
//            }
//        }
//    }


    private void applyViewRotation(int width, int height) {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        if (childSeekBar != null) {
            boolean isLayoutDirectionLTR = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR;
            int rotationAngle = childSeekBar.getRotationAngle();
            int measuredWidth = childSeekBar.getMeasuredWidth();
            int measuredHeight = childSeekBar.getMeasuredHeight();

            float centerOffset = (Math.max(0, width - (getPaddingLeft() + getPaddingRight())) - measuredHeight) * 0.5f;

            ViewGroup.LayoutParams layoutParams = childSeekBar.getLayoutParams();
            int availableHeight = height - getPaddingTop() - getPaddingBottom();
            layoutParams.width = Math.max(0, availableHeight);
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            childSeekBar.setLayoutParams(layoutParams);

            float pivotX = isLayoutDirectionLTR ? 0f : Math.max(0, availableHeight);
            childSeekBar.setPivotX(pivotX);
            childSeekBar.setPivotY(0f);

            if (rotationAngle == 90) {
                childSeekBar.setRotation(90f);
                if (isLayoutDirectionLTR) {
                    childSeekBar.setTranslationX(-centerOffset);
                    childSeekBar.setTranslationY(measuredWidth);
                } else {
                    childSeekBar.setTranslationX(centerOffset + measuredHeight);
                    childSeekBar.setTranslationY(0f);
                }
            } else if (rotationAngle == 270) {
                childSeekBar.setRotation(270f);
                if (isLayoutDirectionLTR) {
                    childSeekBar.setTranslationX(-centerOffset - measuredHeight);
                    childSeekBar.setTranslationY(0f);
                } else {
                    childSeekBar.setTranslationX(centerOffset);
                    childSeekBar.setTranslationY(measuredWidth);
                }
            }
        }
    }

    private VerticalSeekBar getChildSeekBar() {
        View childAt = getChildCount() > 0 ? getChildAt(0) : null;
        if (childAt instanceof VerticalSeekBar) {
            return (VerticalSeekBar) childAt;
        }
        return null;
    }

    private boolean useViewRotation() {
        VerticalSeekBar childSeekBar = getChildSeekBar();
        return childSeekBar != null ? childSeekBar.useViewRotation() : false;
    }
}
