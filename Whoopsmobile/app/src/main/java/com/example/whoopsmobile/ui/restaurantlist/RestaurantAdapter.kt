package com.example.whoopsmobile.ui.restaurantlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.whoopsmobile.BuildConfig
import com.example.whoopsmobile.R
import com.example.whoopsmobile.model.Restaurant

class RestaurantAdapter(
    private var items: List<Restaurant>,
    private val onRestaurantClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvRestaurantName)
        val image: ImageView = view.findViewById(R.id.ivRestaurant)

        init {
            view.setOnClickListener { if (bindingAdapterPosition != RecyclerView.NO_POSITION) onRestaurantClick(items[bindingAdapterPosition]) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val r = items[position]
        holder.name.text = r.name
        if (r.imageUrl != null && r.imageUrl.isNotBlank()) {
            holder.image.visibility = View.VISIBLE
            val fullUrl = if (r.imageUrl.startsWith("http")) r.imageUrl
                else "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/${r.imageUrl}"
            holder.image.load(fullUrl)
        } else {
            holder.image.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Restaurant>) {
        items = newItems
        notifyDataSetChanged()
    }
}
