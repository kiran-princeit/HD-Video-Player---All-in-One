package hd.video.player.videoplayer.mplayer.masterplayer.customview;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.GridLayoutManager;

public class NpaGridLayoutManager extends GridLayoutManager {
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public NpaGridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public NpaGridLayoutManager(Context context, int i) {
        super(context, i);
    }

    public NpaGridLayoutManager(Context context, int i, int i2, boolean z) {
        super(context, i, i2, z);
    }
}
