package com.amary21.trinihub.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import java.net.HttpURLConnection
import java.net.URL

class DownloadImageTask(private val bmImage: ImageView?) :
    AsyncTask<String?, Void?, Bitmap?>() {


    override fun doInBackground(vararg params: String?): Bitmap? {
        return downloadBitmap(params[0])
    }

    private fun downloadBitmap(url: String?): Bitmap? {
        var urlConnection: HttpURLConnection? = null
        try {
            val uri = URL(url)
            urlConnection = uri.openConnection() as HttpURLConnection?
            val statusCode = urlConnection?.responseCode
            if (statusCode != HttpURLConnection.HTTP_OK){
                return null
            }

            val inputStream = urlConnection?.inputStream
            if (inputStream != null){
                return BitmapFactory.decodeStream(inputStream)
            }
        }catch (e : Exception){
            urlConnection?.disconnect()
        }finally {
            urlConnection?.disconnect()
        }
        return null
    }


    override fun onPostExecute(bitmap: Bitmap?) {
        var mBitmap = bitmap
        if (isCancelled){
            mBitmap = null
        }

        if (bmImage != null){
            if (mBitmap != null){
                bmImage.setImageBitmap(mBitmap)
            }
        }
    }
}