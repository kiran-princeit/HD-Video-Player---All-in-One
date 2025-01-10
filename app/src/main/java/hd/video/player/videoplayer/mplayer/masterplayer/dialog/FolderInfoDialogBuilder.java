package hd.video.player.videoplayer.mplayer.masterplayer.dialog;

import android.content.Context;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.VideoFolder;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;

public class FolderInfoDialogBuilder {
    private final BottomSheetDialog mDialog;

    public FolderInfoDialogBuilder(Context context, VideoFolder videoFolder) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomDialog);
        this.mDialog = bottomSheetDialog;
        bottomSheetDialog.requestWindowFeature(1);
        bottomSheetDialog.setContentView(R.layout.dialog_folder_info);

        ((TextView) bottomSheetDialog.findViewById(R.id.tv_info_name)).setText(videoFolder.getFolderName());
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_info_size)).setText(Utility.convertSize(videoFolder.getSize()));
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_info_total)).setText(context.getResources().getQuantityString(R.plurals.value_of_video, videoFolder.getVideoList().size(), new Object[]{Integer.valueOf(videoFolder.getVideoList().size())}));
        ((TextView) bottomSheetDialog.findViewById(R.id.tv_info_path)).setText(videoFolder.getPath());
    }

    public BottomSheetDialog build() {
        return this.mDialog;
    }
}
