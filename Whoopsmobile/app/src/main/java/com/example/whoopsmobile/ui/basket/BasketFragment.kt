package com.example.whoopsmobile.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.R
import com.example.whoopsmobile.model.BasketItem
import com.example.whoopsmobile.service.BasketService

class BasketFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvBasketEmpty: TextView
    private lateinit var basketFooter: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var adapter: BasketAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_basket, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rvBasket)
        tvBasketEmpty = view.findViewById(R.id.tvBasketEmpty)
        basketFooter = view.findViewById(R.id.basketFooter)
        tvTotal = view.findViewById(R.id.tvTotal)
        btnCheckout = view.findViewById(R.id.btnCheckout)
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        adapter = BasketAdapter(
            items = BasketService.getBasket().items.toList(),
            onQuantityChanged = { basketItem, newQuantity ->
                BasketService.updateQuantity(basketItem, newQuantity)
                loadBasket()
            },
            onRemove = { basketItem ->
                BasketService.removeItem(basketItem)
                loadBasket()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnCheckout.setOnClickListener {
            if (BasketService.getBasket().items.isEmpty()) {
                Toast.makeText(requireContext(), R.string.basket_empty, Toast.LENGTH_SHORT).show()
            } else {
                (activity as? com.example.whoopsmobile.MainActivity)?.openCheckout()
            }
        }

        loadBasket()
    }

    override fun onResume() {
        super.onResume()
        loadBasket()
    }

    private fun loadBasket() {
        val items = BasketService.getBasket().items.toList()
        if (items.isEmpty()) {
            tvBasketEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            basketFooter.visibility = View.GONE
        } else {
            tvBasketEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            basketFooter.visibility = View.VISIBLE
            adapter.updateItems(items)
            val total = BasketService.calculateTotal()
            tvTotal.text = "$total ISK"
        }
    }
}
