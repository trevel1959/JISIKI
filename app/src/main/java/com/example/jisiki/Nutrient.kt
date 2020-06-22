package com.example.jisiki

import java.io.Serializable

class Nutrient(
    var calorie: Double,
    var carbohydrate: Double,
    var protein: Double,
    var fat: Double,
    var sugar: Double,
    var sodium: Double,
    var cholesterol: Double,
    var saturated_fat: Double,
    var trans_fat: Double)
    :Serializable{

    override fun toString():String{
        return calorie.toString()+","+carbohydrate.toString()+","+protein.toString()+","+fat.toString()+","+sugar.toString()+","+sodium.toString()+","+cholesterol.toString()+","+saturated_fat.toString()+","+trans_fat.toString()
    }
}