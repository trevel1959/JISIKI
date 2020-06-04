package com.example.jisiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// 검색 결과에서 각 항목을 터치했을 경우 그 성분에 대한 뉴스를 최근 n개 출력하는 액티비티.
class SearchResultDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result_detail)
    }
}
