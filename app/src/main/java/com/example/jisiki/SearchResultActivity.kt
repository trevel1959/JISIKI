package com.example.jisiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//검색 결과를 뉴스 글 수로 정렬해 보여주는 액티비티.
class SearchResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
    }
}
