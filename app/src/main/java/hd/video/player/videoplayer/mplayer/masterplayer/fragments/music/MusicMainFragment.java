package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.IntentSenderRequest.Builder;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicInfoAdapter;

import hd.video.player.videoplayer.mplayer.masterplayer.customview.NpaGridLayoutManager;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.MusicDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.MusicFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.AddToPlaylistDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MediaInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.SortDialogBuilder.OkButtonClickListener;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicMainPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicInfoView;

public class MusicMainFragment extends BaseFragment<MusicMainPresenter> implements MusicInfoView, MusicInfoAdapter.MusicInfoCallback {
    private final ContentObserver contentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z, Uri uri) {
            if (MusicMainFragment.this.mPresenter != null) {
                ((MusicMainPresenter) MusicMainFragment.this.mPresenter).openMusicsTab();
            }
        }
    };
    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            MusicMainFragment.this.handleLauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            MusicMainFragment.this.handleLauncherRenameMusic((ActivityResult) obj);
        }
    });
    private ProgressBar loading;
    private MusicInfoAdapter mAdapter;
    private Context mContext;
    private RecyclerView mRvContent;
    private MusicInfo musicRequestDelete = null;
    private int positionMusicRequest = -1;
    private SwipeRefreshLayout refreshLayout;
    private TextView tvTotal;
    ImageView iv_empty;

    private List<MusicInfo> mMusics = new ArrayList();
    private NpaGridLayoutManager mGridLayoutManager;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public MusicMainPresenter createPresenter() {
        return new MusicMainPresenter(this, new MusicDataRepository(requireActivity()));
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_music_main, viewGroup, false);
        this.mRvContent = (RecyclerView) inflate.findViewById(R.id.rv_content_tab);
        this.refreshLayout = (SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh);
        this.loading = (ProgressBar) inflate.findViewById(R.id.loading);
        this.tvTotal = (TextView) inflate.findViewById(R.id.tv_total);
        this.iv_empty = (ImageView) inflate.findViewById(R.id.iv_empty);
        inflate.findViewById(R.id.iv_sort).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                sortMusicList();
            }
        });
        this.mAdapter = new MusicInfoAdapter(requireActivity(), false, this, null);

        MusicInfoAdapter musicInfoAdapter = new MusicInfoAdapter(requireActivity(), false, this, mMusics);
        mRvContent.setAdapter(musicInfoAdapter);
        this.mRvContent.setLayoutManager(new LinearLayoutManager(this.mContext));
        this.mRvContent.setAdapter(this.mAdapter);
        ItemAnimator itemAnimator = this.mRvContent.getItemAnimator();
        if (itemAnimator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);
        }
        this.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            public final void onRefresh() {
                if (mPresenter != null) {
                    ((MusicMainPresenter) mPresenter).openMusicsTab();
                }
            }
        });
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

    }

    public void onResume() {
        super.onResume();
        if (this.mPresenter != null) {
            ((MusicMainPresenter) this.mPresenter).openMusicsTab();
        }
        FirebaseAnalyticsUtils.putScreenChecking(this.mContext, "Music_Home_Tab");
        requireActivity().getContentResolver().registerContentObserver(Media.EXTERNAL_CONTENT_URI, true, this.contentObserver);
    }

    public void onPause() {
        super.onPause();
        requireActivity().getContentResolver().unregisterContentObserver(this.contentObserver);
    }

    public void sortMusicList() {
        BottomMenuDialogControl.getInstance().showSortDialogForMusic(this.mContext, new OkButtonClickListener() {
            public final void onClick(int i, boolean z) {
                mAdapter.sortMusicList(i, z);
            }
        });
        FirebaseAnalyticsUtils.putEventClick(this.mContext, FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_LAYOUT, "click_icon_sort");
    }

    public void updateMusicList(List<MusicInfo> list) {
        this.loading.setVisibility(View.GONE);
        this.refreshLayout.setVisibility(View.VISIBLE);
        this.refreshLayout.setRefreshing(false);
        this.tvTotal.setText(getString(R.string.all_music, Integer.valueOf(list.size())));

        MusicInfoAdapter musicInfoAdapter = this.mAdapter;
        if (musicInfoAdapter != null) {
            musicInfoAdapter.updateMusicDataList(list);
        }
        if (list == null || list.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
        } else {
            iv_empty.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }


    public void onMoreClick(final int i, final MusicInfo musicInfo) {
        final boolean checkFavoriteMusicIdExisted = MusicFavoriteUtil.checkFavoriteMusicIdExisted(this.mContext, musicInfo.getId());
        BottomMenuDialogControl.getInstance().showMoreDialogMusic(this.mContext, checkFavoriteMusicIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                MusicMainFragment.this.MClick(musicInfo, checkFavoriteMusicIdExisted, i, i);
            }
        });
    }

    public void MClick(final MusicInfo musicInfo, boolean z, final int i, int i2) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_MORE;
        if (i2 == 0) {
            MusicFavoriteUtil.addFavoriteMusicId(this.mContext, musicInfo.getId(), !z);
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_favorite");
        } else if (i2 == 1) {
            List allPlaylist = MyDatabase.getInstance(this.mContext).musicPlaylistDAO().getAllPlaylist();
            if (allPlaylist == null || allPlaylist.isEmpty()) {
                Toast.makeText(this.mContext, "you haven't created any playlist yet", 0).show();
            } else {
                new AddToPlaylistDialogBuilder(this.mContext, musicInfo.getId(), allPlaylist, null).build().show();
            }
        } else if (i2 == 2) {
            String substring;
            String displayName = musicInfo.getDisplayName();
            String str2 = ".";
            if (displayName.indexOf(str2) > 0) {
                substring = displayName.substring(displayName.lastIndexOf(str2));
                displayName = displayName.substring(0, displayName.lastIndexOf(str2));
            } else {
                substring = "";
            }
            final String str3 = substring;
            final String str4 = displayName;
            final MusicInfo musicInfo2 = musicInfo;
            final int i3 = i;
            int colors = Utility.getColorAttr(mContext, Utility.getColorAttr(mContext, R.color.app_color), R.color.color_787D85);
            new InputDialogBuilder(this.mContext, new InputDialogBuilder.OkButtonClickListener() {
                public final void onClick(String str) {
                    MusicMainFragment.this.renameMusicName(str4, str3, musicInfo2, i3, str);
                }
            }, displayName).setTitle(R.string.rename, colors).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_rename_music");
        } else if (i2 == 3) {
            Utility.shareMusic(this.mContext, musicInfo);
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_share_music");
        } else if (i2 == 4) {
            new MediaInfoDialogBuilder(this.mContext, musicInfo).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_info");
        } else if (i2 == 5) {
            new QuestionDialogBuilder(this.mContext, new QuestionDialogBuilder.OkButtonClickListener() {
                public void onCancelClick() {
                }

                public void onOkClick() {
                    Uri parse = Uri.parse(musicInfo.getUri());
                    ContentResolver contentResolver = MusicMainFragment.this.mContext.getContentResolver();
                    PendingIntent pendingIntent = null;
                    try {
                        contentResolver.delete(parse, null, null);
                        MusicMainFragment.this.mAdapter.removeItemAtPosition(i);
                        MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                        MusicFavoriteUtil.addFavoriteMusicId(MusicMainFragment.this.mContext, musicInfo.getId(), false);
                    } catch (SecurityException e) {
                        if (VERSION.SDK_INT >= 30) {
                            pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                        } else if (VERSION.SDK_INT < 29) {
                            MusicMainFragment.this.mAdapter.removeItemAtPosition(i);
                            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                            MusicFavoriteUtil.addFavoriteMusicId(MusicMainFragment.this.mContext, musicInfo.getId(), false);
                        } else {
                            pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                        }
                        if (pendingIntent != null) {
                            IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                            MusicMainFragment.this.positionMusicRequest = i;
                            MusicMainFragment.this.musicRequestDelete = musicInfo;
                            MusicMainFragment.this.launcherDeleteMusic.launch(build);
                        }
                    }
                }
            }).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_delete");
        }
    }

    public void renameMusicName(String str, String str2, MusicInfo musicInfo, int i, String str3) {
        final String trim = str3.trim();
        if (TextUtils.isEmpty(str3)) {
            Toast.makeText(this.mContext, R.string.empty_music_name, 0).show();
        } else if (!trim.equals(str)) {
            if (VERSION.SDK_INT >= 30) {
                str = trim + str2;
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str);
                Uri parse = Uri.parse(musicInfo.getUri());
                musicInfo.setDisplayName(str);
                ContentResolver contentResolver = this.mContext.getContentResolver();
                try {
                    contentResolver.update(parse, contentValues, null);
                    this.mAdapter.notifyItemChanged(i);
                    return;
                } catch (SecurityException unused) {
                    IntentSenderRequest build = new Builder(MediaStore.createWriteRequest(contentResolver, Collections.singletonList(parse)).getIntentSender()).build();
                    this.positionMusicRequest = i;
                    this.launcherRenameMusic.launch(build);
                    return;
                }
            }
            final MusicInfo musicInfo2 = musicInfo;
            final String str4 = str2;
            final int i2 = i;
            Utility.renameAMusic(this.mContext, musicInfo, trim + str2, new OnScanCompletedListener() {
                public final void onScanCompleted(String str, Uri uri) {
                    MusicMainFragment.this.handlerequest(musicInfo2, trim, str4, i2, str, uri);
                }
            });
        }
    }

    public void handlerequest(MusicInfo musicInfo, String str, String str2, final int i, String str3, Uri uri) {
        if (uri != null) {
            long parseId;
            musicInfo.setDisplayName(str + str2);
            musicInfo.setPath(str3);
            musicInfo.setUri(uri.toString());
            try {
                parseId = ContentUris.parseId(uri);
            } catch (Exception unused) {
                parseId = 0;
            }
            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
            musicInfo.setId(parseId);
            MusicDatabaseControl.getInstance().addMusic(musicInfo);
            Log.d("aaa", " renameAMusic = " + musicInfo.toString());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public final void run() {
                    mAdapter.notifyItemChanged(i);
                }
            });
        }
    }

    public void handleLauncherDeleteMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionMusicRequest;
            if (i >= 0 && i < this.mAdapter.getItemCount()) {
                this.mAdapter.removeItemAtPosition(this.positionMusicRequest);
            }
            if (this.musicRequestDelete != null) {
                MusicDatabaseControl.getInstance().removeMusicById(this.musicRequestDelete.getId());
                MusicFavoriteUtil.addFavoriteMusicId(this.mContext, this.musicRequestDelete.getId(), false);
            }
        }
    }

    public void handleLauncherRenameMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionMusicRequest;
            if (i >= 0 && i < this.mAdapter.getItemCount()) {
                MusicInfo item = this.mAdapter.getItem(this.positionMusicRequest);
                if (item != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_display_name", item.getDisplayName());
                    Uri parse = Uri.parse(item.getUri());
                    ContentResolver contentResolver = this.mContext.getContentResolver();
                    try {
                        if (VERSION.SDK_INT >= 30) {
                            contentResolver.update(parse, contentValues, null);
                            this.mAdapter.notifyItemChanged(this.positionMusicRequest);
                        }
                    } catch (SecurityException unused) {
                        Toast.makeText(this.mContext, R.string.an_error_occurred, 0).show();
                    }
                }
            }
        }
    }
}
