package com.bennyv17.river.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Environment.getExternalStorageDirectory
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import android.util.TypedValue
import com.bennyv17.river.R
import com.github.johnpersano.supertoasts.library.Style
import com.github.johnpersano.supertoasts.library.SuperActivityToast
import java.io.*
import java.nio.charset.Charset


object Tool {

    val UTF8 = Charset.forName("UTF-8")
    val BUFFER_SIZE = 8192

    fun toast(context: Context, msg: String) {
        SuperActivityToast.cancelAllSuperToasts()
        SuperActivityToast.create(context, Style.lime())
                .setColor(context.resources.getColor(R.color.primary))
                .setTextColor(Color.WHITE)
                .setText(msg)
                .setDuration(500)
                .setFrame(Style.FRAME_STANDARD)
                .setAnimations(Style.ANIMATIONS_SCALE)
                .show()
    }

    @Throws(Exception::class)
    private fun convertStreamToString(stream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(stream, UTF8), BUFFER_SIZE)
        val result = reader.readText()
        reader.close()
        return result
    }

    fun getProject(file: File): String {
        return try {
            val fin = FileInputStream(file)
            val ret = convertStreamToString(fin)
            fin.close()
            ret
        } catch (e: Exception) {
            ""
        }
    }

    fun saveScript(name: String, data: String): File? {
        val dirs = File(getExternalStorageDirectory().path + ("/River/Project"))
        if (!dirs.exists()) {
            dirs.mkdirs()
        }

        try {
            val file = File(dirs, name + ".rive")
            val outputStreamWriter = BufferedWriter(OutputStreamWriter(FileOutputStream(file),UTF8), BUFFER_SIZE)
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return File(dirs, name + ".rive")
    }

    fun stripExtension(str: String): String {
        val pos = str.lastIndexOf(".")
        return if (pos == -1) str else str.substring(0, pos)
    }

    fun dp2px(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    fun px2dp(px: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val dp = px / (metrics.densityDpi / 160f)
        return Math.round(dp).toFloat()
    }

    @ColorInt
    fun getThemeColor(context: Context,
                      @AttrRes attributeColor: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attributeColor, value, true)
        return value.data
    }

    fun listDirectory(path: String, extension: String): ArrayList<File> {
        val parentFolder = File(path)

        val result = arrayListOf<File>()

        val files = parentFolder.listFiles(FilenameFilter { _, name ->

            if (name.endsWith(extension))
                return@FilenameFilter true

            false
        })
        if (files != null)
            result += files

        return result
    }

}