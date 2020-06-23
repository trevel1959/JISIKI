package com.example.jisiki

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_intro.*

//첫 화면.
class IntroActivity : AppCompatActivity() {
    companion object{
        lateinit var dbHelper: DBHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        init()
    }

    private fun init() {
        val dbfile = this.getDatabasePath("mydb.db")
        if(!dbfile.parentFile.exists()){
            dbfile.parentFile.mkdir()
        }
        if(!dbfile.exists()){
            dbfile.createNewFile()
        }
        dbHelper = DBHelper(this)

        imageButton6.setOnClickListener{
            val intent = Intent(this, OCRActivity::class.java)
            startActivity(intent)
        }
        imageButton7.setOnClickListener{
            val intent = Intent(this, SearchHistoryActivity::class.java)
            startActivity(intent)
        }
        imageButton8.setOnClickListener{
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
        imageButton9.setOnClickListener{
            val intent = Intent(this, MyPageSettingActivity::class.java)
            startActivity(intent)
        }
    }
}
