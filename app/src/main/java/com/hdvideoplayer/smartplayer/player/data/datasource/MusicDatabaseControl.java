package com.hdvideoplayer.smartplayer.player.data.datasource;

import android.os.Build;
import android.util.LongSparseArray;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.hdvideoplayer.smartplayer.player.data.entity.music.MusicInfo;

public class MusicDatabaseControl {
    private static volatile MusicDatabaseControl sInstance;
    private Set<MusicInfo> mSetMusics = new HashSet();
    private LongSparseArray<MusicInfo> mSparseMusics = new LongSparseArray();

    private MusicDatabaseControl() {
    }

    public static MusicDatabaseControl getInstance() {
        if (sInstance == null) {
            synchronized (MusicDatabaseControl.class) {
                if (sInstance == null) {
                    sInstance = new MusicDatabaseControl();
                }
            }
        }
        return sInstance;
    }

    public void addMusic(MusicInfo musicInfo) {
        this.mSparseMusics.put(musicInfo.getId(), musicInfo);
        this.mSetMusics.add(musicInfo);
    }

    public void setNewData(Set<MusicInfo> set, LongSparseArray<MusicInfo> longSparseArray) {
        this.mSparseMusics = longSparseArray;
        this.mSetMusics = set;
    }

    public Set<MusicInfo> getAllMusics() {
        return this.mSetMusics;
    }

    public MusicInfo getMusicById(long j) {
        return (MusicInfo) this.mSparseMusics.get(j);
    }

    public void removeMusicById(long j) {
        this.mSetMusics.remove(this.mSparseMusics.get(j));
        this.mSparseMusics.remove(j);
    }

    public List<MusicInfo> searchMusicByMusicName(final String str) {
        ArrayList arrayList = new ArrayList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return (List) this.mSetMusics.stream().filter(new Predicate() {
                public final boolean test(Object obj) {
                    return ((MusicInfo) obj).getDisplayName().toLowerCase().contains(str.trim().toLowerCase());
                }
            }).collect(Collectors.toList());
        }
        return arrayList;
    }

    public MusicInfo getMusicByFilePath(String str) {
        for (MusicInfo musicInfo : this.mSetMusics) {
            if (musicInfo.getPath().equals(str)) {
                return musicInfo;
            }
        }
        return null;
    }
}
