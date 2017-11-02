package com.bennyv17.river.util

import android.content.Context
import android.content.res.Resources
import android.os.Environment.getExternalStorageDirectory
import android.text.Html
import android.util.Log
import java.io.*
import android.widget.Toast
import android.R.attr.data
import android.util.TypedValue
import android.support.annotation.AttrRes
import android.support.annotation.NonNull
import android.support.annotation.ColorInt


object Tool {

    fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    @Throws(Exception::class)
    private fun convertStreamToString(stream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(stream))
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
            val outputStreamWriter = OutputStreamWriter(FileOutputStream(file))
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

        result += parentFolder.listFiles(FilenameFilter { _, name ->

            if (name.endsWith(extension))
                return@FilenameFilter true

            false
        })

        return result
    }

}