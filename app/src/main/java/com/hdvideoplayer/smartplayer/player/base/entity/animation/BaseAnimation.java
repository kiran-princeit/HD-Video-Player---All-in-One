package com.hdvideoplayer.smartplayer.player.base.entity.animation;

import android.animation.Animator;
import android.view.View;

public interface BaseAnimation {
    Animator[] getAnimators(View view);
}
