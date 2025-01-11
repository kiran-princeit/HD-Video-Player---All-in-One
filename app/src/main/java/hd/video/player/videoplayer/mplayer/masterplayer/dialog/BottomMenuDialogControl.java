package hd.video.player.videoplayer.mplayer.masterplayer.dialog;

import android.app.Dialog;
import android.content.Context;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Arrays;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter.Callback;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;

public class BottomMenuDialogControl {
    private static BottomMenuDialogControl sInstance;
    Dialog mDialog;

    public static BottomMenuDialogControl getInstance() {
        if (sInstance == null) {
            sInstance = new BottomMenuDialogControl();
        }
        return sInstance;
    }

    public static void shortMusicClick(Context context, SortDialogBuilder.OkButtonClickListener okButtonClickListener, int i, boolean z) {
        Utility.setMusicSortModeAndAscending(context, i, z);
        okButtonClickListener.onClick(i, z);
    }

    public static void sortVideoClick(Context context, SortDialogBuilder.OkButtonClickListener okButtonClickListener, int i, boolean z) {
        Utility.setVideoSortModeAndAscending(context, i, z);
        okButtonClickListener.onClick(i, z);
    }

    public void showMoreDialogVideo(Context context, boolean z, Callback callback) {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            int i;
            int i2;
            if (z) {
                i = R.string.remove_from_favorite;
                i2 = R.drawable.ic_favorite_false;
            } else {
                i = R.string.add_to_favorite;
                i2 = R.drawable.ic_favorite_true;
            }
            final Callback callback2 = callback;

            BottomSheetDialog build = new BottomMenuDialogBuilder(
                    context,
                    new BottomMenuAdapter(
                            new ArrayList<>(Arrays.asList(
                                    i,
                                    R.string.add_to_playlist,
                                    R.string.rename,
                                    R.string.share,
                                    R.string.info,
                                    R.string.delete_video
                            )),
                            new ArrayList<>(Arrays.asList(
                                    i2,
                                    R.drawable.ic_menu_add_to_playlist,
                                    R.drawable.ic_menu_rename,
                                    R.drawable.ic_menu_share,
                                    R.drawable.ic_menu_info,
                                    R.drawable.ic_delete
                            )),
                            new Callback() {
                                public final void onClick(int i) {
                                    BottomMenuDialogControl.this.showmoredialogvideo(callback2, i);
                                }
                            }
                    )
            ).build();
            this.mDialog = build;
            build.show();
        }
    }

    public void showmoredialogvideo(Callback callback, int i) {
        this.mDialog.dismiss();
        callback.onClick(i);
    }

    public void showMoreDialogMusic(Context context, boolean z, final Callback callback) {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            int i;
            int i2;
            if (z) {
                i = R.string.remove_from_favorite;
                i2 = R.drawable.ic_favorite_false;
            } else {
                i = R.string.add_to_favorite;
                i2 = R.drawable.ic_favorite_true;
            }
            BottomSheetDialog build = new BottomMenuDialogBuilder(
                    context, new BottomMenuAdapter(
                    new ArrayList(Arrays.asList(
                            new Integer[]{Integer.valueOf(i),
                                    Integer.valueOf(R.string.add_to_playlist),
                                    Integer.valueOf(R.string.rename),
                                    Integer.valueOf(R.string.share),
                                    Integer.valueOf(R.string.info),
                                    Integer.valueOf(R.string.delete_song)})),

                    new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(i2),
                            Integer.valueOf(R.drawable.ic_menu_add_to_playlist),
                            Integer.valueOf(R.drawable.ic_menu_rename),
                            Integer.valueOf(R.drawable.ic_menu_share),
                            Integer.valueOf(R.drawable.ic_menu_info),
                            Integer.valueOf(R.drawable.ic_delete)})), new Callback() {


                public final void onClick(int i) {
                    BottomMenuDialogControl.this.m604x4fb7305e(callback, i);
                }
            })).build();
            this.mDialog = build;
            build.show();
        }
    }

    public void m604x4fb7305e(Callback callback, int i) {
        this.mDialog.dismiss();
        callback.onClick(i);
    }

    public void showMoreDialogVideoFolder(Context context, final Callback callback) {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            BottomSheetDialog build = new BottomMenuDialogBuilder(context, new BottomMenuAdapter(
                    new ArrayList(Arrays.asList(new Integer[]{
                            Integer.valueOf(R.string.delete_folder),
                            Integer.valueOf(R.string.info)})),
                    new ArrayList(Arrays.asList(new Integer[]{
                            Integer.valueOf(R.drawable.ic_delete),
                            Integer.valueOf(R.drawable.ic_menu_info)})), new Callback() {

                public final void onClick(int i) {
                    BottomMenuDialogControl.this.moredialogclick(callback, i);
                }
            })).build();
            this.mDialog = build;
            build.show();
        }
    }

    public void moredialogclick(Callback callback, int i) {
        this.mDialog.dismiss();
        callback.onClick(i);
    }

    public void showMoreDialogPlaylist(Context context, final Callback callback) {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            BottomSheetDialog build = new BottomMenuDialogBuilder(context, new BottomMenuAdapter(new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.string.rename), Integer.valueOf(R.string.duplicate_playlist), Integer.valueOf(R.string.delete_playlist)})), new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.ic_menu_rename), Integer.valueOf(R.drawable.ic_menu_duplicate), Integer.valueOf(R.drawable.ic_delete)})), new Callback() {
                public final void onClick(int i) {
                    BottomMenuDialogControl.this.dialogclick(callback, i);
                }
            })).build();
            this.mDialog = build;
            build.show();
        }
    }

    public void dialogclick(Callback callback, int i) {
        this.mDialog.dismiss();
        callback.onClick(i);
    }

    public void showMoreDialogHistory(Context context, final Callback callback) {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            BottomSheetDialog build = new BottomMenuDialogBuilder(context, new BottomMenuAdapter(new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.string.delete_from_history), Integer.valueOf(R.string.delete_file), Integer.valueOf(R.string.info)})), new ArrayList(Arrays.asList(new Integer[]{Integer.valueOf(R.drawable.ic_delete), Integer.valueOf(R.drawable.ic_delete), Integer.valueOf(R.drawable.ic_menu_info)})), new Callback() {
                public final void onClick(int i) {
                    BottomMenuDialogControl.this.m603xacf4150(callback, i);
                }
            })).build();
            this.mDialog = build;
            build.show();
        }
    }

    public void m603xacf4150(Callback callback, int i) {
        this.mDialog.dismiss();
        callback.onClick(i);
    }
    public void showSortDialogForMusic(final Context context, final SortDialogBuilder.OkButtonClickListener okButtonClickListener) {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            Dialog build = new SortDialogBuilder(context, new SortDialogBuilder.OkButtonClickListener() {
                public final void onClick(int i, boolean z) {
                    BottomMenuDialogControl.shortMusicClick(context, okButtonClickListener, i, z);
                }
            }, Utility.getMusicSortMode(context), Utility.getMusicSortAscending(context)).build();
            this.mDialog = build;
            build.show();
        }
    }

    public void showSortDialogForVideo(final Context context, final SortDialogBuilder.OkButtonClickListener okButtonClickListener) {
        Dialog dialog = this.mDialog;
        if (dialog == null || !dialog.isShowing()) {
            Dialog build = new SortDialogBuilder(context, new SortDialogBuilder.OkButtonClickListener() {
                public final void onClick(int i, boolean z) {
                    BottomMenuDialogControl.sortVideoClick(context, okButtonClickListener, i, z);
                }
            }, Utility.getVideoSortMode(context), Utility.getVideoSortAscending(context)).build();
            this.mDialog = build;
            build.show();
        }
    }
}
