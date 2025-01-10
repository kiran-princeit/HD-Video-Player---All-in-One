package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SearchView;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicInfoAdapter;

import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.MusicDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.data.repository.MusicDataRepository;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.MusicFavoriteUtil;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.SettingPreferences;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.AddToPlaylistDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.BottomMenuDialogControl;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.InputDialogBuilder.OkButtonClickListener;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.MediaInfoDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.dialog.QuestionDialogBuilder;
import hd.video.player.videoplayer.mplayer.masterplayer.fragments.BaseDialogFragment;
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicSearchingPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;

import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant;
import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicSearchView;


public class SearchMusicDialogFragment extends BaseDialogFragment<MusicSearchingPresenter> implements MusicSearchView, MusicInfoAdapter.MusicInfoCallback {
    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            SearchMusicDialogFragment.this.handlelauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            SearchMusicDialogFragment.this.handlelauncherRenameMusic((ActivityResult) obj);
        }
    });

    private MusicInfoAdapter musicAdapter;
    private Context context;
    private MusicInfo musicRequestDelete = null;
    private int positionMusicRequest = -1;
    RecyclerView rvSearchResult;
    SearchView musicSearchView;
    TextView tv_search_result;

    public static SearchMusicDialogFragment newInstance() {
        return new SearchMusicDialogFragment();
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public MusicSearchingPresenter createPresenter() {
        return new MusicSearchingPresenter(this, new MusicDataRepository(requireActivity()));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setStyle(0, AppConstant.Themes.THEMES_STYLE[new SettingPreferences(this.context).getThemes()]);
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(bundle);
        ((Window) Objects.requireNonNull(onCreateDialog.getWindow())).requestFeature(1);
        return onCreateDialog;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_search, viewGroup, false);
        this.musicSearchView = (SearchView) inflate.findViewById(R.id.search_view);
        this.tv_search_result = (TextView) inflate.findViewById(R.id.tv_search_result);
        this.rvSearchResult = (RecyclerView) inflate.findViewById(R.id.rv_search_result);
        inflate.findViewById(R.id.tv_cancel_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        MusicInfoAdapter musicInfoAdapter = new MusicInfoAdapter(requireActivity(), false, this, null);
        this.musicAdapter = musicInfoAdapter;
        this.rvSearchResult.setAdapter(musicInfoAdapter);
        this.rvSearchResult.setLayoutManager(new LinearLayoutManager(this.context));
        this.musicSearchView.setFocusable(true);
        this.musicSearchView.setIconified(false);
        this.musicSearchView.requestFocusFromTouch();
        this.musicSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                SearchMusicDialogFragment.this.musicSearchView.clearFocus();
                return false;
            }

            public boolean onQueryTextChange(String str) {
                ((MusicSearchingPresenter) SearchMusicDialogFragment.this.mPresenter).searchMusicByMusicName(str);
                return false;
            }
        });
        ((MusicSearchingPresenter) this.mPresenter).searchMusicByMusicName("");
        return inflate;
    }


    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

    }

    public void onSearchMusic(List<MusicInfo> list) {
        MusicInfoAdapter musicInfoAdapter = this.musicAdapter;
        if (musicInfoAdapter != null) {
            musicInfoAdapter.updateMusicDataList(list);
        }
    }

    public void onMoreClick(final int i, final MusicInfo musicInfo) {
        final boolean checkFavoriteMusicIdExisted = MusicFavoriteUtil.checkFavoriteMusicIdExisted(this.context, musicInfo.getId());
        BottomMenuDialogControl.getInstance().showMoreDialogMusic(this.context, checkFavoriteMusicIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                SearchMusicDialogFragment.this.handleMoreOptionsClick(musicInfo, checkFavoriteMusicIdExisted, i, i);
            }
        });
    }

    public void renameMusic(String str, String str2, MusicInfo musicInfo, int i, String str3) {
        final String trim = str3.trim();
        if (TextUtils.isEmpty(str3)) {
            Toast.makeText(this.context, R.string.empty_music_name, 0).show();
        } else if (!trim.equals(str)) {
            if (VERSION.SDK_INT >= 30) {
                str = trim + str2;
                ContentValues contentValues = new ContentValues();
                contentValues.put("_display_name", str);
                Uri parse = Uri.parse(musicInfo.getUri());
                musicInfo.setDisplayName(str);
                ContentResolver contentResolver = this.context.getContentResolver();
                try {
                    contentResolver.update(parse, contentValues, null);
                    this.musicAdapter.notifyItemChanged(i);
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
            Utility.renameAMusic(this.context, musicInfo, trim + str2, new OnScanCompletedListener() {
                public final void onScanCompleted(String str, Uri uri) {
                    SearchMusicDialogFragment.this.handleRenameSecurityException(musicInfo2, trim, str4, i2, str, uri);
                }
            });
        }
    }

    public void handleRenameSecurityException(MusicInfo musicInfo, String str, String str2, final int i, String str3, Uri uri) {
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
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public final void run() {
                    musicAdapter.notifyItemChanged(i);
                }
            });
        }
    }

    public void handleMoreOptionsClick(final MusicInfo musicInfo, boolean z, final int i, int i2) {
        String str = FirebaseAnalyticsUtils.EVENT_PROX_MUSIC_MORE;
        if (i2 == 0) {
            MusicFavoriteUtil.addFavoriteMusicId(this.context, musicInfo.getId(), !z);
            FirebaseAnalyticsUtils.putEventClick(this.context, str, "click_item_favorite");
        } else if (i2 == 1) {
            List allPlaylist = MyDatabase.getInstance(this.context).musicPlaylistDAO().getAllPlaylist();
            if (allPlaylist == null || allPlaylist.isEmpty()) {
                Toast.makeText(this.context, "you haven't created any playlist yet", 0).show();
            } else {
                new AddToPlaylistDialogBuilder(this.context, musicInfo.getId(), allPlaylist, null).build().show();
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
            int colors = Utility.getColorAttr(context, Utility.getColorAttr(context, R.color.app_color), R.color.color_787D85);
            new InputDialogBuilder(this.context, new OkButtonClickListener() {
                public final void onClick(String str) {
                    SearchMusicDialogFragment.this.renameMusic(str4, str3, musicInfo2, i3, str);
                }
            }, displayName).setTitle(R.string.rename, colors).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.context, str, "click_rename_music");
        } else if (i2 == 3) {
            Utility.shareMusic(this.context, musicInfo);
            FirebaseAnalyticsUtils.putEventClick(this.context, str, "click_share_music");
        } else if (i2 == 4) {
            new MediaInfoDialogBuilder(this.context, musicInfo).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.context, str, "click_item_info");
        } else if (i2 == 5) {
            new QuestionDialogBuilder(this.context, new QuestionDialogBuilder.OkButtonClickListener() {
                public void onCancelClick() {
                }

                public void onOkClick() {
                    Uri parse = Uri.parse(musicInfo.getUri());
                    ContentResolver contentResolver = SearchMusicDialogFragment.this.context.getContentResolver();
                    PendingIntent pendingIntent = null;
                    try {
                        contentResolver.delete(parse, null, null);
                        SearchMusicDialogFragment.this.musicAdapter.removeItemAtPosition(i);
                        MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                        MusicFavoriteUtil.addFavoriteMusicId(SearchMusicDialogFragment.this.context, musicInfo.getId(), false);
                    } catch (SecurityException e) {
                        if (VERSION.SDK_INT >= 30) {
                            pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                        } else if (VERSION.SDK_INT < 29) {
                            SearchMusicDialogFragment.this.musicAdapter.removeItemAtPosition(i);
                            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                            MusicFavoriteUtil.addFavoriteMusicId(SearchMusicDialogFragment.this.context, musicInfo.getId(), false);
                        } else {
                            pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                        }
                        if (pendingIntent != null) {
                            IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                            SearchMusicDialogFragment.this.positionMusicRequest = i;
                            SearchMusicDialogFragment.this.musicRequestDelete = musicInfo;
                            SearchMusicDialogFragment.this.launcherDeleteMusic.launch(build);
                        }
                    }
                }
            }).setTitle(R.string.delete, this.context.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.context, str, "click_item_delete");
        }
    }

    public void handlelauncherDeleteMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionMusicRequest;
            if (i >= 0 && i < this.musicAdapter.getItemCount()) {
                this.musicAdapter.removeItemAtPosition(this.positionMusicRequest);
            }
            if (this.musicRequestDelete != null) {
                MusicDatabaseControl.getInstance().removeMusicById(this.musicRequestDelete.getId());
                MusicFavoriteUtil.addFavoriteMusicId(this.context, this.musicRequestDelete.getId(), false);
            }
        }
    }

    public void handlelauncherRenameMusic(ActivityResult activityResult) {
        if (activityResult.getResultCode() == -1) {
            int i = this.positionMusicRequest;
            if (i >= 0 && i < this.musicAdapter.getItemCount()) {
                MusicInfo item = this.musicAdapter.getItem(this.positionMusicRequest);
                if (item != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("_display_name", item.getDisplayName());
                    Uri parse = Uri.parse(item.getUri());
                    ContentResolver contentResolver = this.context.getContentResolver();
                    try {
                        if (VERSION.SDK_INT >= 30) {
                            contentResolver.update(parse, contentValues, null);
                            this.musicAdapter.notifyItemChanged(this.positionMusicRequest);
                        }
                    } catch (SecurityException unused) {
                        Toast.makeText(this.context, R.string.an_error_occurred, 0).show();
                    }
                }
            }
        }
    }
}



