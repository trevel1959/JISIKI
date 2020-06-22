package com.example.jisiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//TODO("제품명을 통해 영양정보를 가져오는데, 가져오지 못했을 경우 사용자가 수동 입력할 수 있어야 함.")
class NutrientDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrient_detail)
    }
}