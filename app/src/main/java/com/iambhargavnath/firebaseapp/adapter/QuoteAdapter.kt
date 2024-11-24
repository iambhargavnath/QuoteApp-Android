package com.iambhargavnath.firebaseapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iambhargavnath.firebaseapp.R
import com.iambhargavnath.firebaseapp.model.Quote

class QuoteAdapter(
    private val itemList: List<Quote>,
    private val onClick: (Quote?) -> Unit
) : RecyclerView.Adapter<QuoteAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val quoteFill: TextView = itemView.findViewById(R.id.quoteFill)
        val authorFill: TextView = itemView.findViewById(R.id.authorFill)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_student, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemList[position]
        holder.quoteFill.text = item.quote
        holder.authorFill.text = "~ ${item.author}"
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = itemList.size
}