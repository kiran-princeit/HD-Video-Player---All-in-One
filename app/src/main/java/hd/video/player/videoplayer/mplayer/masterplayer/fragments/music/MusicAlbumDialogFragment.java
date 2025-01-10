package hd.video.player.videoplayer.mplayer.masterplayer.fragments.music;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.activities.PlayMusicActivity;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.BottomMenuAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.adapters.music.MusicInfoAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.data.database.MyDatabase;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.MusicDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicAlbum;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
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
import hd.video.player.videoplayer.mplayer.masterplayer.presenter.music.MusicAlbumPresenter;
import hd.video.player.videoplayer.mplayer.masterplayer.util.FirebaseAnalyticsUtils;
import hd.video.player.videoplayer.mplayer.masterplayer.util.Utility;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant;
import hd.video.player.videoplayer.mplayer.masterplayer.util.constant.AppConstant.IntentExtra;

import hd.video.player.videoplayer.mplayer.masterplayer.view.music.MusicAlbumView;

public class MusicAlbumDialogFragment extends BaseDialogFragment<MusicAlbumPresenter> implements MusicAlbumView, MusicInfoAdapter.MusicInfoCallback {
    private final Uri artworkUri = Uri.parse("content://media/external/audio/albumart");
    ImageView ivAlbumArt;
    int ENCODING_PCM_32BIT = 805306368;
    private final ActivityResultLauncher<IntentSenderRequest> launcherDeleteMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            MusicAlbumDialogFragment.this.handleLauncherDeleteMusic((ActivityResult) obj);
        }
    });
    private final ActivityResultLauncher<IntentSenderRequest> launcherRenameMusic = registerForActivityResult(new StartIntentSenderForResult(), new ActivityResultCallback() {
        public final void onActivityResult(Object obj) {
            MusicAlbumDialogFragment.this.handleLauncherRenameMusic((ActivityResult) obj);
        }
    });
    private MusicInfoAdapter mAdapter;
    private Callback mCallback;
    private Context mContext;
    private MusicAlbum mMusicAlbum;
    private List<MusicInfo> mMusics = new ArrayList();
    private MusicInfo musicRequestDelete = null;
    private int positionMusicRequest = -1;
    RelativeLayout rlSearchView;
    RelativeLayout rlTitle;
    RecyclerView rvContent;
    SearchView searchView;
    TextView tvArtist;
    TextView tvInfoAlbum;
    TextView tvTitle;
    TextView tvTitle2;

    public interface Callback {
    }

    public void onUpdateAlbum(List<MusicAlbum> list) {
    }

    public MusicAlbumDialogFragment(MusicAlbum musicAlbum, Callback callback) {
        this.mMusicAlbum = musicAlbum;
        this.mCallback = callback;
    }

    public static MusicAlbumDialogFragment newInstance(MusicAlbum musicAlbum, Callback callback) {
        return new MusicAlbumDialogFragment(musicAlbum, callback);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    public MusicAlbumPresenter createPresenter() {
        return new MusicAlbumPresenter(this.mContext, this, new MusicDataRepository(requireActivity()));
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

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.dialog_fragment_music_album, viewGroup, false);
        this.ivAlbumArt = (ImageView) inflate.findViewById(R.id.iv_album_art);
        this.rlSearchView = (RelativeLayout) inflate.findViewById(R.id.rl_search_view);
        this.rlTitle = (RelativeLayout) inflate.findViewById(R.id.rl_title);
        this.rvContent = (RecyclerView) inflate.findViewById(R.id.rv_content);
        this.searchView = (SearchView) inflate.findViewById(R.id.search_view);
        this.tvArtist = (TextView) inflate.findViewById(R.id.tv_artist);
        this.tvInfoAlbum = (TextView) inflate.findViewById(R.id.tv_info_album);
        this.tvTitle = (TextView) inflate.findViewById(R.id.tv_title);
        this.tvTitle2 = (TextView) inflate.findViewById(R.id.tv_title_2);
        inflate.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        inflate.findViewById(R.id.tv_cancel_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlTitle.setVisibility(0);
                rlSearchView.setVisibility(4);
                MusicInfoAdapter musicInfoAdapter = mAdapter;
                if (musicInfoAdapter != null) {
                    musicInfoAdapter.updateMusicDataList(mMusics);
                }
            }
        });
        inflate.findViewById(R.id.iv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSearchView.setVisibility(0);
                rlTitle.setVisibility(4);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                searchView.requestFocusFromTouch();
            }
        });

        inflate.findViewById(R.id.iv_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomMenuDialogControl.getInstance().showSortDialogForMusic(mContext, new OkButtonClickListener() {
                    public final void onClick(int i, boolean z) {
                        MusicAlbumDialogFragment.this.m659xc0e98105(i, z);
                    }
                });
            }
        });
        inflate.findViewById(R.id.ll_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List list = mMusics;
                if (list != null && !list.isEmpty()) {
                    Intent intent = new Intent(mContext, PlayMusicActivity.class);
                    intent.putExtra(IntentExtra.EXTRA_MUSIC_NUMBER, 0);
                    intent.putExtra(IntentExtra.EXTRA_MUSIC_ARRAY, (ArrayList) getAllMusicId());
                    intent.addFlags(ENCODING_PCM_32BIT);
                    startActivity(intent);
                }
            }
        });
        MusicAlbum musicAlbum = this.mMusicAlbum;
        if (musicAlbum == null) {
            dismiss();
        } else {
            this.tvTitle.setText(musicAlbum.getAlbumName());
            this.tvTitle2.setText(this.mMusicAlbum.getAlbumName());
            this.tvArtist.setText(this.mMusicAlbum.getArtistName());
            ((RequestBuilder) ((RequestBuilder) ((RequestBuilder) Glide.with(this.mContext).load(ContentUris.withAppendedId(this.artworkUri, this.mMusicAlbum.getAlbumId())).placeholder(R.drawable.ic_music_album)).centerCrop()).error(R.drawable.ic_music_album)).into(this.ivAlbumArt);
            this.mAdapter = new MusicInfoAdapter(requireActivity(), false, this, null);
            this.rvContent.setLayoutManager(new LinearLayoutManager(this.mContext));
            this.rvContent.setAdapter(this.mAdapter);
            ((MusicAlbumPresenter) this.mPresenter).loadMusicListForAlbum(this.mMusicAlbum);
            this.searchView.setOnQueryTextListener(new OnQueryTextListener() {
                public boolean onQueryTextSubmit(String str) {
                    MusicAlbumDialogFragment.this.searchView.clearFocus();
                    return false;
                }

                public boolean onQueryTextChange(String str) {
                    List searchMusicByMusicName = Utility.searchMusicByMusicName(MusicAlbumDialogFragment.this.mMusics, str);
                    if (MusicAlbumDialogFragment.this.mAdapter != null) {
                        MusicAlbumDialogFragment.this.mAdapter.updateMusicDataList(searchMusicByMusicName);
                    }
                    return false;
                }
            });
        }
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
       
    }

    public void onUpdateMusicList(List<MusicInfo> list) {
        if (this.mMusicAlbum != null) {
            this.mMusics = list;
            MusicInfoAdapter musicInfoAdapter = this.mAdapter;
            if (musicInfoAdapter != null) {
                musicInfoAdapter.updateMusicDataList(list);
            }
            long j = 0;
            for (MusicInfo duration : list) {
                j += duration.getDuration();
            }
            j /= 60000;
            long lastYear = this.mMusicAlbum.getLastYear();
            Log.d("aaa ", "last year = " + lastYear + ", totalTime = " + j);
            if (lastYear > 0) {
                this.tvInfoAlbum.setText(this.mContext.getResources().getQuantityString(R.plurals.value_of_album_info, (int) this.mMusicAlbum.getNumberOfSongs(), new Object[]{Long.valueOf(this.mMusicAlbum.getLastYear()), Long.valueOf(this.mMusicAlbum.getNumberOfSongs()), Long.valueOf(j)}));
            } else {
                this.tvInfoAlbum.setText(this.mContext.getResources().getQuantityString(R.plurals.value_of_album_info_2, (int) this.mMusicAlbum.getNumberOfSongs(), new Object[]{Long.valueOf(this.mMusicAlbum.getNumberOfSongs()), Long.valueOf(j)}));
            }
        }
    }

    public void m659xc0e98105(int i, boolean z) {
        this.mAdapter.sortMusicList(i, z);
    }

    private List<Long> getAllMusicId() {
        ArrayList arrayList = new ArrayList();
        List<MusicInfo> list = this.mMusics;
        if (list == null) {
            return new ArrayList();
        }
        for (MusicInfo id : list) {
            arrayList.add(Long.valueOf(id.getId()));
        }
        return arrayList;
    }

    public void onMoreClick(final int i, final MusicInfo musicInfo) {
        final boolean checkFavoriteMusicIdExisted = MusicFavoriteUtil.checkFavoriteMusicIdExisted(this.mContext, musicInfo.getId());
        BottomMenuDialogControl.getInstance().showMoreDialogMusic(this.mContext, checkFavoriteMusicIdExisted, new BottomMenuAdapter.Callback() {
            public final void onClick(int i) {
                MusicAlbumDialogFragment.this.moreitemclick(musicInfo, checkFavoriteMusicIdExisted, i, i);
            }
        });
    }

    public void renameclick(String str, String str2, MusicInfo musicInfo, int i, String str3) {
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
                    MusicAlbumDialogFragment.this.handlerequest(musicInfo2, trim, str4, i2, str, uri);
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

    public void moreitemclick(final MusicInfo musicInfo, boolean z, final int i, int i2) {
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
                    MusicAlbumDialogFragment.this.renameclick(str4, str3, musicInfo2, i3, str);
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
                    ContentResolver contentResolver = MusicAlbumDialogFragment.this.mContext.getContentResolver();
                    PendingIntent pendingIntent = null;
                    try {
                        contentResolver.delete(parse, null, null);
                        MusicAlbumDialogFragment.this.mAdapter.removeItemAtPosition(i);
                        MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                        MusicFavoriteUtil.addFavoriteMusicId(MusicAlbumDialogFragment.this.mContext, musicInfo.getId(), false);
                    } catch (SecurityException e) {
                        if (VERSION.SDK_INT >= 30) {
                            pendingIntent = MediaStore.createDeleteRequest(contentResolver, Collections.singletonList(parse));
                        } else if (VERSION.SDK_INT < 29) {
                            MusicAlbumDialogFragment.this.mAdapter.removeItemAtPosition(i);
                            MusicDatabaseControl.getInstance().removeMusicById(musicInfo.getId());
                            MusicFavoriteUtil.addFavoriteMusicId(MusicAlbumDialogFragment.this.mContext, musicInfo.getId(), false);
                        } else {
                            pendingIntent = ((RecoverableSecurityException) e).getUserAction().getActionIntent();
                        }
                        if (pendingIntent != null) {
                            IntentSenderRequest build = new Builder(pendingIntent.getIntentSender()).build();
                            MusicAlbumDialogFragment.this.positionMusicRequest = i;
                            MusicAlbumDialogFragment.this.musicRequestDelete = musicInfo;
                            MusicAlbumDialogFragment.this.launcherDeleteMusic.launch(build);
                        }
                    }
                }
            }).setTitle(R.string.delete, this.mContext.getResources().getColor(R.color.color_FF6666)).setQuestion(R.string.question_remove_file).build().show();
            FirebaseAnalyticsUtils.putEventClick(this.mContext, str, "click_item_delete");
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
