package hd.video.player.videoplayer.mplayer.masterplayer.util.music;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import hd.video.player.videoplayer.mplayer.masterplayer.data.datasource.MusicDatabaseControl;
import hd.video.player.videoplayer.mplayer.masterplayer.data.entity.music.MusicInfo;
import hd.video.player.videoplayer.mplayer.masterplayer.util.video.VideoPlayerUtils;

public class MusicPlayerUtils {

    public static MusicInfo getMusicInfoFromUri(Context context, Uri uri) {
        String realPathFromURI = getRealPathFromURI(context, uri);
        MusicInfo musicByFilePath = realPathFromURI != null ? MusicDatabaseControl.getInstance().getMusicByFilePath(realPathFromURI) : null;
        if (musicByFilePath != null) {
            return musicByFilePath;
        }
        musicByFilePath = new MusicInfo();
        musicByFilePath.setUri(uri.toString());
        musicByFilePath.setDisplayName(VideoPlayerUtils.getFileName(context, uri));
        return musicByFilePath;
    }

    private static String getRealPathFromURI(Context context, Uri uri) {
        try {
            Cursor query = context.getContentResolver().query(uri, null, null, null, null);
            if (query == null) {
                return uri.getPath();
            }
            query.moveToFirst();
            String string = query.getString(query.getColumnIndex("_data"));
            query.close();
            return string;
        } catch (Exception unused) {
            return null;
        }
    }

    public static int dpToPx(int i) {
        return (int) (((float) i) * Resources.getSystem().getDisplayMetrics().density);
    }

//    public static Bitmap getThumbnailOfSong(Context context, String str, int i) {
//        i = dpToPx(i);
//        try {
//            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
//            mediaMetadataRetriever.setDataSource(context, Uri.parse(str));
//            byte[] embeddedPicture = mediaMetadataRetriever.getEmbeddedPicture();
//            if (embeddedPicture != null) {
//                return decodeSampledBitmapFromResource(embeddedPicture, i, i);
//            }
//            return null;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static Bitmap decodeSampledBitmapFromResource(byte[] bArr, int i, int i2) {
//        Options options = new Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
//        options.inSampleSize = calculateInSampleSize(options, i, i2);
//        options.inJustDecodeBounds = false;
//        return BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
//    }



    public static Bitmap getThumbnailOfSong(Context context, String str, int sizeInDp) {
        int sizeInPx = dpToPx(sizeInDp); // Convert dp to px
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, Uri.parse(str));
            byte[] embeddedPicture = retriever.getEmbeddedPicture();

            if (embeddedPicture != null) {
                return decodeSampledBitmapFromByteArray(embeddedPicture, sizeInPx, sizeInPx);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromByteArray(byte[] data, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to get the dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both height and width larger than the requested height and width
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

//    public static int dpToPx(int dp) {
//        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
//    }
//
//
//    public static int calculateInSampleSize(Options options, int i, int i2) {
//        int i3 = options.outHeight;
//        int i4 = options.outWidth;
//        int i5 = 1;
//        if (i3 > i2 || i4 > i) {
//            i3 /= 2;
//            i4 /= 2;
//            while (i3 / i5 >= i2 && i4 / i5 >= i) {
//                i5 *= 2;
//            }
//        }
//        return i5;
//    }
}
