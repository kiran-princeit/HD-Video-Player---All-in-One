package hd.video.player.videoplayer.mplayer.masterplayer.customview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Consumer;
import hd.video.player.videoplayer.mplayer.masterplayer.R;

public final class SecondsView extends ConstraintLayout {
    private long cycleDuration = 750;
    private final ValueAnimator fifthAnimator;
    private final ValueAnimator firstAnimator;
    private final ValueAnimator fourthAnimator;
    private int icon = R.drawable.ic_play_triangle;
    private boolean isForward = true;
    private final ValueAnimator secondAnimator;
    private int seconds = 0;
    private final ValueAnimator thirdAnimator;

    private final class CustomValueAnimator extends ValueAnimator {
        public CustomValueAnimator(final Runnable runnable, final Consumer<Float> consumer, final Runnable runnable2) {
            setDuration(SecondsView.this.getCycleDuration() / 5);
            setFloatValues(new float[]{0.0f, 1.0f});
            addUpdateListener(new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    consumer.accept((Float) valueAnimator.getAnimatedValue());
                }
            });
            addListener(new AnimatorListener() {
                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                    runnable.run();
                }

                public void onAnimationEnd(Animator animator) {
                    runnable2.run();
                }
            });
        }
    }

    public SecondsView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        LayoutInflater.from(context).inflate(R.layout.yt_seconds_view, this, true);
        this.firstAnimator = new CustomValueAnimator(new Runnable() {
            public void run() {
                SecondsView.this.findViewById(R.id.icon_1).setAlpha(0.0f);
                SecondsView.this.findViewById(R.id.icon_2).setAlpha(0.0f);
                SecondsView.this.findViewById(R.id.icon_3).setAlpha(0.0f);
            }
        }, new Consumer<Float>() {
            public void accept(Float f) {
                SecondsView.this.findViewById(R.id.icon_1).setAlpha(f.floatValue());
            }
        }, new Runnable() {
            public void run() {
                SecondsView.this.secondAnimator.start();
            }
        });
        this.secondAnimator = new CustomValueAnimator(new Runnable() {
            public void run() {
                SecondsView.this.findViewById(R.id.icon_1).setAlpha(1.0f);
                SecondsView.this.findViewById(R.id.icon_2).setAlpha(0.0f);
                SecondsView.this.findViewById(R.id.icon_3).setAlpha(0.0f);
            }
        }, new Consumer<Float>() {
            public void accept(Float f) {
                SecondsView.this.findViewById(R.id.icon_2).setAlpha(f.floatValue());
            }
        }, new Runnable() {
            public void run() {
                SecondsView.this.thirdAnimator.start();
            }
        });
        this.thirdAnimator = new CustomValueAnimator(new Runnable() {
            public void run() {
                SecondsView.this.findViewById(R.id.icon_1).setAlpha(1.0f);
                SecondsView.this.findViewById(R.id.icon_2).setAlpha(1.0f);
                SecondsView.this.findViewById(R.id.icon_3).setAlpha(0.0f);
            }
        }, new Consumer<Float>() {
            public void accept(Float f) {
                SecondsView.this.findViewById(R.id.icon_1).setAlpha(1.0f - SecondsView.this.findViewById(R.id.icon_3).getAlpha());
                SecondsView.this.findViewById(R.id.icon_3).setAlpha(f.floatValue());
            }
        }, new Runnable() {
            public void run() {
                SecondsView.this.fourthAnimator.start();
            }
        });
        this.fourthAnimator = new CustomValueAnimator(new Runnable() {
            public void run() {
                SecondsView.this.findViewById(R.id.icon_1).setAlpha(0.0f);
                SecondsView.this.findViewById(R.id.icon_2).setAlpha(1.0f);
                SecondsView.this.findViewById(R.id.icon_3).setAlpha(1.0f);
            }
        }, new Consumer<Float>() {
            public void accept(Float f) {
                SecondsView.this.findViewById(R.id.icon_2).setAlpha(1.0f - f.floatValue());
            }
        }, new Runnable() {
            public void run() {
                SecondsView.this.fifthAnimator.start();
            }
        });
        this.fifthAnimator = new CustomValueAnimator(new Runnable() {
            public void run() {
                SecondsView.this.findViewById(R.id.icon_1).setAlpha(0.0f);
                SecondsView.this.findViewById(R.id.icon_2).setAlpha(0.0f);
                SecondsView.this.findViewById(R.id.icon_3).setAlpha(1.0f);
            }
        }, new Consumer<Float>() {
            public void accept(Float f) {
                SecondsView.this.findViewById(R.id.icon_3).setAlpha(1.0f - f.floatValue());
            }
        }, new Runnable() {
            public void run() {
                SecondsView.this.firstAnimator.start();
            }
        });
    }

    public final long getCycleDuration() {
        return this.cycleDuration;
    }

    public final void setCycleDuration(long j) {
        long j2 = j / 5;
        this.firstAnimator.setDuration(j2);
        this.secondAnimator.setDuration(j2);
        this.thirdAnimator.setDuration(j2);
        this.fourthAnimator.setDuration(j2);
        this.fifthAnimator.setDuration(j2);
        this.cycleDuration = j;
    }

    public final int getSeconds() {
        return this.seconds;
    }

    public final void setSeconds(int i) {
        ((TextView) findViewById(R.id.tv_seconds)).setText(getContext().getResources().getQuantityString(R.plurals.quick_seek_x_second, i, new Object[]{Integer.valueOf(i)}));
        this.seconds = i;
    }

    public final boolean isForward() {
        return this.isForward;
    }

    public final void setForward(boolean z) {
        ((LinearLayout) findViewById(R.id.triangle_container)).setRotation(z ? 0.0f : 180.0f);
        this.isForward = z;
    }

    public final TextView getTextView() {
        return (TextView) findViewById(R.id.tv_seconds);
    }

    public final int getIcon() {
        return this.icon;
    }

    public final void setIcon(int i) {
        if (i > 0) {
            ((ImageView) findViewById(R.id.icon_1)).setImageResource(i);
            ((ImageView) findViewById(R.id.icon_2)).setImageResource(i);
            ((ImageView) findViewById(R.id.icon_3)).setImageResource(i);
        }
        this.icon = i;
    }

    public final void start() {
        stop();
        this.firstAnimator.start();
    }

    public final void stop() {
        this.firstAnimator.cancel();
        this.secondAnimator.cancel();
        this.thirdAnimator.cancel();
        this.fourthAnimator.cancel();
        this.fifthAnimator.cancel();
        reset();
    }

    private final void reset() {
        findViewById(R.id.icon_1).setAlpha(0.0f);
        findViewById(R.id.icon_2).setAlpha(0.0f);
        findViewById(R.id.icon_3).setAlpha(0.0f);
    }
}
