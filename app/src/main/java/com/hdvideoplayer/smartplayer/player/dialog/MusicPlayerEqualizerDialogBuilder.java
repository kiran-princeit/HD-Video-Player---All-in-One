package com.hdvideoplayer.smartplayer.player.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.audiofx.BassBoost;
import android.media.audiofx.BassBoost.Settings;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.music.SoundPresetAdapter;


public class MusicPlayerEqualizerDialogBuilder {
    private static BassBoost bassBoost;
    private static Equalizer mEqualizer;
    public static int presetPos;
    public static int[] seekbarCustomPreset = new int[5];
    public static int[] seekbarPos = new int[5];
    private static Virtualizer virtualizer;
    private final Dialog dialogEqualizer;
    private final SeekBar[] seekBarFinal = new SeekBar[5];
    SoundPresetAdapter soundPresetAdapter;

    public MusicPlayerEqualizerDialogBuilder(Context context, int i) {
        SeekBar seekBar;
        final Context context2 = context;
        int i2 = i;
//        Dialog dialog = new Dialog(context2, Themes.THEMES_STYLE[new SettingPreferences(context2).getThemes()]);
        Dialog dialog = new Dialog(context2,R.style.AppBottomSheetDialogTheme);
        this.dialogEqualizer = dialog;
        short s = (short) 1;
        dialog.requestWindowFeature(1);
        short s2 = (short) 0;
        ((Window) Objects.requireNonNull(dialog.getWindow())).setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.dialog_bottom_music_equalizer);

        dialog.findViewById(R.id.view_click).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                dialogEqualizer.dismiss();
            }
        });
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rv_preset_horizontal);
        if (mEqualizer == null) {
            try {
                Equalizer equalizer = new Equalizer(Integer.MAX_VALUE, i2);
                mEqualizer = equalizer;
                equalizer.setEnabled(true);
            } catch (UnsupportedOperationException unused) {
                Toast.makeText(context2, R.string.device_not_supported, Toast.LENGTH_SHORT).show();
                this.dialogEqualizer.dismiss();
                return;
            }
        }

        if (bassBoost == null) {
            try {
                bassBoost = new BassBoost(Integer.MAX_VALUE, i2);
                bassBoost.setEnabled(true);
            } catch (UnsupportedOperationException unused2) {
                Toast.makeText(context2, R.string.device_not_supported, Toast.LENGTH_SHORT).show();
                this.dialogEqualizer.dismiss();
                return;
            }
        }

        if (virtualizer == null) {
            try {
                 virtualizer = new Virtualizer(Integer.MAX_VALUE, i2);
                virtualizer.setEnabled(true);
            } catch (UnsupportedOperationException unused3) {
                Toast.makeText(context2, R.string.device_not_supported, Toast.LENGTH_SHORT).show();
                this.dialogEqualizer.dismiss();
                return;
            }
        }
        final short s3 = mEqualizer.getBandLevelRange()[0];
        final short s4 = mEqualizer.getBandLevelRange()[1];
        short s5 = (short) 0;
        while (s5 < (short) 5) {
            seekBar = new SeekBar(context2);
            if (s5 == (short) 0) {
                seekBar = (SeekBar) this.dialogEqualizer.findViewById(R.id.seekBar1);
            } else if (s5 == s) {
                seekBar = (SeekBar) this.dialogEqualizer.findViewById(R.id.seekBar2);
            } else if (s5 == (short) 2) {
                seekBar = (SeekBar) this.dialogEqualizer.findViewById(R.id.seekBar3);
            } else if (s5 == (short) 3) {
                seekBar = (SeekBar) this.dialogEqualizer.findViewById(R.id.seekBar4);
            } else if (s5 == (short) 4) {
                seekBar = (SeekBar) this.dialogEqualizer.findViewById(R.id.seekBar5);
            }
            SeekBar seekBar2 = seekBar;
            this.seekBarFinal[s5] = seekBar2;
            final short s6 = s3;
            final short s7 = s5;
            final short s8 = s4;
            final RecyclerView recyclerView2 = recyclerView;
            seekBar2.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                public void onStopTrackingTouch(SeekBar seekBar) {
                }

                public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                    if (z) {
                        short s = s6;
                        MusicPlayerEqualizerDialogBuilder.mEqualizer.setBandLevel(s7, (short) ((int) (((float) s) + (((float) ((s8 - s) * i)) / 100.0f))));
                        MusicPlayerEqualizerDialogBuilder.seekbarCustomPreset[s7] = i;
                        MusicPlayerEqualizerDialogBuilder.seekbarPos[s7] = i;
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    MusicPlayerEqualizerDialogBuilder.this.soundPresetAdapter.setCurrentSelect(0);
                    recyclerView2.smoothScrollToPosition(0);
                    System.arraycopy(MusicPlayerEqualizerDialogBuilder.seekbarPos, 0, MusicPlayerEqualizerDialogBuilder.seekbarCustomPreset, 0, 5);
                }
            });

            s5 = (short) (s5 + 1);
            s = (short) 1;
        }
        for (short s9 = (short) 0; s9 < (short) 5; s9 = (short) (s9 + 1)) {
            short bandLevel = (short) ((int) ((((float) (mEqualizer.getBandLevel(s9) - s3)) * 100.0f) / ((float) (s4 - s3))));
            seekbarCustomPreset[s9] = bandLevel;
            seekbarPos[s9] = bandLevel;
            this.seekBarFinal[s9].setProgress(bandLevel);
        }
        seekBar = (SeekBar) this.dialogEqualizer.findViewById(R.id.sb_bass_booster);
        SeekBar seekBar3 = (SeekBar) this.dialogEqualizer.findViewById(R.id.sb_virtualizer);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z && bassBoost != null) {
                    BassBoost.Settings settings = new BassBoost.Settings(bassBoost.getProperties().toString());
                    settings.strength = (short) ((int) (((float) i) * 500.0f));
                    try {
                        bassBoost.setProperties(settings);
                    } catch (IllegalArgumentException e) {
                        // Handle exception
                    }
                }
            }
        });
        seekBar3.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    Virtualizer.Settings settings = new Virtualizer.Settings(MusicPlayerEqualizerDialogBuilder.virtualizer.getProperties().toString());
                    settings.strength = (short) ((int) (((float) i) * 500.0f));
                    try {
                        MusicPlayerEqualizerDialogBuilder.virtualizer.setProperties(settings);
                    } catch (IllegalArgumentException unused) {
                    }
                }
            }
        });
        seekBar.setProgress((int) (((float) new Settings(bassBoost.getProperties().toString()).strength) / 500.0f));
        seekBar3.setProgress((int) (((float) new Virtualizer.Settings(virtualizer.getProperties().toString()).strength) / 500.0f));
        recyclerView.setLayoutManager(new LinearLayoutManager(context2, RecyclerView.HORIZONTAL, false));
        ArrayList arrayList = new ArrayList();
        while (s2 < mEqualizer.getNumberOfPresets()) {
            arrayList.add(mEqualizer.getPresetName(s2));
            s2 = (short) (s2 + 1);
        }
        SoundPresetAdapter soundPresetAdapter = new SoundPresetAdapter(context2, arrayList, new SoundPresetAdapter.Callback() {
            public final void onSelect(int i) {
                MusicPlayerEqualizerDialogBuilder.this.soundSet(s3, s4, context2, i);
            }
        }, presetPos);
        this.soundPresetAdapter = soundPresetAdapter;
        recyclerView.setAdapter(soundPresetAdapter);
    }

    public static void release() {
        Equalizer equalizer = mEqualizer;
        if (equalizer != null) {
            equalizer.release();
        }
        BassBoost bassBoost1 = bassBoost;
        if (bassBoost1 != null) {
            bassBoost1.release();
        }
        Virtualizer virtualizer1 = virtualizer;
        if (virtualizer1 != null) {
            virtualizer1.release();
        }
        mEqualizer = null;
        bassBoost = null;
        virtualizer = null;
    }

    public void soundSet(short s, short s2, Context context, int i) {
        if (presetPos != i) {
            int i2 = 0;
            if (i == 0) {
                presetPos = 0;
                while (i2 < 5) {
                    mEqualizer.setBandLevel((short) i2, (short) ((int) (((float) s) + (((float) ((s2 - s) * seekbarCustomPreset[i2])) / 100.0f))));
                    this.seekBarFinal[i2].setProgress(seekbarCustomPreset[i2]);
                    i2 = (short) (i2 + 1);
                }
                return;
            }
            try {
                mEqualizer.usePreset((short) (i - 1));
                presetPos = i;
                for (short s3 = (short) 0; s3 < (short) 5; s3 = (short) (s3 + 1)) {
                    short bandLevel = (short) ((int) ((((float) (mEqualizer.getBandLevel(s3) - s)) * 100.0f) / ((float) (s2 - s))));
                    this.seekBarFinal[s3].setProgress(bandLevel);
                    seekbarPos[s3] = bandLevel;
                }
            } catch (Exception unused) {
                Toast.makeText(context, R.string.error_while_updating_equalizer, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Dialog build() {
        return this.dialogEqualizer;
    }
}
