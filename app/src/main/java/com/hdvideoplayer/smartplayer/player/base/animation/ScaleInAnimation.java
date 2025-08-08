package com.hdvideoplayer.smartplayer.player.base.animation;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

public class ScaleInAnimation implements BaseAnimation {
    private static final float DEFAULT_SCALE_FROM = 0.5f;
    private final float mFrom;

    public ScaleInAnimation() {
        this(0.5f);
    }

    public ScaleInAnimation(float f) {
        this.mFrom = f;
    }

    public Animator[] getAnimators(View view) {
        ObjectAnimator[] objectAnimator = new ObjectAnimator[2];
        objectAnimator[0] = ObjectAnimator.ofFloat(view, "scaleX", new float[]{this.mFrom, 1.0f});
        objectAnimator[1] = ObjectAnimator.ofFloat(view, "scaleY", new float[]{this.mFrom, 1.0f});
        return objectAnimator;
    }
}
