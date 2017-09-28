package work.petrichor.petrichor.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.net.URISyntaxException;

/**
 * @author mitscherlich
 * @date 2017/9/28
 **/

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            try {
                @SuppressLint("Recycle")
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor != null ? cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) : 0;
                if (cursor != null && cursor.moveToFirst())
                    return cursor.getString(column_index);
            } catch (Exception e) {
                Log.e(TAG, "Get resource path error. "  + e);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme()))
            return uri.getPath();
        return null;
    }
}
