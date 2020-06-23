package com.example.jisiki

import java.io.Serializable

class Nutrient():Serializable{
    var calorie = 0.0
    var carbohydrate = 0.0
    var protein = 0.0
    var fat = 0.0
    var sugar = 0.0
    var sodium = 0.0
    var cholesterol = 0.0
    var saturated_fat = 0.0
    var trans_fat = 0.0

    constructor(calorie:Double, carbohydrate:Double, protein:Double, fat:Double, sugar:Double, sodium:Double, cholesterol:Double, saturated_fat:Double, trans_fat:Double): this(){
        this.calorie = calorie
        this.carbohydrate = carbohydrate
        this.protein = protein
        this.fat = fat
        this.sugar = sugar
        this.sodium = sodium
        this.cholesterol = cholesterol
        this.saturated_fat = saturated_fat
        this.trans_fat = trans_fat
    }

    constructor(data:ArrayList<Double>):this(){
        this.calorie = data[0]
        this.carbohydrate = data[1]
        this.protein = data[2]
        this.fat = data[3]
        this.sugar = data[4]
        this.sodium = data[5]
        this.cholesterol = data[6]
        this.saturated_fat = data[7]
        this.trans_fat = data[8]
    }

    override fun toString():String{
        return calorie.toString()+","+carbohydrate.toString()+","+protein.toString()+","+fat.toString()+","+sugar.toString()+","+sodium.toString()+","+cholesterol.toString()+","+saturated_fat.toString()+","+trans_fat.toString()
    }

    fun toArray():ArrayList<String>{
        var tmp = ArrayList<String>()
        tmp.add(calorie.toString())
        tmp.add(carbohydrate.toString())
        tmp.add(protein.toString())
        tmp.add(fat.toString())
        tmp.add(sugar.toString())
        tmp.add(sodium.toString())
        tmp.add(cholesterol.toString())
        tmp.add(saturated_fat.toString())
        tmp.add(trans_fat.toString())

        return tmp
    }
}
