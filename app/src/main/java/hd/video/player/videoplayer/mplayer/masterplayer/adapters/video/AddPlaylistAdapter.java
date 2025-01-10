package hd.video.player.videoplayer.mplayer.masterplayer.adapters.video;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.base.BaseQuickAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.base.BaseViewHolder;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.video.Playlist;

public class AddPlaylistAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {
    private final long mId;

    public AddPlaylistAdapter(long j) {
        super(R.layout.item_add_playlist);
        this.mId = j;
    }
    @Override
    public void convert(BaseViewHolder baseViewHolder, T t) {
        Log.d("AddPlaylistAdapter", "Converting item: " + t);
        TextView playlistName = baseViewHolder.getView(R.id.tv_name);
        ImageView checkBox = baseViewHolder.getView(R.id.iv_check);
        if (t instanceof Playlist) {
            Playlist playlist = (Playlist) t;
            playlistName.setText(playlist.getPlaylistName());
            checkBox.setActivated(playlist.getVideoIdList().contains(this.mId));
        } else if (t instanceof MusicPlaylist) {
            MusicPlaylist musicPlaylist = (MusicPlaylist) t;
            playlistName.setText(musicPlaylist.getPlaylistName());
            checkBox.setActivated(musicPlaylist.getMusicIdList().contains(this.mId));
        }
    }
}


