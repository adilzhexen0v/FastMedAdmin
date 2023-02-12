package com.example.trikotaprojectadmin

import android.os.AsyncTask
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Suppress("DEPRECATION")
class RetrievePdfFromURL(pdfView: PDFView) :
    AsyncTask<String, Void, InputStream>() {

    val mypdfView: PDFView = pdfView

    override fun doInBackground(vararg params: String?): InputStream? {
        var inputStream: InputStream? = null
        try {
            val url = URL(params.get(0))
            val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
            if (urlConnection.responseCode == 200) {
                inputStream = BufferedInputStream(urlConnection.inputStream)
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            return null;
        }
        return inputStream;
    }

    override fun onPostExecute(result: InputStream?) {
        mypdfView.fromStream(result).load()
    }
}