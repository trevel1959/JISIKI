package com.example.jisiki

import org.json.JSONObject
import java.io.Serializable

data class SearchData(val word:String, val result : JSONObject) :Serializable{
}