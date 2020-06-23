package com.example.jisiki

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyPageAdapter (val items:ArrayList<String>, val parent: MyPageActivity)
    : RecyclerView.Adapter <MyPageAdapter.MyViewHolder>() {
    val nutrientName = arrayOf("총열량(kcal)", "탄수화물(g)", "단백질(g)", "지방(g)", "당류(g)", "나트륨(mg)", "콜레스테롤(mg)", "포화지방(g)", "트랜스지방(g)")

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView: TextView = itemView.findViewById(R.id.textViewShow)
        var textViewRatio:TextView = itemView.findViewById(R.id.textViewShow2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.show, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userNutr = parent.userDailyNutrient?.toArray()
        holder.textView.text = nutrientName[position] + " : " + items[position]
        //holder.textViewRatio.text = userNutr!![position]
        holder.textViewRatio.text = String.format("%.02f", items[position].toDouble() * 100 / userNutr!!.get(position).toDouble()) + "%"
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