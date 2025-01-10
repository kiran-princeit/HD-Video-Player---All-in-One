package hd.video.player.videoplayer.mplayer.masterplayer.dialog;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;

public class BottomMenuDialogBuilder {
    BottomSheetDialog mDialog;
    RecyclerView mRvContent;

    public BottomMenuDialogBuilder(Context context, BottomMenuAdapter bottomMenuAdapter) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomDialog);
        this.mDialog = bottomSheetDialog;
        bottomSheetDialog.setContentView(R.layout.custom_bottom_dialog);
        RecyclerView recyclerView = (RecyclerView) this.mDialog.findViewById(R.id.rv_content_dialog);
        this.mRvContent = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.mRvContent.setAdapter(bottomMenuAdapter);
    }

    public BottomSheetDialog build() {
        return this.mDialog;
    }
}
