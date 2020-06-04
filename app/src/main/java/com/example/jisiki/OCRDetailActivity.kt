package com.example.jisiki

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// OCR을 통해 추출된 문자들을 정리하는 액티비티.
class OCRDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_c_r_detail)
    }
}
