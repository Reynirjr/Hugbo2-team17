package com.example.whoopsmobile.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.R
import com.example.whoopsmobile.data.api.ApiHelper

class MenuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.rvItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MenuAdapter(emptyList())
        recyclerView.adapter = adapter

        loadItems()
    }

    private fun loadItems() {

        Thread {

            val apiHelper = ApiHelper()
            val items = apiHelper.getItems()

            requireActivity().runOnUiThread {
                adapter.updateItems(items)
            }

        }.start()
    }
}
