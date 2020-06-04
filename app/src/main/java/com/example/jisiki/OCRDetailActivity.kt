package com.example.jisiki

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_o_c_r_detail.*

// OCR을 통해 추출된 문자들을 정리하는 액티비티.
class OCRDetailActivity : AppCompatActivity() {
    lateinit var adapter: OCRDetailAdapter
    var readWords = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_c_r_detail)
        init()
    }
        
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var intent = Intent(this, SearchResultActivity::class.java)
        intent.putExtra("readWords", readWords)
        startActivity(intent)

        return super.onOptionsItemSelected(item)

    }

    private fun init() {
        readWords = intent.extras["readWords"] as ArrayList<String>

        recyclerViewACD.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = OCRDetailAdapter(readWords)

        recyclerViewACD.adapter = adapter
    }
}
