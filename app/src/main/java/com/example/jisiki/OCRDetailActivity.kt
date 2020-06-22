package com.example.jisiki

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jisiki.IntroActivity.Companion.dbHelper
import kotlinx.android.synthetic.main.activity_o_c_r_detail.*
import java.time.LocalDateTime

// OCR을 통해 추출된 문자들을 정리하는 액티비티.
class OCRDetailActivity : AppCompatActivity() {
    lateinit var adapter: OCRDetailAdapter
    private var readWords = ArrayList<String>()
    var imgbase64:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_c_r_detail)
        init()
    }
        
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_btn1) {
            var flag = false
            var str = ""
            var ad = AlertDialog.Builder(this)
            ad.setTitle("제품명 입력")
            ad.setMessage("제품명을 입력해주세요.")

            val editText = EditText(this)
            ad.setView(editText)

            ad.setPositiveButton("입력") { dialogInterface: DialogInterface, i: Int ->
                str = editText.text.toString()
                flag = true
                dialogInterface.dismiss()
            }
            ad.setNegativeButton("닫기") { dialogInterface: DialogInterface, i: Int ->
                flag = true
                dialogInterface.dismiss()
            }

            ad.setOnDismissListener {
                if (str != "") {
                    var currentTime = LocalDateTime.now().toString()
                    dbHelper.insert(DBHelper.TABLE_SEARCH_HISTORY_NAME, str, currentTime, imgbase64)
                    Log.i("addSearchHistory", str)

                    for(value in readWords){
                        dbHelper.insert(DBHelper.TABLE_SEARCH_WORD_NAME, currentTime, value, "")
                        Log.i("addSearchWord", value)
                    }

                    var intent = Intent(this, SearchResultActivity::class.java)
                    intent.putExtra("productName", str)
                    intent.putExtra("readWords", readWords)
                    startActivity(intent)
                    readWords.clear()
                    finish()
                } else {
                    Toast.makeText(this, "제품명을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
            ad.show()
        }
        else{
            readWords.add("")
            adapter.notifyDataSetChanged()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        imgbase64 = dbHelper.getTmpImage()
        val decodedString = Base64.decode(imgbase64, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        imageView3.setImageBitmap(decodedByte)

        readWords = intent.extras["readWords"] as ArrayList<String>

        var totalString = ""
        while(readWords.isNotEmpty()){
            totalString += readWords[0]
            readWords.removeAt(0)
        }
        readWords = ArrayList(totalString.split(",".toRegex()))

        recyclerViewACD.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = OCRDetailAdapter(readWords)

        adapter.itemClickListener = object: OCRDetailAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: OCRDetailAdapter.MyViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                holder.editText.setText(holder.textView.text)

                when(holder.editSpace.visibility){
                    View.VISIBLE->
                        holder.editSpace.visibility = View.GONE
                    View.GONE->
                        holder.editSpace.visibility = View.VISIBLE
                }
            }
        }

        recyclerViewACD.adapter = adapter

        val simpleCallback = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN or ItemTouchHelper.UP, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Toast.makeText(this@OCRDetailActivity, "Swiped", Toast.LENGTH_SHORT).show()
                adapter.removeItem(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerViewACD)
    }
}
