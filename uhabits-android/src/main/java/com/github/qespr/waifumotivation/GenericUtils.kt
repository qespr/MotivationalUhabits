package com.github.qespr.waifumotivation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**Contains all of the image related functions and methods so it's easier to pull updates in future*/
object GenericUtils {

    const val RESULT_SELECT_WAIFU = 1000
    const val RESULT_UNSELECT_WAIFU = 1001
    const val REQUEST_IMAGE = 1002

    const val WAIFU_FILENAME :String = "waifu.png"

    fun loadWaifuBitmap(context: Context) : Bitmap? {

        val f = File(context.filesDir, WAIFU_FILENAME)

        if (f.exists()) {
            return  BitmapFactory.decodeFile(f.absolutePath)
        }
        return null
    }

    fun loadWaifuDrawable(context: Context) :Drawable? {
        val f = File(context.filesDir, WAIFU_FILENAME)

        if (f.exists()) {
            return BitmapDrawable(context.resources, BitmapFactory.decodeFile(f.absolutePath))
        }
        return null
    }

    fun startImageSelection(activity: Activity) {

        val getIntent = Intent(Intent.ACTION_GET_CONTENT)
        getIntent.type = "image/*"

        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        val chooser = Intent.createChooser(getIntent, "Select Image")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

        startActivityForResult(activity, chooser, REQUEST_IMAGE, null)
    }

    fun onImageSelect(activity :Activity, resultCode: Int, data: Intent?) {

        //From: http://burnignorance.com/android-apps-development/writing-bitmap-to-an-image-file-in-internal-storage/
        //However some bits look really weird
        if (resultCode == Activity.RESULT_OK) {
            //Getting weird error when using BitmapFactory.decodeFile(data.data.path) so using deprecated MediaStore for now
            val bmap: Bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data!!.data)

            val baos = ByteArrayOutputStream(1024)
            bmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val buffer :ByteArray = baos.toByteArray()

            try {
                baos.flush()
                baos.close()

                val fos : FileOutputStream = activity.openFileOutput("waifu.png", Context.MODE_PRIVATE)
                fos.write(buffer)
                fos.flush()
                fos.close()

            } catch (ioe : IOException) {
                Log.e("Motivation","Error while making motivation image copy $ioe")
            }
        }
    }

    fun deleteMotivationImage(context :Context) {
        //Todo: Maybe we should ask for confirmation
        //Todo: Add the "waifu.png" filename to some constant
        context.deleteFile("waifu.png")
    }

}