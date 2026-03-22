package com.example.whoopsmobile.ui.menu

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
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

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var emptyText: TextView
    private lateinit var basketBarContainer: View
    private lateinit var basketBadge: TextView
    private lateinit var btnLastOrderStatus: TextView
    private lateinit var filterTabs: List<TextView>

    private var selectedTabId: Int = R.id.chipAll
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
        basketBarContainer = view.findViewById(R.id.basketBarContainer)
        basketBadge = view.findViewById(R.id.basketBadge)
        btnLastOrderStatus = view.findViewById(R.id.btnLastOrderStatus)

        filterTabs = listOf(
            view.findViewById(R.id.chipAll),
            view.findViewById(R.id.chipVegetarian),
            view.findViewById(R.id.chipSides),
            view.findViewById(R.id.chipDrinks)
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MenuAdapter(emptyList()) { item -> (activity as? MainActivity)?.openItemDetails(item.id) }
        recyclerView.adapter = adapter

        basketBarContainer.setOnClickListener { (activity as? MainActivity)?.openBasket() }
        btnLastOrderStatus.setOnClickListener {
            SessionManager.lastPlacedOrderId?.let { id ->
                (activity as? MainActivity)?.openOrderStatus(id)
            }
        }

        filterTabs.forEach { tab ->
            tab.setOnClickListener { selectTab(tab.id) }
        }

        selectTab(R.id.chipAll)
        loadItems()
    }

    override fun onResume() {
        super.onResume()
        updateBasketBadge()
        updateLastOrderStatusShortcut()
    }

    private fun updateLastOrderStatusShortcut() {
        val show = SessionManager.lastPlacedOrderId?.let { it > 0L } == true
        btnLastOrderStatus.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun selectTab(tabId: Int) {
        selectedTabId = tabId
        val selectedDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.filter_tab_selected)
        filterTabs.forEach { tab ->
            if (tab.id == tabId) {
                tab.background = selectedDrawable
                tab.setTypeface(null, Typeface.BOLD)
                tab.alpha = 1.0f
            } else {
                tab.background = null
                tab.setTypeface(null, Typeface.NORMAL)
                tab.alpha = 0.5f
            }
        }
        applyFilter(tabId)
    }

    private fun updateBasketBadge() {
        val count = BasketService.totalItemCount()
        if (count > 0) {
            basketBarContainer.visibility = View.VISIBLE
            basketBadge.visibility = View.VISIBLE
            basketBadge.text = if (count > 99) "99+" else count.toString()
        } else {
            basketBarContainer.visibility = View.GONE
            basketBadge.visibility = View.GONE
        }
    }

    private fun applyFilter(tabId: Int) {
        val filtered = when (tabId) {
            R.id.chipAll -> allItems.filter { it.hasTag("tilbod") }
            R.id.chipVegetarian -> allItems.filter { it.hasTag("burger") }
            R.id.chipSides -> allItems.filter { it.hasTag("sides") }
            R.id.chipDrinks -> allItems.filter { it.hasTag("drykkir") }
            else -> allItems
        }
        adapter.updateItems(filtered)
        if (filtered.isEmpty()) {
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
            apiHelper.getStoreQueueMinutes()?.let { SessionManager.restaurantQueueMinutes = it }

            requireActivity().runOnUiThread {

                when (result) {

                    is ApiResult.Success -> {
                        Log.d("MenuFragment", "API Success: ${result.items.size} items")
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        allItems = result.items
                        BasketService.setCurrentMenuItems(result.items)
                        applyFilter(selectedTabId)
                        updateBasketBadge()
                    }

                    is ApiResult.Empty -> {
                        Log.d("MenuFragment", "API Empty: using fallback HagaMenuData")
                        if (SessionManager.restaurantQueueMinutes == null) SessionManager.restaurantQueueMinutes = 20
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        allItems = HagaMenuData.items
                        BasketService.setCurrentMenuItems(HagaMenuData.items)
                        applyFilter(selectedTabId)
                        updateBasketBadge()
                    }

                    is ApiResult.Error -> {
                        recyclerView.visibility = View.GONE
                        emptyText.visibility = View.VISIBLE
                        emptyText.text = result.message
                    }

                    is ApiResult.Restaurants -> {
                        if (SessionManager.restaurantQueueMinutes == null) SessionManager.restaurantQueueMinutes = 20
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        allItems = HagaMenuData.items
                        BasketService.setCurrentMenuItems(HagaMenuData.items)
                        applyFilter(selectedTabId)
                        updateBasketBadge()
                    }
                }
            }

        }.start()
    }
}

private fun Item.hasTag(tag: String): Boolean =
    tags.split(",").map { it.trim() }.any { it.equals(tag, ignoreCase = true) }
