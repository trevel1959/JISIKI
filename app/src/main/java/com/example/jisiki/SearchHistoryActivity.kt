package com.example.jisiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//검색했던 제품명/원재료명을 쭉 나열하는 액티비티.
class SearchHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_history)
    }
}
