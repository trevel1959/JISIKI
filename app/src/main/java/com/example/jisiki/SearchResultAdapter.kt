package com.example.jisiki

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject

class SearchResultAdapter(val items:ArrayList<SearchData>)
    : RecyclerView.Adapter <SearchResultAdapter.MyViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(holder: MyViewHolder, view: View, data:SearchData, position: Int)
    }

    var itemClickListener:OnItemClickListener?=null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var textView: TextView = itemView.findViewById(R.id.searchResultTextView)
        var head1:TextView = itemView.findViewById(R.id.newsHeadTextView1)
        var head2:TextView = itemView.findViewById(R.id.newsHeadTextView2)
        var head3:TextView = itemView.findViewById(R.id.newsHeadTextView3)
        var newsHead:LinearLayout = itemView.findViewById(R.id.newsDetail)

        init{
            val context = itemView.context

            textView.setOnClickListener() {
                when (newsHead.visibility) {
                    View.VISIBLE ->
                        newsHead.visibility = View.GONE
                    View.GONE -> {
                        newsHead.visibility = View.VISIBLE

                    }
                }
            }

            head1.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse((items[position].result.getJSONArray("items")[0] as JSONObject).getString("link")))
                context.startActivity(intent)
            }
            head2.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse((items[position].result.getJSONArray("items")[1] as JSONObject).getString("link")))
                context.startActivity(intent)
            }
            head3.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse((items[position].result.getJSONArray("items")[2] as JSONObject).getString("link")))
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.search_result_row, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val totalNum = items[position].result.getInt("total")
        holder.textView.text = items[position].word + " : " + totalNum + "건"

        if(totalNum == 0)
            holder.head1.visibility = View.GONE
        else
            holder.head1.text = (items[position].result.getJSONArray("items")[0] as JSONObject).getString("title")

        if(totalNum <= 1)
            holder.head2.visibility = View.GONE
        else
            holder.head2.text = (items[position].result.getJSONArray("items")[1] as JSONObject).getString("title")

        if(totalNum <= 2)
            holder.head3.visibility = View.GONE
        else
            holder.head3.text = (items[position].result.getJSONArray("items")[2] as JSONObject).getString("title")
    }
}
