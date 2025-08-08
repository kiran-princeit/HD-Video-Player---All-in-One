package com.hdvideoplayer.smartplayer.player.adapters.video;

import java.util.List;

import com.hdvideoplayer.smartplayer.player.R;
import com.hdvideoplayer.smartplayer.player.base.BaseMultiItemQuickAdapter;
import com.hdvideoplayer.smartplayer.player.base.BaseViewHolder;
import com.hdvideoplayer.smartplayer.player.data.datasource.VideoDatabaseControl;
import com.hdvideoplayer.smartplayer.player.data.entity.video.Playlist;
import com.hdvideoplayer.smartplayer.player.data.utils.VideoFavoriteUtil;

public class PlaylistAdapter extends BaseMultiItemQuickAdapter<Playlist, BaseViewHolder> {
    public PlaylistAdapter(List<Playlist> list) {
        super(list);
        addItemType(1, R.layout.playlist_item);
        addItemType(2, R.layout.item_folder_grid);
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Playlist playlist) {
        int layoutPosition = baseViewHolder.getLayoutPosition();

        if (layoutPosition == 0) {
            // Recent Videos
            int size = VideoDatabaseControl.getInstance().getAllRecentlyVideo().size();
            baseViewHolder.setText(R.id.tv_folder_name, R.string.recently_added)
                    .setText(R.id.tv_number, mContext.getResources().getQuantityString(R.plurals.value_of_video, size, size))
                    .setGone(R.id.imgFavorite, false)
                    .setVisible(R.id.imgRecentFav, true)
                    .setGone(R.id.iv_folder, false)
                    .setImageResource(R.id.imgRecentFav, R.drawable.ic_recent_video);
        } else if (layoutPosition == 1) {
            // Favorite Videos
            int size = VideoFavoriteUtil.getAllFavoriteVideoId(mContext).size();
            baseViewHolder.setText(R.id.tv_folder_name, R.string.favorite)
                    .setText(R.id.tv_number, mContext.getResources().getQuantityString(R.plurals.value_of_video, size, size))
                    .setVisible(R.id.imgFavorite, true)
                    .setGone(R.id.imgRecentFav, false)
                    .setGone(R.id.iv_folder, false)
                    .setImageResource(R.id.imgFavorite, R.drawable.ic_fav_videoplaylist);
        } else {
            // Video Playlist
            int videoCount = playlist.getVideoIdList().size();
            baseViewHolder.setText(R.id.tv_folder_name, playlist.getPlaylistName())
                    .setText(R.id.tv_number, mContext.getResources().getQuantityString(R.plurals.value_of_video, videoCount, videoCount))
                    .setGone(R.id.imgFavorite, false)
                    .setGone(R.id.imgRecentFav, false)
                    .setVisible(R.id.iv_folder, true)
                    .setImageResource(R.id.iv_folder, R.drawable.ic_video_playlist);
        }
    }

}
