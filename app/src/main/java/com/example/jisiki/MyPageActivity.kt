package com.example.jisiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate

//오늘 먹은 음식들의 성분비율 등을 보여줌.
//TODO("날짜에 맞는 음식의 데이터를 가져와서 출력해줘야 함.")
class MyPageActivity : AppCompatActivity() {
    var userDailyNutrient: Nutrient? = null
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)
        init()
    }

    fun init(){
        calculateDailyNutrient()

        val date = LocalDate.now()
        year = date.year
        month = date.monthValue
        day = date.dayOfMonth
        //그 날짜에 맞는 데이터를 긁어옴

        //버튼 리스너
        //날짜를 DatePickerDialog로 받아옴
        //그 날짜에 맞는 데이터를 긁어옴
    }

    fun calculateDailyNutrient(){
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
        val nut5 = 2.0
        val nut6 = 0.3
        val nut7 = (totalCalorie * 0.05)/9
        val nut8 = (totalCalorie * 0.01)/9

        userDailyNutrient = Nutrient(totalCalorie, nut1, nut2, nut3, nut4, nut5, nut6, nut7, nut8)
    }
}
