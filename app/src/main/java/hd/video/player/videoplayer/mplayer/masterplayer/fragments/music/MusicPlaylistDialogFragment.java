package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import static hd.video.player.videoplayer.mplayer.masterplayer.MyApplication.isNetworkConnected;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.IntentSenderRequest.Builder;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hd.video.player.videoplayer.mplayer.masterplayer.R;

import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicInfoAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adsprosimple.AdManager;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.MusicDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.MusicFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.SettingPreferences;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.AddToPlaylistDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MediaInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.SortDialogBuilder.OkButtonClickListener;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseDialogFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicDialogPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;

import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant;
import hd.video.player.videoplayer.mplayer.masterplayer.view.MusicDialogView;

public class MusicPlaylistDialogFragment extends BaseDialogFragment<MusicDialogPresenter> implements MusicDialogView, MusicInfoAdapter.MusicInfoCallback {
    ImageView ivAddMusic;
    ImageView ivViewMode;
    ImageView iv_empty;
    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            MusicPlaylistDialogFragment.this.handleLauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            MusicPlaylistDialogFragment.this.handleLauncherRenameMusic((ActivityResult) obj);
        }
    });
    ProgressBar loading;
    private MusicInfoAdapter mAdapter;
    private Callback mCallback;
    private Context mContext;
    private boolean mIsNewPlaylist;
    private MusicArtist mMusicArtist;
    private List<MusicInfo> mMusics;
    private MusicPlaylist mPlaylist;
    private int mType;
    private MusicInfo musicRequestDelete = null;
    private int positionMusicRequest = -1;
    RelativeLayout rlSearchView;
    RelativeLayout rlTitle;
    RecyclerView rvMusicDialog;
    SearchView searchView;
    TextView tvFolderName;

    public interface Callback {
        void onDialogDismiss();
    }

    public MusicPlaylistDialogFragment(int i) {
        this.mType = i;
    }

    public MusicPlaylistDialogFragment(int i, MusicPlaylist musicPlaylist, Callback callback) {
        this.mType = i;
        this.mCallback = callback;
        this.mPlaylist = musicPlaylist;
    }

    public MusicPlaylistDialogFragment(int i, MusicArtist musicArtist, Callback callback) {
        this.mType = i;
        this.mCallback = callback;
        this.mMusicArtist = musicArtist;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public MusicDialogPresenter createPresenter() {
        return new MusicDialogPresenter(this, new MusicDataRepository(requireActivity()));
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
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onDialogDismiss();
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_playlist_video, viewGroup, false);

       inflate.findViewById(R.id.tv_cancel_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSearchView.setVisibility(4);
                rlTitle.setVisibility(0);
                MusicInfoAdapter musicInfoAdapter = mAdapter;
                if (musicInfoAdapter != null) {
                    musicInfoAdapter.updateMusicDataList(mMusics);
                }
            }
        });
        inflate.findViewById(R.id.iv_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        inflate.findViewById(R.id.iv_add_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddVideoClick();
                Log.e("MusicPlaylistDialogFragment", "onClick: ");
            }
        });
        inflate.findViewById(R.id.iv_sort).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuDialogControl.getInstance().showSortDialogForMusic(mContext, new OkButtonClickListener() {
                    public final void onClick(int i, boolean z) {
                        MusicPlaylistDialogFragment.this.sortClick(i, z);
                    }
                });
            }
        });
        inflate.findViewById(R.id.iv_search).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSearchView.setVisibility(0);
                rlTitle.setVisibility(4);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        });
        this.ivAddMusic = (ImageView) inflate.findViewById(R.id.iv_add_video);
        this.iv_empty = (ImageView) inflate.findViewById(R.id.iv_empty);
        this.ivViewMode = (ImageView) inflate.findViewById(R.id.iv_view_mode);
        this.loading = (ProgressBar) inflate.findViewById(R.id.loading);
        this.rlSearchView = (RelativeLayout) inflate.findViewById(R.id.rl_search_view);
        this.rlTitle = (RelativeLayout) inflate.findViewById(R.id.rl_title);
        this.rvMusicDialog = (RecyclerView) inflate.findViewById(R.id.rv_video_dialog);
        this.searchView = (SearchView) inflate.findViewById(R.id.search_view);
        this.tvFolderName = (TextView) inflate.findViewById(R.id.tv_folder_name);
        this.ivViewMode.setVisibility(8);
        this.mAdapter = new MusicInfoAdapter(requireActivity(), false, this, null);


        this.rvMusicDialog.setAdapter(this.mAdapter);
        this.rvMusicDialog.setLayoutManager(new LinearLayoutManager(this.mContext));
        int i = this.mType;

//        if (this.mType == 1) {
//            View inflate2 = LayoutInflater.from(this.mContext).inflate(R.layout.layout_empty_video_in_playlist, null);
//            ((TextView) inflate2.findViewById(R.id.tv_no_video)).setText(R.string.no_songs);
//            ((TextView) inflate2.findViewById(R.id.tv_add_video)).setText(R.string.add_song);
//            inflate2.findViewById(R.id.rl_add_video).setOnClickListener(new OnClickListener() {
//                public final void onClick(View view) {
//                    onAddVideoClick();
//                }
//            });
////        } else {
////            viewGroup2 = (ViewGroup) null;
////            ((TextView) LayoutInflater.from(this.mContext).inflate(R.layout.item_empty_data, null).findViewById(R.id.tv_history)).setText(R.string.no_songs);
//        }
//        else

        if (i == 2) {
            this.tvFolderName.setText(R.string.favorite);
            ((MusicDialogPresenter) this.mPresenter).getAllFavoriteMusic();
        } else if (i == 3) {
            MusicArtist musicArtist = this.mMusicArtist;
            if (musicArtist != null) {
                this.tvFolderName.setText(musicArtist.getArtistName());
                ((MusicDialogPresenter) this.mPresenter).getAllMusicOfArtist(this.mMusicArtist);
            }
        } else if (i == 1) {
            MusicPlaylist musicPlaylist = this.mPlaylist;
            if (musicPlaylist != null) {
                this.tvFolderName.setText(musicPlaylist.getPlaylistName());
                ((MusicDialogPresenter) this.mPresenter).getAllMusicOfPlaylist(this.mPlaylist);
            }
            this.ivAddMusic.setVisibility(0);
        } else {
            dismiss();
        }
        this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                MusicPlaylistDialogFragment.this.searchView.clearFocus();
                return false;
            }

            public boolean onQueryTextChange(String str) {
                List searchMusicByMusicName = Utility.searchMusicByMusicName(MusicPlaylistDialogFragment.this.mMusics, str);
                if (MusicPlaylistDialogFragment.this.mAdapter != null) {
                    MusicPlaylistDialogFragment.this.mAdapter.updateMusicDataList(searchMusicByMusicName);
                }
                return false;
            }
        });
        if (this.mIsNewPlaylist) {
            onAddVideoClick();
        }
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

    }

    public void onMoreClick(final int i, final MusicInfo musicInfo) {
        final boolean checkFavoriteMusicIdExisted = MusicFavoriteUtil.checkFavoriteMusicIdExisted(this.mContext, musicInfo.getId());
        BottomMenuDialogControl.getInstance().showMoreDialogMusic(this.mContext, checkFavoriteMusicIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                MusicPlaylistDialogFragment.this.moreItemClick(musicInfo, checkFavoriteMusicIdExisted, i, i);
            }
        });
    }


    public void createNewPalyList(String str, String str2, MusicInfo musicInfo, int i, String str3) {
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
                    MusicPlaylistDialogFragment.this.renameMusicRequest(musicInfo2, trim, str4, i2, str, uri);
                }
            });
        }
    }

    public void renameMusicRequest(MusicInfo musicInfo, String str, String str2, final int i, String str3, Uri uri) {
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

    public void moreItemClick(final MusicInfo musicInfo, boolean z, final int i, int i2) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_MORE;
        if (i2 == 0) {
            MusicFavoriteUtil.addFavoriteMusicId(this.mContext, musicInfo.getId(), !z);
            if (z && this.mType == 2 && this.mPresenter != null) {
                ((MusicDialogPresenter) this.mPresenter).getAllFavoriteMusic();
            }
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_favorite");
        } else if (i2 == 1) {
            List allPlaylist = MyDatabase.getInstance(this.mContext).musicPlaylistDAO().getAllPlaylist();
            if (allPlaylist == null || allPlaylist.isEmpty()) {
                Toast.makeText(this.mContext, "you haven't created any playlist yet", 0).show();
            } else {
                new AddToPlaylistDialogBuilder(this.mContext, musicInfo.getId(), allPlaylist, new AddToPlaylistDialogBuilder.OkButtonClickListener() {
                    public final void onClose() {
                        if (mPresenter != null && mType == 1) {
                            ((MusicDialogPresenter) mPresenter).getAllMusicOfPlaylist(mPlaylist);
                        }
                    }
                }).build().show();
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
                    MusicPlaylistDialogFragment.this.createNewPalyList(str4, str3, musicInfo2, i3, str);
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
                    if (parse != null) {
                        ContentResolver contentResolver = MusicPlaylistDialogFragment.this.mContext.getContentResolver();
                        try {
                            // Attempt deletion
                            int deletedRows = contentResolver.delete(parse, null, null);
                            if (deletedRows > 0) {
                                // Successfully deleted
                                MusicPlaylistDialogFragment.this.mAdapter.removeItemAtPosition(i);
                                MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                                MusicFavoriteUtil.addFavoriteMusicId(MusicPlaylistDialogFragment.this.mContext, musicInfo.getId(), false);
                            } else {
                                // Handle the case where no rows were deleted (e.g., URI not found)
                                Log.e("Delete Error", "No rows deleted. URI might not exist: " + musicInfo.getUri());
                            }
                        } catch (SecurityException e) {
                            // Handle security exception based on API levels
                            handleSecurityException(contentResolver, parse, musicInfo, i, e);
                        }
                    } else {
                        Log.e("Delete Error", "Invalid URI: " + musicInfo.getUri());
                    }
                }
            }).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_delete");
        }
    }

    private void handleSecurityException(ContentResolver contentResolver, Uri parse, MusicInfo musicInfo, int i, SecurityException e) {
        if (Build.VERSION.SDK_INT >= 30) {
            // Handle for API >= 30
            PendingIntent pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
            if (pendingIntent != null) {
                IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                MusicPlaylistDialogFragment.this.positionMusicRequest = i;
                MusicPlaylistDialogFragment.this.musicRequestDelete = musicInfo;
                MusicPlaylistDialogFragment.this.launcherDeleteMusic.launch(build);
            }
        } else if (Build.VERSION.SDK_INT < 29) {
            // Handle for API < 29 (pre-scoped storage)
            MusicPlaylistDialogFragment.this.mAdapter.removeItemAtPosition(i);
            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
            MusicFavoriteUtil.addFavoriteMusicId(MusicPlaylistDialogFragment.this.mContext, musicInfo.getId(), false);
        } else {
            // For API levels between 29 and 30, handle RecoverableSecurityException
            if (e instanceof RecoverableSecurityException) {
                PendingIntent pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                if (pendingIntent != null) {
                    IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                    MusicPlaylistDialogFragment.this.positionMusicRequest = i;
                    MusicPlaylistDialogFragment.this.musicRequestDelete = musicInfo;
                    MusicPlaylistDialogFragment.this.launcherDeleteMusic.launch(build);
                }
            }
        }
    }

    public void onMusicLoader(List<MusicInfo> list) {

        this.mMusics = new ArrayList(list);

        if (mMusics == null && mMusics.isEmpty()) {
            iv_empty.setVisibility(View.VISIBLE);
            rvMusicDialog.setVisibility(View.GONE);
        } else {
            this.mAdapter.updateMusicDataList(list);
            this.loading.setVisibility(View.GONE);
            iv_empty.setVisibility(View.GONE);
            this.rvMusicDialog.setVisibility(View.VISIBLE);
        }
    }

    public void sortClick(int i, boolean z) {
        MusicInfoAdapter musicInfoAdapter = this.mAdapter;
        if (musicInfoAdapter != null) {
            musicInfoAdapter.sortMusicList(i, z);
        }
    }

    public void onAddVideoClick() {
        new AddMusicPlaylistDialogFragment(this.mPlaylist, new AddMusicPlaylistDialogFragment.Callback() {
            @Override
            public void onDismiss() {
                if (mPresenter != null) {
                    ((MusicDialogPresenter) mPresenter).getAllMusicOfPlaylist(mPlaylist);
                }
            }
        }).show(getChildFragmentManager().beginTransaction(), "dialog_playlist_add_music");
    }

    public void setIsNewPlaylist(boolean z) {
        this.mIsNewPlaylist = z;
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
