package com.example.jisiki

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_my_page_setting.*

// 사용자의 성별, 나이 등의 정보들을 설정함.
class MyPageSettingActivity : AppCompatActivity() {
    var userAge = 0
    var userGender = ""
    var userHeight = 0.0
    var userWeight = 0.0
    var userActive = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page_setting)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        userAge = editTextUserAge.text.toString().toInt()
        userHeight = editTextUserHeight.text.toString().toDouble()
        userWeight = editTextUserWeight.text.toString().toDouble()
        IntroActivity.dbHelper.insertUserData(userAge, userGender, userHeight, userWeight, userActive)
        Toast.makeText(this, "사용자 정보가 등록되었습니다.", Toast.LENGTH_SHORT).show()
        Log.i("userInfo", userAge.toString() + userGender + userActive.toString())
        finish()

        return true
    }
    fun init(){
        var userData = IntroActivity.dbHelper.getAll(DBHelper.TABLE_USER_DATA_NAME)

        if(userData.isNotEmpty()) {
            editTextUserAge.setText(userData[0])
            if (userData[1] == "man"){
                radioGroup_gender.check(R.id.radioButton_man)
                userGender = "man"
            }
            else {
                radioGroup_gender.check(R.id.radioButton_woman)
                userGender = "woman"
            }

            editTextUserHeight.setText(userData[2])
            editTextUserWeight.setText(userData[3])

            userActive = userData[4].toInt()
            when(userData[4].toInt()){
                0 -> radioGroup_active.check(R.id.radioButton_active0)
                1 -> radioGroup_active.check(R.id.radioButton_active1)
                2 -> radioGroup_active.check(R.id.radioButton_active2)
                3 -> radioGroup_active.check(R.id.radioButton_active3)
            }
        }

        radioGroup_gender.setOnCheckedChangeListener{ _, checkedId ->
            when(checkedId){
                R.id.radioButton_man -> userGender = "man"
                R.id.radioButton_woman -> userGender = "woman"
            }
        }

        radioGroup_active.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.radioButton_active0 -> userActive = 0
                R.id.radioButton_active1 -> userActive = 1
                R.id.radioButton_active2 -> userActive = 2
                R.id.radioButton_active3 -> userActive = 3
            }
        }
    }
}
