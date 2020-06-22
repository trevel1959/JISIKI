package com.example.jisiki

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchHistoryAdapter (val items:ArrayList<SearchHistory>)
    : RecyclerView.Adapter <SearchHistoryAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(holder: MyViewHolder, view: View, data:SearchHistory, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val context = itemView.context
        val imageView:ImageView = itemView.findViewById(R.id.searchHistoryImageView)
        val textView: TextView = itemView.findViewById(R.id.searchHistoryWordView)
        val timeView: TextView = itemView.findViewById(R.id.searchHistoryTimeView)

        init{
            textView.setOnClickListener{
                val readWords = IntroActivity.dbHelper.getByDate(DBHelper.TABLE_SEARCH_WORD_NAME, items[position].searchTime)
                var intent = Intent(context, SearchResultActivity::class.java)
                intent.putExtra("productName", items[position].productName)
                intent.putExtra("readWords", readWords)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_history_row, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var imgbase64 = items[position].searchImage
        val decodedString = Base64.decode(imgbase64, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        holder.imageView.setImageBitmap(decodedByte)
        holder.textView.text = items[position].productName
        holder.timeView.text = items[position].searchTime
    }

    fun removeItem(pos:Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }
}