package com.example.jisiki

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_my_page.*
import java.time.LocalDate
import kotlin.math.roundToInt

//오늘 먹은 음식들의 성분비율 등을 보여줌.
//TODO("날짜에 맞는 음식의 데이터를 가져와서 출력해줘야 함.")
class MyPageActivity : AppCompatActivity() {
    var userDailyNutrient: Nutrient? = null
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var dateStr =""
    lateinit var dayNutrient: Nutrient
    lateinit var adapter: MyPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)
        init()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun init(){
        userDailyNutrient = calculateDailyNutrient()

        val date = LocalDate.now()
        year = date.year
        month = date.monthValue
        day = date.dayOfMonth
        setDateStr()

        recyclerViewShow.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        sumDayNutrient(dateStr)
        adapter = MyPageAdapter(dayNutrient.toArray(), this)
        recyclerViewShow.adapter=adapter

        button6.setOnClickListener{
            val listener = DatePickerDialog.OnDateSetListener{ datePicker: DatePicker, i: Int, i1: Int, i2: Int ->
                year = i
                month = i1+1
                day = i2

                setDateStr()
                sumDayNutrient(dateStr)
                adapter = MyPageAdapter(dayNutrient.toArray(), this)
                recyclerViewShow.adapter=adapter
            }
            val dialog = DatePickerDialog(this, listener, year, month-1, day)
            dialog.show()
        }
    }

    fun sumDayNutrient(dateStr: String){
        var tmp = Nutrient()
        val nutrientArr = IntroActivity.dbHelper.getByDate(DBHelper.TABLE_FOOD_EATEN_NAME, dateStr)

        for(item in nutrientArr){
            val tmpArr = ArrayList(item.split(",".toRegex()))
            tmp.calorie += tmpArr[0].toDouble()
            tmp.carbohydrate += tmpArr[1].toDouble()
            tmp.protein += tmpArr[2].toDouble()
            tmp.fat += tmpArr[3].toDouble()
            tmp.sugar += tmpArr[4].toDouble()
            tmp.sodium += tmpArr[5].toDouble()
            tmp.cholesterol += tmpArr[6].toDouble()
            tmp.saturated_fat  += tmpArr[7].toDouble()
            tmp.trans_fat += tmpArr[8].toDouble()
        }

        tmp.calorie = roundSecond(tmp.calorie)
        tmp.carbohydrate = roundSecond(tmp.carbohydrate)
        tmp.protein = roundSecond(tmp.protein)
        tmp.fat = roundSecond(tmp.fat)
        tmp.sugar = roundSecond(tmp.sugar)
        tmp.sodium = roundSecond(tmp.sodium)
        tmp.cholesterol = roundSecond(tmp.cholesterol)
        tmp.saturated_fat = roundSecond(tmp.saturated_fat)
        tmp.trans_fat = roundSecond(tmp.trans_fat)

        dayNutrient = tmp
    }

    fun roundSecond(value:Double):Double{
        return (value*100.0).roundToInt() / 100.0
    }

    fun setDateStr(){
        dateStr = String.format("%04d", year)+ "-" +
                String.format("%02d", month)+"-" +
                String.format("%02d", day)
        textView7.text = dateStr
    }

    fun calculateDailyNutrient():Nutrient{
        val userData = IntroActivity.dbHelper.getAll(DBHelper.TABLE_USER_DATA_NAME)
        var userAge = userData[0].toInt()
        var userGender = userData[1]
        var userHeight = userData[2].toDouble()
        var userWeight = userData[3].toDouble()
        var userActive = userData[4].toInt()

        var userActiveCoefficient = arrayOf(1.0, 1.11, 1.25, 1.48, 1.0, 1.12, 1.27, 1.45)
        var totalCalorie = when(userGender){
            "man" -> 662 - 9.53*userAge + userActiveCoefficient[userActive]*(15.91*userWeight + 5.396*userHeight)
            "woman" -> 354 - 6.91*userAge + userActiveCoefficient[4+userActive]*(9.36*userWeight + 7.26*userHeight)
            else -> 0.0
        }

        val nut1 = (totalCalorie * 0.65)/4
        val nut2 = (totalCalorie * 0.15)/4
        val nut3 = (totalCalorie * 0.20)/9
        val nut4 = (totalCalorie * 0.10)/4
        val nut5 = 2000.0
        val nut6 = 300.0
        val nut7 = (totalCalorie * 0.05)/9
        val nut8 = (totalCalorie * 0.01)/9

        return Nutrient(totalCalorie, nut1, nut2, nut3, nut4, nut5, nut6, nut7, nut8)
    }
}
