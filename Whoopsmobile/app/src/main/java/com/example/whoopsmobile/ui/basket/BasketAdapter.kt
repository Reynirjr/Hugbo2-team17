package com.example.whoopsmobile.ui.basket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.R
import com.example.whoopsmobile.model.BasketItem

class BasketAdapter(
    private var items: List<BasketItem>,
    private val onQuantityChanged: (BasketItem, Int) -> Unit,
    private val onRemove: (BasketItem) -> Unit
) : RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

    inner class BasketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvBasketItemName)
        val quantity: TextView = view.findViewById(R.id.tvQuantity)
        val lineTotal: TextView = view.findViewById(R.id.tvLineTotal)
        val btnDecrease: ImageButton = view.findViewById(R.id.btnDecrease)
        val btnIncrease: ImageButton = view.findViewById(R.id.btnIncrease)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_basket, parent, false)
        return BasketViewHolder(view)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        val bi = items[position]
        holder.name.text = bi.item.name
        holder.quantity.text = bi.quantity.toString()
        holder.lineTotal.text = "${bi.lineTotal} ISK"
        holder.btnDecrease.setOnClickListener {
            if (bi.quantity > 1) onQuantityChanged(bi, bi.quantity - 1)
            else onRemove(bi)
        }
        holder.btnIncrease.setOnClickListener { onQuantityChanged(bi, bi.quantity + 1) }
        holder.btnRemove.setOnClickListener { onRemove(bi) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<BasketItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
