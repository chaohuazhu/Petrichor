package work.petrichor.petrichor.modules.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mitscherlich.
 * @date 2017/7/27.
 **/

public class AssetUtils {

    private static final String ASSET_LIST_FILE_NAME = "assets.lst";

    public static boolean copy(Context context) throws IOException {
        List<String> assets = new ArrayList<String>();
        String appDir = context.getExternalFilesDir(null).getPath();
        if (null == appDir || "".equals(appDir)) return false;
        else appDir += "/";

        List<String> lists = getAssetsList(context);

        for (String it : lists)
            if (!new File(appDir, it).exists())
                assets.add(it);

        for (String it : assets)
            copy(context, it);

        return true;
    }

    private static File copy(Context context, String path) throws IOException {
        InputStream inStream = context.getAssets().open(new File(path).getPath());
        File dstFile = new File(context.getExternalFilesDir(null), path);

        dstFile.getParentFile().mkdir();

        OutputStream outStream = new FileOutputStream(dstFile);
        byte[] buffer = new byte[1024];
        int nRead = 0;

        while ((nRead = inStream.read(buffer)) != -1) {
            if (nRead == 0) {
                nRead = inStream.read();
                if (nRead < 0) break;
                outStream.write(nRead);
                continue;
            }
            outStream.write(buffer, 0, nRead);
        }
        outStream.close();

        return dstFile;
    }

    private static List<String> getAssetsList(Context context) throws IOException {
        List<String> files = new ArrayList<String>();
        InputStream listFile = context.getAssets().open(new File(ASSET_LIST_FILE_NAME).getPath());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listFile));

        String path = "";
        while (null != (path = bufferedReader.readLine()))
            files.add(path);

        return files;
    }
}
