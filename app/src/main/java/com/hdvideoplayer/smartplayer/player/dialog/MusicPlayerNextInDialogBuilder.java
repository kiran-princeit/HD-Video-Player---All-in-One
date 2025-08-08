package com.hdvideoplayer.smartplayer.player.dialog;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.List;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.music.NextInMusicPlaylistAdapter;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;

public class MusicPlayerNextInDialogBuilder {
    private NextInMusicPlaylistAdapter mAdapter;
    private BottomSheetDialog mDialog;
    private RecyclerView recyclerView ;

    public MusicPlayerNextInDialogBuilder(Context context, List<MusicInfo> list, int i, NextInMusicPlaylistAdapter.Callback callback) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomDialog);
        this.mDialog = bottomSheetDialog;
        bottomSheetDialog.setContentView(R.layout.dialog_music_player_next_in);

        // Initialize recyclerView after setting the content view
        this.recyclerView = this.mDialog.findViewById(R.id.rv_next_in_playlist);

        if (list == null || list.isEmpty()) {
            this.mDialog.dismiss();
            return;
        }

        NextInMusicPlaylistAdapter nextInMusicPlaylistAdapter = new NextInMusicPlaylistAdapter(context, list, i, callback);
        this.mAdapter = nextInMusicPlaylistAdapter;
        this.recyclerView.setAdapter(nextInMusicPlaylistAdapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.recyclerView.scrollToPosition(i);
    }

    public void updateCurrentSong(int i) {
        this.mAdapter.updateCurrentPosition(i);
    }

    public BottomSheetDialog build() {
        return this.mDialog;
    }
}



