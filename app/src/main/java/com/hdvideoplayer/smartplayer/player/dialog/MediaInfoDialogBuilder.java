package com.hdvideoplayer.smartplayer.player.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.video.VideoInfo;
import com.hdvideoplayer.smartplayer.player.util.Utility;

public class MediaInfoDialogBuilder<T> {
    private final BottomSheetDialog mDialog;

    public MediaInfoDialogBuilder(Context context, T t) {
        T t2 = t;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomDialog);
        this.mDialog = bottomSheetDialog;
        bottomSheetDialog.requestWindowFeature(1);
        bottomSheetDialog.setContentView(R.layout.dialog_media_info);
        TextView textView = bottomSheetDialog.findViewById(R.id.tv_info_name);
        TextView textView2 = bottomSheetDialog.findViewById(R.id.tv_info_artist);
        TextView textView3 = bottomSheetDialog.findViewById(R.id.tv_info_album);
        TextView textView4 = bottomSheetDialog.findViewById(R.id.tv_info_size);
        TextView textView5 = bottomSheetDialog.findViewById(R.id.tv_info_date);
        TextView textView6 = bottomSheetDialog.findViewById(R.id.tv_info_length);
        TextView textView7 = bottomSheetDialog.findViewById(R.id.tv_info_resolution);
        TextView textView8 = bottomSheetDialog.findViewById(R.id.tv_info_path);
        TextView textView9 = bottomSheetDialog.findViewById(R.id.tv_title_artist);
        TextView textView10 = bottomSheetDialog.findViewById(R.id.tv_title_album);
        TextView textView11 = bottomSheetDialog.findViewById(R.id.tv_title_resolution);
        String str = "yyyy-MM-dd HH:mm:ss";
        if (t2 instanceof VideoInfo) {
            VideoInfo videoInfo = (VideoInfo) t2;
            textView.setText(videoInfo.getDisplayName());
            textView4.setText(Utility.convertSize(videoInfo.getSize()));
            textView5.setText(Utility.convertLongToTime(videoInfo.getDate(), str));
            textView6.setText(Utility.convertLongToDuration(videoInfo.getDuration()));
            textView7.setText(videoInfo.getResolution());
            textView8.setText(videoInfo.getPath());
            textView2.setVisibility(View.GONE);
            textView9.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            textView10.setVisibility(View.GONE);
        } else if (t2 instanceof MusicInfo) {
            MusicInfo musicInfo = (MusicInfo) t2;
            textView.setText(musicInfo.getDisplayName());
            textView2.setText(musicInfo.getArtist());
            textView3.setText(musicInfo.getAlbum());
            textView4.setText(Utility.convertSize(musicInfo.getSize()));
            textView5.setText(Utility.convertLongToTime(musicInfo.getDate() * 1000, str));
            textView6.setText(Utility.convertLongToDuration(musicInfo.getDuration()));
            textView8.setText(musicInfo.getPath());
            textView7.setVisibility(View.GONE);
            textView11.setVisibility(View.GONE);
        }
    }

    public BottomSheetDialog build() {
        return this.mDialog;
    }
}
