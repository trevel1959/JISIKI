package com.example.jisiki

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(val context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    //검색 결과의 DB 테이블은 3개.
    //첫번째는 검색 시간을 가지고 있는 테이블
    //두번째는 검색 시간을 KEY로, 검색 단어를 가지고 있는 테이블.

    companion object {
        val DB_VERSION = 1
        val DB_NAME = "mydb.db"

        val TABLE_SEARCH_HISTORY_NAME = "searchHistoryTable"
        val INDEX = "index"
        val PNAME = "productName"
        val SEARCH_TIME = "searchTime"
        val SEARCH_IMAGE = "searchImage"

        val TABLE_SEARCH_WORD_NAME = "searchResultTable"
        //SEARCH_TIME 공유
        val INGREDIENT_NAME = "ingredientName"

        val TABLE_FOOD_EATEN_NAME = "foodEatenTable"
        //INGREDIENT_NAME 공유
        //SEARCH_TIME 공유
        val FOOD_QUANTITY = "foodQuantity"

        val TABLE_USER_DATA_NAME = "userDataTable"
        val USER_AGE = "userAge"
        val USER_GENDER = "userGender"
        val USER_ACTIVE = "userActive"

        val TMP_IMAGE = "tmpImageTable"
        val IMG = "img"
    }

    fun setTmpImage(img: String){
        val drop_table = "drop table if exists " + TMP_IMAGE
        var db = this.writableDatabase
        db?.execSQL(drop_table)

        var create_table = "create table " + TMP_IMAGE + "(" +
                IMG + " text)"
        db?.execSQL(create_table)

        val values = ContentValues()
        values.put(IMG, img)
        db = this.writableDatabase
        db.insert(TMP_IMAGE, null, values)
        db.close()
    }

    fun getTmpImage():String{
        val strsql = "select * from " + TMP_IMAGE
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)
        cursor.moveToFirst()
        val str = cursor.getString(0)
        cursor.close()
        db.close()
        return str
    }

    override fun onCreate(db: SQLiteDatabase?) {
        var create_table = "create table if not exists " + TABLE_SEARCH_HISTORY_NAME + "(" +
                PNAME + " text, " +
                SEARCH_TIME + " text, " +
                SEARCH_IMAGE + " text)"
        db?.execSQL(create_table)

        create_table = "create table if not exists " + TABLE_SEARCH_WORD_NAME + "(" +
                SEARCH_TIME + " text, " +
                INGREDIENT_NAME + " text)"
        db?.execSQL(create_table)

        create_table = "create table if not exists " + TABLE_FOOD_EATEN_NAME + "(" +
                INGREDIENT_NAME + " text, " +
                SEARCH_TIME + " text, " +
                FOOD_QUANTITY + " integer)"
        db?.execSQL(create_table)

        create_table = "create table if not exists " + TABLE_USER_DATA_NAME + "(" +
                USER_AGE + " integer, "+
                USER_GENDER + "text, "+
                USER_ACTIVE + " integer)"
        db?.execSQL(create_table)

        create_table = "create table if not exists " + TMP_IMAGE + "(" +
                IMG + " text)"
        db?.execSQL(create_table)

        Log.e("real", "why")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        var drop_table = "drop table if exists $TABLE_SEARCH_HISTORY_NAME"
        p0?.execSQL(drop_table)
        drop_table = "drop table if exists $TABLE_SEARCH_WORD_NAME"
        p0?.execSQL(drop_table)
        drop_table = "drop table if exists $TABLE_FOOD_EATEN_NAME"
        p0?.execSQL(drop_table)
        drop_table = "drop table if exists $TABLE_USER_DATA_NAME"
        p0?.execSQL(drop_table)
        drop_table = "drop table if exists $TMP_IMAGE"
        p0?.execSQL(drop_table)

        onCreate(p0)
    }

    fun insert(tableName:String, data1:String, data2:String, data3:String) {
        val values = ContentValues()

        when(tableName){
            TABLE_SEARCH_HISTORY_NAME->{
                values.put(PNAME, data1)
                values.put(SEARCH_TIME, data2)
                values.put(SEARCH_IMAGE, data3)

                val db = this.writableDatabase
                db.insert(TABLE_SEARCH_HISTORY_NAME, null, values)
                db.close()
            }

            TABLE_SEARCH_WORD_NAME->{
                values.put(SEARCH_TIME, data1)
                values.put(INGREDIENT_NAME, data2)

                val db = this.writableDatabase
                db.insert(TABLE_SEARCH_WORD_NAME, null, values)
                db.close()
            }

            TABLE_FOOD_EATEN_NAME->{
                values.put(INGREDIENT_NAME, data1)
                values.put(SEARCH_TIME, data2)
                values.put(FOOD_QUANTITY, data3.toInt())

                val db = this.writableDatabase
                db.insert(TABLE_SEARCH_HISTORY_NAME, null, values)
                db.close()
            }
            TABLE_USER_DATA_NAME->{
                values.put(USER_AGE, data1.toInt())
                values.put(USER_GENDER, data2)
                values.put(USER_ACTIVE, data3.toInt())

                val db = this.writableDatabase
                db.insert(TABLE_SEARCH_HISTORY_NAME, null, values)
                db.close()
            }
        }
    }

    fun getAll(tableName: String):ArrayList<String>{
        val strsql = "select * from $tableName"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)

        if(cursor.count != 0){
            cursor.moveToFirst()
            when(tableName){
                TABLE_SEARCH_HISTORY_NAME->{
                    var list = ArrayList<SearchHistory>()
                    while(cursor.moveToNext()){
                        val pname = cursor.getString(0)
                        val searchTime = cursor.getString(1)
                        val searchImage = cursor.getString(2)
                        list.add(SearchHistory(pname, searchTime, searchImage))
                    }
                    return (list as ArrayList<String>)
                }

            }
        }
        cursor.close()
        db.close()
        return ArrayList<String>()
    }

    fun getByDate(tableName: String, searchTime:String):ArrayList<String>{
        val strsql = "select * from $tableName" + " where " + SEARCH_TIME + " = \'" + searchTime + "\'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(strsql, null)

        if(cursor.count != 0){
            cursor.moveToFirst()

            when(tableName){
                TABLE_SEARCH_WORD_NAME->{
                    var list = ArrayList<String>()
                    while(cursor.moveToNext()){
                        list.add(cursor.getString(1))
                    }
                    return list
                }

            }
        }
        cursor.close()
        db.close()
        return ArrayList<String>()
    }

    fun delete(data: SearchData){

    }

    fun find(data: String){

    }

    fun getAll(){

    }
}