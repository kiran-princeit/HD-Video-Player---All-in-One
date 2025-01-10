package hd.video.player.videoplayer.mplayer.masterplayer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.video.AddPlaylistAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.base.BaseQuickAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;

public class AddToPlaylistDialogBuilder<T> {
    private final Dialog mDialog;
    AddPlaylistAdapter addPlaylistAdapter;

    public interface OkButtonClickListener {
        void onClose();
    }

    public AddToPlaylistDialogBuilder(Context context, long j, List<T> list, OkButtonClickListener okButtonClickListener) {
        final Context context2 = context;
        Dialog dialog = new Dialog(context, R.style.CustomDialog);
        this.mDialog = dialog;
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.dialog_add_to_playlist);
        Window window = this.mDialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(R.color.transparent);
            window.setGravity(Gravity.CENTER);
        }

        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.rv_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        final long j2 = j;
        addPlaylistAdapter = new AddPlaylistAdapter(j);
        addPlaylistAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            public final void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                View viewByPosition = addPlaylistAdapter.getViewByPosition(recyclerView, i, R.id.iv_check);
                if (viewByPosition != null) {
                    boolean z = !viewByPosition.isActivated();
                    viewByPosition.setActivated(z);
                    Object item = addPlaylistAdapter.getItem(i);
                    if (item instanceof Playlist) {
                        Playlist playlist = (Playlist) item;
                        if (z) {
                            playlist.getVideoIdList().add(Long.valueOf(j2));
                        } else {
                            playlist.getVideoIdList().remove(Long.valueOf(j2));
                        }
                        MyDatabase.getInstance(context2).videoPlaylistDAO().updateVideoListForPlaylist(playlist.getDateAdded(), playlist.getVideoIdList());
                    } else if (item instanceof MusicPlaylist) {
                        MusicPlaylist musicPlaylist = (MusicPlaylist) item;
                        if (z) {
                            musicPlaylist.getMusicIdList().add(Long.valueOf(j2));
                        } else {
                            musicPlaylist.getMusicIdList().remove(Long.valueOf(j2));
                        }
                        MyDatabase.getInstance(context2).musicPlaylistDAO().updateMusicListForPlaylist(musicPlaylist.getDateAdded(), musicPlaylist.getMusicIdList());
                    }
                }
            }
        });
        addPlaylistAdapter.setNewData(list);
        Log.d("AddToPlaylistDialog", "Playlist data size: " + (list != null ? list.size() : 0));
        recyclerView.setAdapter(addPlaylistAdapter);
        dialog.findViewById(R.id.tv_dialog_ok).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                AddToPlaylistDialogBuilder.this.mDialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                dialog.dismiss();
            }
        });
        final OkButtonClickListener okButtonClickListener2 = okButtonClickListener;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                if (okButtonClickListener != null) {
                    okButtonClickListener.onClose();
                }

            }
        });
    }


    public Dialog build() {
        return this.mDialog;
    }
}

