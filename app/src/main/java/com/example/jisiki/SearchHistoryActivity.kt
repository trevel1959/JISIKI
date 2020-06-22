package com.example.jisiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_search_history.*

//검색했던 제품명/원재료명을 쭉 나열하는 액티비티.
class SearchHistoryActivity : AppCompatActivity() {
    lateinit var adapter :SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_history)
        init()
    }

    fun init(){
        val history = IntroActivity.dbHelper.getAll(DBHelper.TABLE_SEARCH_HISTORY_NAME) as ArrayList<SearchHistory>

        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = SearchHistoryAdapter(history)
        searchHistoryRecyclerView.adapter = adapter
    }
}
