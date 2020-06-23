package com.example.jisiki

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_nutrient_detail.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDateTime
import kotlin.math.roundToInt


//TODO("제품명을 통해 영양정보를 가져오는데, 가져오지 못했을 경우 사용자가 수동 입력할 수 있어야 함.")
class NutrientDetailActivity : AppCompatActivity() {
    var productName =""
    var apiFlag = false
    var responseBody = ""
    lateinit var currentFoodNutr: Nutrient
    lateinit var task:NutrientAsyncTask
    lateinit var adapter: OCRDetailAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrient_detail)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var currentTime = LocalDateTime.now().toString()
        IntroActivity.dbHelper.insert(DBHelper.TABLE_FOOD_EATEN_NAME, productName, currentTime, currentFoodNutr.toString())

        val intent = Intent(applicationContext, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }



    fun init(){
        productName = intent.extras["productName"].toString()
        task = NutrientAsyncTask(this)
        task.execute()
        while(!apiFlag){
        }
        parsingResult(responseBody)

        textView5.text = productName
        nutrients.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = OCRDetailAdapter(currentFoodNutr.toArray(), 1)
        adapter.itemClickListener = object: OCRDetailAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: OCRDetailAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {

                when(holder.editSpace.visibility){
                    View.VISIBLE->
                        holder.editSpace.visibility = View.GONE
                    View.GONE->
                        holder.editSpace.visibility = View.VISIBLE
                }
            }

            override fun OnButtonClick(
                holder: OCRDetailAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                when (position) {
                    0 -> currentFoodNutr.calorie = holder.editText.text.toString().toDouble()
                    1 -> currentFoodNutr.carbohydrate =
                        holder.editText.text.toString().toDouble()
                    2 -> currentFoodNutr.protein = holder.editText.text.toString().toDouble()
                    3 -> currentFoodNutr.fat = holder.editText.text.toString().toDouble()
                    4 -> currentFoodNutr.sugar = holder.editText.text.toString().toDouble()
                    5 -> currentFoodNutr.sodium = holder.editText.text.toString().toDouble()
                    6 -> currentFoodNutr.cholesterol =
                        holder.editText.text.toString().toDouble()
                    7 -> currentFoodNutr.saturated_fat =
                        holder.editText.text.toString().toDouble()
                    8 -> currentFoodNutr.trans_fat = holder.editText.text.toString().toDouble()
                }

                OnItemClick(holder, view, data, position)
            }
        }

        nutrients.adapter = adapter

        button5.setOnClickListener{
            val radioEaten = editTextRadioOfEaten.text.toString().toDouble()
            if(0 < radioEaten && radioEaten <=100){
                currentFoodNutr.calorie = (currentFoodNutr.calorie * radioEaten).roundToInt() / 100.0
                currentFoodNutr.trans_fat = (currentFoodNutr.trans_fat * radioEaten).roundToInt() / 100.0
                currentFoodNutr.saturated_fat = (currentFoodNutr.saturated_fat * radioEaten).roundToInt() / 100.0
                currentFoodNutr.cholesterol = (currentFoodNutr.cholesterol * radioEaten).roundToInt() / 100.0
                currentFoodNutr.sodium = (currentFoodNutr.sodium * radioEaten).roundToInt() / 100.0
                currentFoodNutr.sugar = (currentFoodNutr.sugar * radioEaten).roundToInt() / 100.0
                currentFoodNutr.fat = (currentFoodNutr.fat * radioEaten).roundToInt() / 100.0
                currentFoodNutr.protein = (currentFoodNutr.protein * radioEaten).roundToInt() / 100.0
                currentFoodNutr.carbohydrate = (currentFoodNutr.carbohydrate * radioEaten).roundToInt() / 100.0

                adapter = OCRDetailAdapter(currentFoodNutr.toArray(), 1)
                nutrients.adapter = adapter
            }

            editTextRadioOfEaten.setText("")
        }
    }

    private fun parsingResult(responseBody: String) {
        Log.i("this", responseBody)
        val result = JSONObject(responseBody).getJSONObject("I2790") as JSONObject
        val totalCount = result.getInt("total_count")
        if(totalCount == 0){
            Toast.makeText(this, "해당하는 이름의 제품이 존재하지 않습니다. 직접 입력해주세요.", Toast.LENGTH_SHORT).show()
            var tmp = ArrayList<Double>()
            for(i in 0..8){
                tmp.add(0.0)
            }
            currentFoodNutr = Nutrient(tmp)
        } else{
            //일단 1개라고 가정
            var productNutr = parsingResultNutrient(result, 0)
            currentFoodNutr = productNutr
        }
    }

    fun parsingResultNutrient(result: JSONObject, index:Int):Nutrient{
        var tmp = ArrayList<Double>()
        var itemName = ""

        for(i in 0..8){
            itemName = "NUTR_CONT" + (i+1)
            val itemStr = (result.getJSONArray("row")[index] as JSONObject).getString(itemName)
            if(itemStr.isEmpty())
                tmp.add(0.0)
            else
                tmp.add(itemStr.toDouble())
        }

        return Nutrient(tmp)
    }

    class NutrientAsyncTask(context: NutrientDetailActivity): AsyncTask<URL, Unit, Unit>(){
        val activityreference = WeakReference(context)
        val activity = activityreference.get()

        @ExperimentalStdlibApi
        override fun doInBackground(vararg params: URL?): Unit {
            val productName = URLEncoder.encode(activity?.productName, "UTF-8")
            // 1. 웹 연결

            val NUTRIENT_URL = "http://openapi.foodsafetykorea.go.kr/api/e7a22a65fd7e4154a5d6/I2790/json/1/100/DESC_KOR=${productName}"
            val url = URL(NUTRIENT_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                val streamReader = InputStreamReader(connection.inputStream)
                BufferedReader(streamReader).use { lineReader ->
                    val responseBody = StringBuilder()
                    var line: String? = ""
                    while (lineReader.readLine().also { line = it } != null) {
                        responseBody.append(line)
                    }
                    activity?.responseBody = responseBody.toString()
                    activity?.apiFlag = true
                }
            }
            else{
                activity?.apiFlag = true
            }
            cancel(true)
        }
    }
}