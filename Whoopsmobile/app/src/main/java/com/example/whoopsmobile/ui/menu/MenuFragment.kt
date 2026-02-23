package com.example.whoopsmobile.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
import com.example.whoopsmobile.data.HagaMenuData
import com.example.whoopsmobile.data.api.ApiHelper
import com.example.whoopsmobile.data.api.ApiResult
import com.example.whoopsmobile.model.Item
import com.example.whoopsmobile.service.BasketService
import com.example.whoopsmobile.service.SessionManager
import com.google.android.material.chip.ChipGroup

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var emptyText: TextView
    private lateinit var filterChipGroup: ChipGroup
    private lateinit var basketBadge: TextView

    private var allItems: List<Item> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.rvItems)
        emptyText = view.findViewById(R.id.tvEmpty)
        filterChipGroup = view.findViewById(R.id.filterChipGroup)
        basketBadge = view.findViewById(R.id.basketBadge)
        val btnBasket: ImageButton = view.findViewById(R.id.btnBasket)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MenuAdapter(emptyList()) { item -> (activity as? MainActivity)?.openItemDetails(item.id) }
        recyclerView.adapter = adapter

        btnBasket.setOnClickListener { (activity as? MainActivity)?.openBasket() }

        filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            applyFilter(checkedIds.first())
        }

        loadItems()
    }

    override fun onResume() {
        super.onResume()
        updateBasketBadge()
    }

    private fun updateBasketBadge() {
        val count = BasketService.totalItemCount()
        if (count > 0) {
            basketBadge.visibility = View.VISIBLE
            basketBadge.text = if (count > 99) "99+" else count.toString()
        } else {
            basketBadge.visibility = View.GONE
        }
    }

    private fun applyFilter(checkedChipId: Int) {
        val filtered = when (checkedChipId) {
            R.id.chipVegetarian -> allItems.filter { it.hasTag("vegan") }
            R.id.chipMeat -> allItems.filter { it.hasTag("meat") }
            else -> allItems // chipAll or unknown
        }
        adapter.updateItems(filtered)
        // Show message when filter returns no items (e.g. tags missing in Supabase)
        if (filtered.isEmpty() && checkedChipId != R.id.chipAll) {
            emptyText.text = getString(R.string.no_items_match_filter)
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.GONE
        }
    }

    private fun loadItems() {

        Thread {

            val apiHelper = ApiHelper()
            val result = apiHelper.getItems(SessionManager.currentMenuId)

            requireActivity().runOnUiThread {

                when (result) {

                    is ApiResult.Success -> {
                        val withWait = result.items.count { it.estimatedWaitTimeMinutes != null && it.estimatedWaitTimeMinutes!! > 0 }
                        Log.d("MenuFragment", "API Success: ${result.items.size} items, $withWait with wait time")
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        allItems = result.items
                        BasketService.setCurrentMenuItems(result.items)
                        applyFilter(filterChipGroup.checkedChipId.let { if (it != View.NO_ID) it else R.id.chipAll })
                        updateBasketBadge()
                    }

                    is ApiResult.Empty -> {
                        Log.d("MenuFragment", "API Empty: using fallback HagaMenuData (has wait times)")
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        allItems = HagaMenuData.items
                        BasketService.setCurrentMenuItems(HagaMenuData.items)
                        applyFilter(filterChipGroup.checkedChipId.let { if (it != View.NO_ID) it else R.id.chipAll })
                        updateBasketBadge()
                    }

                    is ApiResult.Error -> {
                        recyclerView.visibility = View.GONE
                        emptyText.visibility = View.VISIBLE
                        emptyText.text = result.message
                    }
                }
            }

        }.start()
    }
}

private fun Item.hasTag(tag: String): Boolean =
    tags.split(",").map { it.trim() }.any { it.equals(tag, ignoreCase = true) }