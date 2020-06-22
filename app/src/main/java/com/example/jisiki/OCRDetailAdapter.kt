package com.example.jisiki

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OCRDetailAdapter (val items:ArrayList<String>)
    : RecyclerView.Adapter <OCRDetailAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(holder: MyViewHolder, view: View, data:String, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView: TextView = itemView.findViewById(R.id.textView3)
        var editSpace:LinearLayout = itemView.findViewById(R.id.editSpace)
        var editText: EditText = itemView.findViewById(R.id.editTextWords)
        var editbtn:Button = itemView.findViewById(R.id.editButton)

        init{

            textView.setOnClickListener{
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }

            editbtn.setOnClickListener{
                items[adapterPosition] = editText.text.toString()
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ocr_detail_row, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = items[position]
    }

    fun moveItem(oldPos:Int, newPos:Int){
        val item = items.get(oldPos)
        items.removeAt(oldPos)
        items.add(newPos, item)
        notifyItemMoved(oldPos, newPos)
    }

    fun removeItem(pos:Int){
        items.removeAt(pos)
        notifyItemRemoved(pos)
    }
}