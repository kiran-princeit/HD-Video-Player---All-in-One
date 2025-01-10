package hd.video.player.videoplayer.mplayer.masterplayer.base.entity;

import java.util.List;

public interface IExpandable<T> {
    int getLevel();

    List<T> getSubItems();

    boolean isExpanded();

    void setExpanded(boolean z);
}
