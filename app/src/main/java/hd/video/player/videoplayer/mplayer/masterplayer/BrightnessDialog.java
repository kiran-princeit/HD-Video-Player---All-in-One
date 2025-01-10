package hd.video.player.videoplayer.mplayer.masterplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class BrightnessDialog extends AppCompatDialogFragment {

    public TextView brightnessValueTextView;
    private ImageView closeButton;
    private SeekBar brightnessSeekBar;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.brightness_dialog_layout, null);
        dialogBuilder.setView(dialogView);

        closeButton = dialogView.findViewById(R.id.close_button);
        brightnessValueTextView = dialogView.findViewById(R.id.brightness_value_text);
        brightnessSeekBar = dialogView.findViewById(R.id.brightness_seekbar);

        int currentBrightness = Settings.System.getInt(getContext().getContentResolver(), "screen_brightness", 0);
        brightnessValueTextView.setText(String.valueOf(currentBrightness));
        brightnessSeekBar.setProgress(currentBrightness);

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional implementation
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optional implementation
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Context appContext = BrightnessDialog.this.getContext().getApplicationContext();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(appContext)) {
                        int adjustedBrightness = (progress * 255) / 255;
                        brightnessValueTextView.setText(String.valueOf(adjustedBrightness));
                        Settings.System.putInt(appContext.getContentResolver(), "screen_brightness_mode", 0);
                        Settings.System.putInt(appContext.getContentResolver(), "screen_brightness", adjustedBrightness);
                        return;
                    }
                }
                Toast.makeText(appContext, "Enable write settings for brightness control", Toast.LENGTH_SHORT).show();
                Intent settingsIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                settingsIntent.setData(Uri.parse("package:" + appContext.getPackageName()));
                BrightnessDialog.this.startActivityForResult(settingsIntent, 0);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrightnessDialog.this.dismiss();
            }
        });

        return dialogBuilder.create();
    }
}

