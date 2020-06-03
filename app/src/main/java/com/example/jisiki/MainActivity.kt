package com.example.jisiki

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.OutputStreamWriter
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    var imgbitmap: Bitmap? = null
    var imgbase64 : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        button.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null)
                startActivityForResult(intent, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == Activity.RESULT_OK){
            val extras = data?.extras
            imgbitmap = extras?.get("data") as Bitmap
            imageView.setImageBitmap(imgbitmap)

            imgbase64 = bitmap_to_base64(imgbitmap!!)
            OCR()
        }
    }

    fun bitmap_to_base64(bitmap: Bitmap) : String{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        val encodeImage = Base64.encodeToString(b, Base64.DEFAULT)

        return encodeImage
    }

    fun OCR(){
        val task = MyAsyncTask(this)
        task.execute(null)
    }

    @ExperimentalStdlibApi
    fun parseJSON(input : String){
        val inputLine = String(input.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        val result = JSONObject(inputLine)
        val images = result.getJSONArray("images").get(0) as JSONObject
        val fields = images.getJSONArray("fields")

        for(i in 0 .. fields.length() - 1){
            val inferText = (fields.get(i) as JSONObject).getString("inferText")
            if(i == 0) textView.text = inferText
            else textView.text = textView.text.toString() + ", " + inferText
        }
    }

    class MyAsyncTask(context: MainActivity): AsyncTask<URL, Unit, Unit>(){
        val OCR_URL = "https://93025475713c408bbcc53d6030ded442.apigw.ntruss.com/custom/v1/2075/7513eeb77a8226d36d946d3d1df41df47d4337923b2cc98e55ad0b5ab46ce285/general"
        val OCR_KEY = "Vm1SdHZzU3pleXRGS3FBeGRrR0dBWUpDVndOZnpYWWM="
        val activityreference = WeakReference(context)
        val activity = activityreference.get()

        @ExperimentalStdlibApi
        override fun doInBackground(vararg params: URL?): Unit {
            // 1. 웹 연결
            val url = URL(OCR_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doInput = true
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("X-OCR-SECRET", OCR_KEY)
            connection.connect()

            // 2. JSON 객체 생성
            var imageData = JSONObject()
            imageData.put("format", "jpg")
            imageData.put("name", "sample")
            imageData.put("data", activity?.imgbase64)

            var image = JSONArray()
            image.put(imageData)

            var data = JSONObject()
            data.put("version", "V2")
            data.put("requestId", "sample")
            data.put("timestamp", 0)
            data.put("lang", "ko")
            data.put("images", image)

            // 3. JSON 값 전송
            var output = OutputStreamWriter(connection.outputStream)
            output.write(data.toString())
            output.flush()
            output.close()

            var input = DataInputStream(connection.inputStream)
            activity?.parseJSON(input.readLine())
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
    }
}