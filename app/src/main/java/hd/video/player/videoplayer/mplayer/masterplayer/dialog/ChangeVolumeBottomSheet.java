package hd.video.player.videoplayer.mplayer.masterplayer.dialog;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import hd.video.player.videoplayer.mplayer.masterplayer.R;

public class ChangeVolumeBottomSheet extends BottomSheetDialogFragment {
    private AudioManager audioManager;
    private SeekBar seekBar_volume;

    //    public int getTheme() {
//        return R.style.AppBottomSheetDialogTheme;
//    }
    public int getTheme() {
        return R.style.CustomDialog;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.change_volume_bottom_sheet, viewGroup, false);
        this.audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        SeekBar seekBar = (SeekBar) inflate.findViewById(R.id.seekBar_volume);
        this.seekBar_volume = seekBar;
        seekBar.setMax(this.audioManager.getStreamMaxVolume(3));
        this.seekBar_volume.setProgress(this.audioManager.getStreamVolume(3));
        this.seekBar_volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                ChangeVolumeBottomSheet.this.audioManager.setStreamVolume(3, i, 0);
            }
        });
        return inflate;
    }
}
