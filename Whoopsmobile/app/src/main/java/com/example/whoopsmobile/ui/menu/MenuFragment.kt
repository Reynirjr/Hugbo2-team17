package com.example.whoopsmobile.ui.menu

import android.os.Bundle
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

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var emptyText: TextView

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

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MenuAdapter(emptyList())
        recyclerView.adapter = adapter

        loadItems()
    }

    private fun loadItems() {

        Thread {

            val apiHelper = ApiHelper()
            val result = apiHelper.getItems()

            requireActivity().runOnUiThread {

                when (result) {

                    is ApiResult.Success -> {
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        adapter.updateItems(result.items)
                    }

                    is ApiResult.Empty -> {
                        recyclerView.visibility = View.VISIBLE
                        emptyText.visibility = View.GONE
                        adapter.updateItems(HagaMenuData.items)
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