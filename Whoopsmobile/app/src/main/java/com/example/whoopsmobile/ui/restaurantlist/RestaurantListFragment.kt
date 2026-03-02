package com.example.whoopsmobile.ui.restaurantlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
import com.example.whoopsmobile.model.Restaurant

class RestaurantListFragment : Fragment() {

    private lateinit var adapter: RestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_restaurant_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvRestaurants)
        adapter = RestaurantAdapter(emptyList()) { restaurant ->
            (activity as? MainActivity)?.openMenu(restaurant.menuId)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        loadRestaurants()
    }

    private fun loadRestaurants() {
        Thread {
            val result = com.example.whoopsmobile.data.api.ApiHelper().getRestaurants()
            requireActivity().runOnUiThread {
                when (result) {
                    is com.example.whoopsmobile.data.api.ApiResult.Restaurants -> adapter.updateItems(result.restaurants)
                    else -> adapter.updateItems(com.example.whoopsmobile.data.api.ApiHelper.fallbackRestaurants())
                }
            }
        }.start()
    }
}
