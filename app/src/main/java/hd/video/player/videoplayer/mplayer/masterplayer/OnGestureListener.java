package hd.video.player.videoplayer.mplayer.masterplayer;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnGestureListener implements View.OnTouchListener {
    public static final int ONE_FINGER = 1;
    private final GestureDetector gestureDetector;

    public void onDoubleClick() {
    }

    public void onHorizontalScroll(MotionEvent motionEvent, float f) {
    }

    public void onSingleClick() {
    }

    public void onSwipeBottom() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public void onSwipeTop() {
    }

    public void onTap() {
    }

    public void onVerticalScroll(MotionEvent motionEvent, float f) {
    }

    public OnGestureListener(Context context) {
        this.gestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.gestureDetector.onTouchEvent(motionEvent);
    }

    private final class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        private MyGestureListener() {
        }

        public boolean onDoubleTap(MotionEvent motionEvent) {
            OnGestureListener.this.onDoubleClick();
            return super.onDoubleTap(motionEvent);
        }

        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            OnGestureListener.this.onSingleClick();
            return super.onSingleTapConfirmed(motionEvent);
        }

        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            float y = motionEvent2.getY() - motionEvent.getY();
            float x = motionEvent2.getX() - motionEvent.getX();
            if (Math.abs(x) > Math.abs(y)) {
                if (Math.abs(x) <= 100.0f) {
                    return false;
                }
                OnGestureListener.this.onHorizontalScroll(motionEvent2, x);
                return false;
            } else if (Math.abs(y) <= 100.0f) {
                return false;
            } else {
                OnGestureListener.this.onVerticalScroll(motionEvent2, y);
                return false;
            }
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            OnGestureListener.this.onTap();
            return false;
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            try {
                float y = motionEvent2.getY() - motionEvent.getY();
                float x = motionEvent2.getX() - motionEvent.getX();
                if (Math.abs(x) > Math.abs(y)) {
                    if (Math.abs(x) > 100.0f && Math.abs(f) > 100.0f) {
                        if (x > 0.0f) {
                            OnGestureListener.this.onSwipeRight();
                        } else {
                            OnGestureListener.this.onSwipeLeft();
                        }
                    }
                } else if (Math.abs(y) > 100.0f && Math.abs(f2) > 100.0f) {
                    if (y > 0.0f) {
                        OnGestureListener.this.onSwipeBottom();
                    } else {
                        OnGestureListener.this.onSwipeTop();
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
