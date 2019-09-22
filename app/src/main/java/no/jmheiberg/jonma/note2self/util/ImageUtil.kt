package no.jmheiberg.jonma.note2self.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageUtil(private val appCompatActivity: AppCompatActivity) {

    lateinit var currentPhotoPath: String
//https://stackoverflow.com/questions/33991908/camera-does-not-set-image-on-imageview
    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = appCompatActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            timeStamp,
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

//https://android.jlelse.eu/loading-large-bitmaps-efficiently-in-android-66826cd4ad53
    fun decodeSampledBitmapFromFile(path: String?, reqwidth: Int, reqheight: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)

        val height = options.outHeight
        val width = options.outWidth
        options.inPreferredConfig = Bitmap.Config.RGB_565
        var inSampleSize = 1

        if (height > reqheight) inSampleSize = Math.round(height.toFloat() / reqheight.toFloat())

        val expectedWidth = width / inSampleSize

        if (expectedWidth > reqwidth) inSampleSize = Math.round(width.toFloat() / reqwidth.toFloat())

        options.inSampleSize = inSampleSize
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, options)
    }

}