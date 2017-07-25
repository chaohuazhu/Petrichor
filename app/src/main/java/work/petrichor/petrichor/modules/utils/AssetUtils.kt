package work.petrichor.petrichor.modules.utils

import android.content.Context
import java.io.*

/**
 * @author  Mitscherlich.
 * @data    2017/7/18.
 */
object AssetUtils {
    private val ASSET_LIST_FILENAME = "assets.lst"

    fun copy(context: Context) : Boolean {
        var files: List<String> = emptyList()
        val appDir = (context.getExternalFilesDir(null).path ?: return false).plus("/")
        val assetsList: List<String> = getAssetsList(context)

        assetsList.forEach { it ->
            if (!File(appDir, it).exists()) files += it
        }

        files.forEach { it -> copy(context, it) }

        return true
    }

    private fun copy(context: Context, path: String) : File {
        val inStream = context.assets.open(File(path).path)
        val dstFile = File(context.getExternalFilesDir(null), path)

        dstFile.parentFile.mkdir()

        val outStream = FileOutputStream(dstFile)
        val buffer = kotlin.ByteArray(1024)

        do {
            var nRead = inStream.read(buffer)

            if (nRead == 0) {
                nRead = inStream.read()
                if (nRead < 0) break
                outStream.write(nRead)
                continue
            }

            outStream.write(buffer, 0 ,nRead)
        } while (nRead != -1)

        outStream.close()
        return dstFile
    }

    private fun getAssetsList(context: Context): List<String> {
        var files: List<String> = emptyList()
        val listFile = context.assets.open(File(ASSET_LIST_FILENAME).path)
        val bufferReader: BufferedReader = BufferedReader(InputStreamReader(listFile))

        do {
            val path = bufferReader.readLine() ?: break
            files += (path)
        } while (true)

        return files
    }

    /**
     * 判断文件是否已拷贝到应用默认的资源文件夹下
     * @param context
     * @param file
     * **/
    fun isAssetFileExist(context: Context, file: String) : Boolean = File(context.applicationContext.getExternalFilesDir(null).path + "/" + file).exists()
}