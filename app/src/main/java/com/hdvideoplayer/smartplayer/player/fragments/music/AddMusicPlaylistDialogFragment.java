package com.hdvideoplayer.smartplayer.player.fragments.music;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hdvideoplayer.smartplayer.player.fragments.BaseDialogFragment;
import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.adapters.music.MusicAdapter;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicPlaylist;
import com.hdvideoplayer.smartplayer.player.data.repository.MusicDataRepository;
import com.hdvideoplayer.smartplayer.player.data.utils.SettingPreferences;
import com.hdvideoplayer.smartplayer.player.dialog.BottomMenuDialogControl;
import com.hdvideoplayer.smartplayer.player.dialog.SortDialogBuilder;
import com.hdvideoplayer.smartplayer.player.presenter.music.MusicMainPresenter;

import com.hdvideoplayer.smartplayer.player.util.constant.AppConstant;
import com.hdvideoplayer.smartplayer.player.view.music.MusicInfoView;

public class AddMusicPlaylistDialogFragment extends BaseDialogFragment<MusicMainPresenter> implements MusicInfoView, MusicAdapter.Callback {
    ImageView ivDone;
    ImageView ivSearch;
    ImageView ivViewMode;
    ProgressBar loading;
    private MusicAdapter mAdapter;
    private Callback mCallback;
    private Context mContext;
    private List<MusicInfo> mMusics = new ArrayList();
    private MusicPlaylist mPlaylist;
    RecyclerView rvMusic;
    TextView tvTitle;

    @Override
    public void onMoreClick(int i, int i2, MusicInfo MusicInfo) {

    }

    public interface Callback {
        void onDismiss();
    }


    public AddMusicPlaylistDialogFragment(MusicPlaylist musicPlaylist, Callback callback) {
        this.mPlaylist = musicPlaylist;
        this.mCallback = callback;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public MusicMainPresenter createPresenter() {
        return new MusicMainPresenter(this, new MusicDataRepository(requireActivity()));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, AppConstant.Themes.THEMES_STYLE[new SettingPreferences(this.mContext).getThemes()]);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(bundle);
        ((Window) Objects.requireNonNull(onCreateDialog.getWindow())).requestFeature(1);
        return onCreateDialog;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        this.mCallback.onDismiss();
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_playlist_video, viewGroup, false);

        ImageView imageView = (ImageView) inflate.findViewById(R.id.iv_done);
        this.ivDone = imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        inflate.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        this.ivSearch = (ImageView) inflate.findViewById(R.id.iv_search);
        this.ivViewMode = (ImageView) inflate.findViewById(R.id.iv_view_mode);
        this.loading = (ProgressBar) inflate.findViewById(R.id.loading);
        this.rvMusic = (RecyclerView) inflate.findViewById(R.id.rv_video_dialog);
        this.tvTitle = (TextView) inflate.findViewById(R.id.tv_folder_name);

        inflate.findViewById(R.id.iv_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuDialogControl.getInstance().showSortDialogForMusic(mContext, new SortDialogBuilder.OkButtonClickListener() {
                    @Override
                    public void onClick(int i, boolean z) {
                        mAdapter.sortMusicList(i, z);
                    }
                });
            }
        });
        this.tvTitle.setText(R.string.add_new_song);
        this.ivViewMode.setVisibility(8);
        this.ivSearch.setVisibility(4);
        this.ivDone.setVisibility(0);
        this.rvMusic.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.mAdapter = new MusicAdapter(requireActivity(), true, this,mPlaylist);
        this.rvMusic.setAdapter(this.mAdapter);
        Log.e("AddMusicPlaylistDialogFragment", "onCreateView: " + mMusics.size());
        ((MusicMainPresenter) this.mPresenter).openMusicsTab();
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    public void updateMusicList(List<MusicInfo> list) {
        MusicAdapter musicInfoAdapter = this.mAdapter;
        if (musicInfoAdapter != null) {
            musicInfoAdapter.updateMusicDataList(list);
        }
        this.mMusics = new ArrayList(list);
        this.loading.setVisibility(8);
        this.rvMusic.setVisibility(0);
    }
}
