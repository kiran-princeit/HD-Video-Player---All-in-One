package hd.video.player.videoplayer.mplayer.masterplayer.adapters.music;

import java.util.List;
import hd.video.player.videoplayer.mplayer.masterplayer.R;
import hd.video.player.videoplayer.mplayer.masterplayer.base.BaseMultiItemQuickAdapter;
import hd.video.player.videoplayer.mplayer.masterplayer.base.BaseViewHolder;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicPlaylist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.utils.MusicFavoriteUtil;

public class MusicPlaylistAdapter extends BaseMultiItemQuickAdapter<MusicPlaylist, BaseViewHolder> {
    public MusicPlaylistAdapter(List<MusicPlaylist> list) {
        super(list);
        addItemType(1, R.layout.item_folder_list1);
        addItemType(2, R.layout.item_folder_grid);
    }

    public void convert(BaseViewHolder baseViewHolder, MusicPlaylist musicPlaylist) {
        if (baseViewHolder.getLayoutPosition() == 0) {
            int size = MusicFavoriteUtil.getAllFavoriteMusicId(this.mContext).size();
            baseViewHolder.setText(R.id.tv_folder_name, R.string.favorite).setText(R.id.tv_number, this.mContext.getResources().getQuantityString(R.plurals.value_of_music, size, new Object[]{Integer.valueOf(size)})).setImageResource(R.id.iv_folder, R.drawable.ic_fav_videoplaylist).setGone(R.id.iv_more, false);
            return;
        }
        int size2 = musicPlaylist.getMusicIdList().size();
        baseViewHolder.setText(R.id.tv_folder_name, musicPlaylist.getPlaylistName()).setText(R.id.tv_number, this.mContext.getResources().getQuantityString(R.plurals.value_of_music, size2, new Object[]{Integer.valueOf(size2)})).setImageResource(R.id.iv_folder, R.drawable.ic_music_playlist).setVisible(R.id.iv_more, true).addOnClickListener(R.id.iv_more);
    }
}
