package com.example.whoopsmobile.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.R
import com.example.whoopsmobile.model.BasketItem

class CheckoutAdapter(private var items: List<BasketItem>) : RecyclerView.Adapter<CheckoutAdapter.RowViewHolder>() {

    class RowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val line: TextView = view.findViewById(R.id.tvLine)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout_row, parent, false)
        return RowViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val bi = items[position]
        holder.name.text = "${bi.quantity} x ${bi.item.name}"
        holder.line.text = "${bi.lineTotal} ISK"
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<BasketItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
