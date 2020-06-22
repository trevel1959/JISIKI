package com.example.jisiki

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.jisiki.IntroActivity.Companion.dbHelper
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//사진을 찍어 OCR을 통해 원재료명을 추출하는 액티비티.
class OCRActivity : AppCompatActivity() {
    var imgbitmap: Bitmap? = null
    var imgbase64 : String = ""
    var readWords = ArrayList<String>()
    var mCurrentPhotoPath:String? = null
    val REQUEST_TAKE_PHOTO = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        captureBtn.setOnClickListener{
            takePicture()
        }
        searchBtn.setOnClickListener{
            if(imgbase64 != ""){
                val task = OCRAsyncTask(this)
                task.execute(null)
                //finish()
            }
            else
                Toast.makeText(this, "사진을 찍어주세요.", Toast.LENGTH_SHORT).show()
        }

        takePicture()
    }

    fun takePicture(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {

            }

            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.example.jisiki.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)

                if (resultCode == Activity.RESULT_OK) {
                    imageView.setImageURI(result.uri)

                    val imageUri = result.uri
                    val imageStream = contentResolver.openInputStream(imageUri)
                    imgbitmap = BitmapFactory.decodeStream(imageStream)

                    val baos = ByteArrayOutputStream()
                    imgbitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val b = baos.toByteArray()
                    imgbase64 = Base64.encodeToString(b, Base64.DEFAULT)

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "이미지 편집에 실패하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_TAKE_PHOTO -> {
                if (resultCode == Activity.RESULT_OK) {
                    val file = File(mCurrentPhotoPath)
                    imgbitmap =
                        MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    if (imgbitmap != null) {
                        imageView.setImageBitmap(imgbitmap)
                        CropImage.activity(getImageUri(this, imgbitmap!!))
                            .start(this)
                    }
                }
            }
        }
    }

    private fun getImageUri(context: Context, inImage: Bitmap):Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path:String = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path);
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath()
        return image
    }

    class OCRAsyncTask(context: OCRActivity): AsyncTask<URL, Unit, Unit>(){
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

    @ExperimentalStdlibApi
    fun parseJSON(input : String){
        val inputLine = String(input.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
        val fields = (JSONObject(inputLine).getJSONArray("images").get(0) as JSONObject).getJSONArray("fields")

        for(i in 0 .. fields.length() - 1){
            val inferText = (fields.get(i) as JSONObject).getString("inferText")
            readWords.add(inferText)
        }

        if(readWords.isNotEmpty()){
            val intent = Intent(this, OCRDetailActivity::class.java)
            intent.putExtra("readWords", readWords)
            dbHelper.setTmpImage(imgbase64)
            startActivity(intent)
            readWords.clear()
            //imageView.setImageResource(R.drawable.pressButton)
        }
    }
}