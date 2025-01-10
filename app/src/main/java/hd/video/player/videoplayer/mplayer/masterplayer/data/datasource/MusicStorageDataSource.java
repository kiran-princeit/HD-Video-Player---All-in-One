package hd.video.player.videoplayer.mplayer.masterplayer.data.datasource;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.util.LongSparseArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicAlbum;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicArtist;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;

public class MusicStorageDataSource {
    private Context mContext;

    public MusicStorageDataSource(Context context) {
        this.mContext = context;
        if (context != null) {
            this.mContext = context.getApplicationContext();
        } else {
            Log.e("MusicStorageDataSource", "Context is null in constructor");
        }
    }

    public List<MusicInfo> getAllMusicFromStorage() {
        if (mContext == null) {
            Log.e("MusicStorageDataSource", "mContext is null, cannot get content resolver");
            return new ArrayList<>(); // Return empty list or handle the error gracefully
        }
        List<MusicInfo> musicList = new ArrayList<>();
        Uri uri;
        LongSparseArray<MusicInfo> musicInfoSparseArray = new LongSparseArray<>();
        HashSet<MusicInfo> musicSet = new HashSet<>();
        String[] columns = {"_id", "_display_name", "artist", "album", "_data", "duration", "date_added", "_size"};

        // Determine the correct URI based on the SDK version
        uri = (Build.VERSION.SDK_INT >= 29) ? MediaStore.Audio.Media.getContentUri("external") : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        try (Cursor cursor = mContext.getContentResolver().query(uri, columns, null, null, null)) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow("_id");
                int nameColumn = cursor.getColumnIndexOrThrow("_display_name");
                int artistColumn = cursor.getColumnIndexOrThrow("artist");
                int albumColumn = cursor.getColumnIndexOrThrow("album");
                int dataColumn = cursor.getColumnIndexOrThrow("_data");
                int durationColumn = cursor.getColumnIndexOrThrow("duration");
                int dateAddedColumn = cursor.getColumnIndexOrThrow("date_added");
                int sizeColumn = cursor.getColumnIndexOrThrow("_size");

                while (cursor.moveToNext()) {
                    MusicInfo musicInfo = new MusicInfo();
                    long id = cursor.getLong(idColumn);
                    musicInfo.setId(id);
                    musicInfo.setUri(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString());
                    musicInfo.setPath(cursor.getString(dataColumn));
                    musicInfo.setDisplayName(cursor.getString(nameColumn));
                    musicInfo.setArtist(cursor.getString(artistColumn));
                    musicInfo.setAlbum(cursor.getString(albumColumn));
                    musicInfo.setDuration(cursor.getLong(durationColumn));
                    musicInfo.setDate(cursor.getLong(dateAddedColumn));
                    musicInfo.setSize(cursor.getLong(sizeColumn));

                    musicSet.add(musicInfo);
                    musicInfoSparseArray.put(musicInfo.getId(), musicInfo);
                }
            }
        } catch (Exception e) {
            Log.e("MusicStorageDataSource", "Error querying music storage", e);
        }

        // Add other music files from a different source (e.g., files not listed in MediaStore)
        for (MusicInfo otherMusic : getAllOtherMusicFile()) {
            if (musicInfoSparseArray.get(otherMusic.getId()) == null) {
                musicSet.add(otherMusic);
                musicInfoSparseArray.put(otherMusic.getId(), otherMusic);
            }
        }

        MusicDatabaseControl.getInstance().setNewData(musicSet, musicInfoSparseArray);
        return new ArrayList<>(musicSet);
    }

    public List<MusicAlbum> getAllMusicAlbum() {
        List<MusicAlbum> albumList = new ArrayList<>();
        Uri uri = (Build.VERSION.SDK_INT >= 29) ? MediaStore.Audio.Albums.getContentUri("external") : MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        try (Cursor cursor = mContext.getContentResolver().query(uri, new String[]{"_id", "album", "artist", "numsongs", "maxyear"}, null, null, "album ASC")) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow("_id");
                int albumColumn = cursor.getColumnIndexOrThrow("album");
                int artistColumn = cursor.getColumnIndexOrThrow("artist");
                int numSongsColumn = cursor.getColumnIndexOrThrow("numsongs");
                int maxYearColumn = cursor.getColumnIndexOrThrow("maxyear");

                while (cursor.moveToNext()) {
                    MusicAlbum musicAlbum = new MusicAlbum();
                    musicAlbum.setAlbumId(cursor.getLong(idColumn));
                    musicAlbum.setAlbumName(cursor.getString(albumColumn));
                    musicAlbum.setArtistName(cursor.getString(artistColumn));
                    musicAlbum.setNumberOfSongs(cursor.getLong(numSongsColumn));
                    musicAlbum.setLastYear(cursor.getLong(maxYearColumn));

                    albumList.add(musicAlbum);
                }
            }
        } catch (Exception e) {
            Log.e("MusicStorageDataSource", "Error querying music albums", e);
        }

        return albumList;
    }

    public List<MusicInfo> getAllMusicFromAlbum(long albumId) {
        List<MusicInfo> musicList = new ArrayList<>();
        Uri uri = (Build.VERSION.SDK_INT >= 29) ? MediaStore.Audio.Media.getContentUri("external") : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] selectionArgs = {String.valueOf(albumId)};
        String selection = "album_id = ? AND _data NOT NULL";
        String[] columns = {"_id", "_display_name", "artist", "album", "_data", "duration", "date_added", "_size"};

        try (Cursor cursor = mContext.getContentResolver().query(uri, columns, selection, selectionArgs, "_display_name ASC")) {
            if (cursor != null) {
                int idColumn = cursor.getColumnIndexOrThrow("_id");
                int nameColumn = cursor.getColumnIndexOrThrow("_display_name");
                int artistColumn = cursor.getColumnIndexOrThrow("artist");
                int albumColumn = cursor.getColumnIndexOrThrow("album");
                int dataColumn = cursor.getColumnIndexOrThrow("_data");
                int durationColumn = cursor.getColumnIndexOrThrow("duration");
                int dateAddedColumn = cursor.getColumnIndexOrThrow("date_added");
                int sizeColumn = cursor.getColumnIndexOrThrow("_size");

                while (cursor.moveToNext()) {
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(cursor.getLong(idColumn));
                    musicInfo.setPath(cursor.getString(dataColumn));
                    musicInfo.setDisplayName(cursor.getString(nameColumn));
                    musicInfo.setArtist(cursor.getString(artistColumn));
                    musicInfo.setAlbum(cursor.getString(albumColumn));
                    musicInfo.setDuration(cursor.getLong(durationColumn));
                    musicInfo.setDate(cursor.getLong(dateAddedColumn));
                    musicInfo.setSize(cursor.getLong(sizeColumn));

                    musicList.add(musicInfo);
                }
            }
        } catch (Exception e) {
            Log.e("MusicStorageDataSource", "Error querying music from album", e);
        }

        return musicList;
    }

    public List<MusicArtist> getAllMusicArtist() {
        Log.d("aaa", "getAllMusicArtist");
        Set<MusicArtist> musicArtists = new HashSet<>();
        Cursor query = null;
        try {
            query = this.mContext.getContentResolver().query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    new String[]{"_id", "artist"},
                    null,
                    null,
                    null
            );
            if (query != null) {
                int idColumn = query.getColumnIndexOrThrow("_id");
                int artistColumn = query.getColumnIndexOrThrow("artist");
                while (query.moveToNext()) {
                    MusicArtist musicArtist = new MusicArtist();
                    musicArtist.setArtistId(query.getLong(idColumn));
                    musicArtist.setArtistName(query.getString(artistColumn));
                    musicArtist.setMusicList(getAllSongsOfArtist(query.getLong(idColumn)));
                    musicArtists.add(musicArtist);
                }
            }
        } catch (Exception e) {
            Log.e("aaa", "Error retrieving music artists", e);
        } finally {
            if (query != null) {
                query.close();
            }
        }
        return new ArrayList<>(musicArtists);
    }

    public List<MusicInfo> getAllSongsOfArtist(long artistId) {
        Set<MusicInfo> musicInfos = new HashSet<>();
        Cursor query = null;
        try {
            Uri uri = (Build.VERSION.SDK_INT >= 29)
                    ? MediaStore.Audio.Media.getContentUri("external")
                    : MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {"_id", "_display_name", "artist", "album", "_data", "duration", "date_added", "_size"};
            String[] selectionArgs = {String.valueOf(artistId)};

            query = this.mContext.getContentResolver().query(
                    uri,
                    projection,
                    "artist_id = ? AND _data NOT NULL",
                    selectionArgs,
                    "_display_name ASC"
            );

            if (query != null) {
                int idColumn = query.getColumnIndexOrThrow("_id");
                int displayNameColumn = query.getColumnIndexOrThrow("_display_name");
                int artistColumn = query.getColumnIndexOrThrow("artist");
                int albumColumn = query.getColumnIndexOrThrow("album");
                int dataColumn = query.getColumnIndexOrThrow("_data");
                int durationColumn = query.getColumnIndexOrThrow("duration");
                int dateAddedColumn = query.getColumnIndexOrThrow("date_added");
                int sizeColumn = query.getColumnIndexOrThrow("_size");

                while (query.moveToNext()) {
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(query.getLong(idColumn));
                    musicInfo.setPath(query.getString(dataColumn));
                    musicInfo.setDisplayName(query.getString(displayNameColumn));
                    musicInfo.setArtist(query.getString(artistColumn));
                    musicInfo.setAlbum(query.getString(albumColumn));
                    musicInfo.setDuration(query.getLong(durationColumn));
                    musicInfo.setDate(query.getLong(dateAddedColumn));
                    musicInfo.setSize(query.getLong(sizeColumn));
                    musicInfos.add(musicInfo);
                }
            }
        } catch (Exception e) {
            Log.e("aaa", "Error retrieving songs for artist", e);
        } finally {
            if (query != null) {
                query.close();
            }
        }
        return new ArrayList<>(musicInfos);
    }

    public List<MusicAlbum> getAllAlbumOfArtist(MusicArtist musicArtist) {
        Set<MusicAlbum> musicAlbums = new HashSet<>();
        Cursor query = null;
        try {
            Uri uri = (Build.VERSION.SDK_INT >= 29)
                    ? MediaStore.Audio.Albums.getContentUri("external")
                    : MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

            String[] selectionArgs = {String.valueOf(musicArtist.getArtistId())};

            query = this.mContext.getContentResolver().query(
                    uri,
                    new String[]{"_id", "album", "artist", "numsongs", "maxyear"},
                    "artist_id = ?",
                    selectionArgs,
                    "album ASC"
            );

            if (query != null) {
                int idColumn = query.getColumnIndexOrThrow("_id");
                int albumColumn = query.getColumnIndexOrThrow("album");
                int artistColumn = query.getColumnIndexOrThrow("artist");
                int numSongsColumn = query.getColumnIndexOrThrow("numsongs");
                int maxYearColumn = query.getColumnIndexOrThrow("maxyear");

                while (query.moveToNext()) {
                    MusicAlbum musicAlbum = new MusicAlbum();
                    musicAlbum.setAlbumId(query.getLong(idColumn));
                    musicAlbum.setAlbumName(query.getString(albumColumn));
                    musicAlbum.setArtistName(query.getString(artistColumn));
                    musicAlbum.setNumberOfSongs(query.getLong(numSongsColumn));
                    musicAlbum.setLastYear(query.getLong(maxYearColumn));
                    musicAlbums.add(musicAlbum);
                }
            }
        } catch (Exception e) {
            Log.e("aaa", "Error retrieving albums for artist", e);
        } finally {
            if (query != null) {
                query.close();
            }
        }
        return new ArrayList<>(musicAlbums);
    }

    public List<MusicInfo> getAllOtherMusicFile() {
        Set<MusicInfo> musicFiles = new HashSet<>();
        Cursor query = null;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            query = this.mContext.getContentResolver().query(
                    MediaStore.Files.getContentUri("external"),
                    new String[]{"_id", "_display_name", "_data", "date_added", "_size"},
                    "_data LIKE '%.mp3' OR _data LIKE '%.ac3'",
                    null,
                    "_id DESC"
            );

            if (query != null) {
                int idColumn = query.getColumnIndexOrThrow("_id");
                int displayNameColumn = query.getColumnIndexOrThrow("_display_name");
                int dataColumn = query.getColumnIndexOrThrow("_data");
                int dateAddedColumn = query.getColumnIndexOrThrow("date_added");
                int sizeColumn = query.getColumnIndexOrThrow("_size");

                while (query.moveToNext()) {
                    String path = query.getString(dataColumn);
                    MusicInfo musicInfo = new MusicInfo();
                    musicInfo.setId(query.getLong(idColumn));
                    musicInfo.setPath(path);
                    musicInfo.setDisplayName(query.getString(displayNameColumn));
                    musicInfo.setDate(query.getLong(dateAddedColumn));
                    musicInfo.setSize(query.getLong(sizeColumn));

                    try {
                        mediaMetadataRetriever.setDataSource(path);
                        musicInfo.setDuration(Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
                        musicInfo.setAlbum(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                        musicInfo.setArtist(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                    } catch (Exception e) {
                        Log.e("aaa", "Error extracting metadata", e);
                    }
                    musicFiles.add(musicInfo);
                }
            }
        } catch (Exception e) {
            Log.e("aaa", "Error retrieving other music files", e);
        } finally {
            if (query != null) {
                query.close();
            }
            try {
                mediaMetadataRetriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>(musicFiles);
    }
}
