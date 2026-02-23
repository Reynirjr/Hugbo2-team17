package com.example.whoopsmobile.ui.menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.R
import com.example.whoopsmobile.data.HagaMenuData
import com.example.whoopsmobile.data.api.ApiHelper
import com.example.whoopsmobile.data.api.ApiResult
import com.example.whoopsmobile.model.Item
import com.google.android.material.chip.ChipGroup

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var emptyText: TextView
    private lateinit var filterChipGroup: ChipGroup

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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MenuAdapter(emptyList())
        recyclerView.adapter = adapter

        filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            applyFilter(checkedIds.first())
        }

        loadItems()
    }

    private fun applyFilter(checkedChipId: Int) {
        val filtered = when (checkedChipId) {
            R.id.chipVegetarian -> allItems.filter { it.hasTag("vegan") }
            R.id.chipMeat -> allItems.filter { it.hasTag("meat") }
            else -> allItems // chipAll or unknown
        }
        adapter.updateItems(filtered)
    }

    private fun loadItems() {

        Thread {

            val apiHelper = ApiHelper()
            val result = apiHelper.getItems()

            requireActivity().runOnUiThread {

                when (result) {

                    is ApiResult.Success -> {
                        val withWait = result.items.count { it.estimatedWaitTimeMinutes != null && it.estimatedWaitTimeMinutes!! > 0 }
                        Log.d("MenuFragment", "API Success: ${result.items.size} items, $withWait with wait time")
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        allItems = result.items
                        applyFilter(filterChipGroup.checkedChipId.let { if (it != View.NO_ID) it else R.id.chipAll })
                    }

                    is ApiResult.Empty -> {
                        Log.d("MenuFragment", "API Empty: using fallback HagaMenuData (has wait times)")
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        allItems = HagaMenuData.items
                        applyFilter(filterChipGroup.checkedChipId.let { if (it != View.NO_ID) it else R.id.chipAll })
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