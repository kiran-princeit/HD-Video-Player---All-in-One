package com.hdvideoplayer.smartplayer.player;

import android.app.Dialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class VolumeDialog extends AppCompatDialogFragment {
    AudioManager audioManager;
    private ImageView closs;
    private SeekBar seekBar;
   
    public TextView volume_no;

    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.vol_dialog_item, (ViewGroup) null);
        builder.setView(inflate);
        getActivity().setVolumeControlStream(3);
        this.closs = (ImageView) inflate.findViewById(R.id.volume_close);
        this.volume_no = (TextView) inflate.findViewById(R.id.vol_number);
        this.seekBar = (SeekBar) inflate.findViewById(R.id.vol_seekbar);
        AudioManager audioManager2 = (AudioManager) getContext().getSystemService("audio");
        this.audioManager = audioManager2;
        this.seekBar.setMax(audioManager2.getStreamMaxVolume(3));
        this.seekBar.setProgress(this.audioManager.getStreamVolume(3));
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                VolumeDialog.this.audioManager.setStreamVolume(3, i, 0);
                VolumeDialog.this.volume_no.setText("" + Math.ceil((((double) VolumeDialog.this.audioManager.getStreamVolume(3)) / ((double) VolumeDialog.this.audioManager.getStreamMaxVolume(3))) * 100.0d));
            }
        });
        this.closs.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VolumeDialog.this.dismiss();
            }
        });
        return builder.create();
    }
}
